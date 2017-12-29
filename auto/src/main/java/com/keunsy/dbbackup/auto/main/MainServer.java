package com.keunsy.dbbackup.auto.main;

import com.keunsy.dbbackup.auto.entity.RemoteDbConfig;
import com.keunsy.dbbackup.auto.oneutil.IOneObjectInvokable;
import com.keunsy.dbbackup.auto.oneutil.OneObjectUtil;
import com.keunsy.dbbackup.auto.service.AutoBackupService;
import com.keunsy.dbbackup.auto.thread.BackupTask;
import com.keunsy.dbbackup.auto.thread.control.TempLock;
import com.keunsy.dbbackup.core.service.ResourceService;
import com.keunsy.dbbackup.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer implements IOneObjectInvokable {

    //特殊表配置线程
    private Map<String, BackupTask> specThreadMap;
    //控制特殊表配置
    private Map<Integer, String> specCfgMap;
    //线程池，控制一般表
    private final ExecutorService executorPool = Executors.newFixedThreadPool(5);
    //控制运行情况
    private boolean isRunnable = true;
    //锁控制
    private final Map<String, TempLock> lockMap = new HashMap<String, TempLock>();

    // 程序入口
    public static void main(String[] args) {
        OneObjectUtil.listen(new MainServer());
    }

    public void start() {

        LogUtil.MONITOR_LOG.info("------> 程序启动  <------");
        try {
            //加载配置文件
            ResourceService.loadConfigProperties();
            //业务获取
            AutoBackupService autoBackupService = AutoBackupService.getInstance();
            //循环记录时间标识
            long last_check_time = 0L;
            long last_run_time = 0L;

            specCfgMap = new HashMap<Integer, String>();
            specThreadMap = new HashMap<String, BackupTask>();

            while (isRunnable) {
                try {
                    //检查参数间隔
                    if (System.currentTimeMillis() - last_check_time > 300000) {
                        //特殊表（自定义备份间隔）
                        checkSpecToUpdate(autoBackupService);
                        //配置文件重加载
                        ResourceService.loadConfigProperties();
                        last_check_time = System.currentTimeMillis();
                    }
                    //一般配置表
                    if (System.currentTimeMillis() - last_run_time > Integer.parseInt(ResourceService.configPro.getProperty("backup.interval", "300000"))) {
                        doBackup(autoBackupService);
                        last_run_time = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    LogUtil.ERROR_LOG.error("", e);
                    e.printStackTrace();
                }
                noExceptionSleep(5000);
            }

        } catch (Exception e) {
            LogUtil.ERROR_LOG.error("", e);
            e.printStackTrace();
        }
        LogUtil.MONITOR_LOG.info("------> 程序中止  <------");

    }

    /** 
     * 检查特殊配置表是否发生了数据变化(数据删除或添加，对更新无效)，并执行相关操作
     * 
     * @method checkSpecToUpdate         
     * @return void 
    */
    private void checkSpecToUpdate(AutoBackupService autoBackupService) {

        List<RemoteDbConfig> finalList = new ArrayList<RemoteDbConfig>();
        List<RemoteDbConfig> dbConfigList = autoBackupService.getRemoteDbSpecConfigList();

        if (dbConfigList != null) {
            //删除数据检查
            Set<Integer> specSet = specCfgMap.keySet();
            for (int specCfgMapKey : specSet) {
                boolean flag = false;
                for (RemoteDbConfig cfg : dbConfigList) {
                    if (cfg.getId() == specCfgMapKey) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {//进行了删除，则停止、移除线程
                    stopSpecThread(specCfgMap.get(specCfgMapKey));
                    specCfgMap.remove(specCfgMapKey);
                }
            }
            //更新，修改检查
            for (RemoteDbConfig cfg : dbConfigList) {
                int key = cfg.getId();
                String new_value = autoBackupService.getTaskName(cfg);
                String old_value = specCfgMap.get(cfg.getId());
                //sn相同，配置发生改变，则停止线程
                if (old_value == null) {
                    finalList.add(cfg);
                } else if (!old_value.equals(new_value)) { //sn相同，配置发生改变，则停止原有线程
                    LogUtil.MAIN_LOG.info("remote_db_spec_config was changed !");
                    stopSpecThread(specCfgMap.get(key));
                    finalList.add(cfg);
                }
                specCfgMap.put(key, new_value);
            }

        }
        //进行备份
        doSpecBackup(autoBackupService, finalList);

        //检查特殊表是否更新
        //        TableStatus tableStatus = autoBackupService.getLocalTableStatus(CHECK_SPEC_TABLE_SQL);
        //        if (specTableStatus != null && tableStatus != null && !specTableStatus.getUpdate_time().equals(specTableStatus.getUpdate_time())) {
        //            //时间不相等说明数据进行了更新,停止已有特殊配置线程,重新设置线程
        //            LogUtil.MAIN_LOG.info("remote_db_spec_config tableStatus was changed !");
        //            stopSpec();
        //            doSpecBackup(autoBackupService);
        //        } else if (specTableStatus == null) {
        //            doSpecBackup(autoBackupService);
        //        }
        //        specTableStatus = tableStatus;
    }

    /** 
     * 特殊表备份
     * 
     * @method doSpecBackup         
     * @return void 
    */
    private void doSpecBackup(AutoBackupService autoBackupService, List<RemoteDbConfig> dbConfigList) {

        if (dbConfigList != null && dbConfigList.size() > 0) {
            for (RemoteDbConfig cfg : dbConfigList) {
                //加锁，防止同线程重复执行(降低数据库tcp连接请求)
                String key = cfg.getRemote_ip() + cfg.getRemote_db();
                if (lockMap.get(key) == null) {
                    lockMap.put(key, new TempLock());
                }
                String taskName = autoBackupService.getTaskName(cfg);
                BackupTask task = new BackupTask(cfg, cfg.getInterval_time(), lockMap.get(key), autoBackupService, taskName);
                Thread t = new Thread(task);
                t.start();
                specThreadMap.put(taskName, task);
            }
        }

    }

    /** 
     * 常规性备份
     * 
     * @method doBackup         
     * @return void 
    */
    private void doBackup(AutoBackupService autoBackupService) {
        //加载配置信息
        List<RemoteDbConfig> dbConfigList = autoBackupService.getRemoteDbConfigList();
        if (dbConfigList != null && dbConfigList.size() > 0) {
            for (RemoteDbConfig cfg : dbConfigList) {
                //加锁，防止同线程重复执行
                String key = cfg.getRemote_ip() + cfg.getRemote_db();
                if (lockMap.get(key) == null) {
                    lockMap.put(key, new TempLock());
                }
                String taskName = autoBackupService.getTaskName(cfg);
                executorPool.execute(new BackupTask(cfg, 0, lockMap.get(key), autoBackupService, taskName));
            }
        }

    }

    /**
     * 停止单一特殊表线程
     */
    public void stopSpecThread(String key) {

        try {
            if (specThreadMap.get(key) != null) {
                specThreadMap.get(key).doStop();
                specThreadMap.remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止特殊表线程
     */
    public void stopAllSpecThread() {

        try {
            if (specThreadMap != null) {
                for (BackupTask task : specThreadMap.values()) {
                    task.doStop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止
     */
    public void stop() {

        isRunnable = false;
        try {
            stopAllSpecThread();
            executorPool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 休眠
     * @method noExceptionSleep         
     * @return void
     */
    public void noExceptionSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}
