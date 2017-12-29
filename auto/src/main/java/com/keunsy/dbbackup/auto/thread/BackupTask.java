/**    
 * 文件名：BackupThread.java    
 *    
 * 版本信息：    
 * 日期：2015-12-17    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.auto.thread;

import com.keunsy.dbbackup.auto.entity.RemoteDbConfig;
import com.keunsy.dbbackup.auto.service.AutoBackupService;
import com.keunsy.dbbackup.auto.thread.control.Stoppable;
import com.keunsy.dbbackup.auto.thread.control.TempLock;
import com.keunsy.dbbackup.core.executor.DbBackupExecuter;
import com.keunsy.dbbackup.core.utils.DateUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

/**    
 *     
 * 项目名称：DbBackupTool-auto    
 * 类名称：BackupThread    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-17 下午2:38:42    
 * 修改人：keunsy
 * 修改时间：2015-12-17 下午2:38:42    
 * 修改备注：    
 * @version     
 *     
 */
public class BackupTask implements Runnable, Stoppable {

    private boolean isRunnable = true;//控制运行情况

    private final RemoteDbConfig dbConfig;//连接
    private final int sleepTime;//连接
    private final TempLock tempLock;//对象锁
    private final AutoBackupService autoBackupService;//业务类
    private final String name;//任务名称

    /**    
     * 创建一个新的实例 BackupTask
     *    
     * @param remoteDbConfig
     * @param sleepTime    
     */
    public BackupTask(RemoteDbConfig dbConfig, int sleepTime, TempLock tempLock, AutoBackupService autoBackupService, String name) {
        super();
        this.dbConfig = dbConfig;
        this.sleepTime = sleepTime;
        this.tempLock = tempLock;
        this.autoBackupService = autoBackupService;
        this.name = name;
    }

    @Override
    public void run() {

        LogUtil.MONITOR_LOG.info("Task [{}] Start !interval time=[{}]", name, sleepTime);
        long time = System.currentTimeMillis();
        while (isRunnable) {
            try {
                synchronized (tempLock) {//防止同线程反复执行
                    //执行备份
                    DbBackupExecuter.doBackup(dbConfig.getRemote_ip(), String.valueOf(dbConfig.getRemote_port()), autoBackupService.getRemote_username(), autoBackupService.getRemote_password(),
                            dbConfig.getRemote_db(),
                            dbConfig.getRemote_bak_data_tables());
                }
                String backupTime = DateUtil.getDateStr24();
                //是否需要执行循环（普通表备份 还是特殊表备份）
                if (sleepTime != 0) {//特殊备份，不加入线程池
                    //执行更新备份时间
                    autoBackupService.uptBackupTime(backupTime, "remote_db_spec_config");
                } else {//一般表
                    autoBackupService.uptBackupTime(backupTime, "remote_db_config");
                    isRunnable = false;
                }
            } catch (Exception e) {
                LogUtil.ERROR_LOG.error(null, e);
                e.printStackTrace();
                if (sleepTime == 0) {
                    isRunnable = false;
                }
            } finally {
                noExceptionSleep(sleepTime);
            }
        }
        LogUtil.MONITOR_LOG.info("Task [{}] stop ! Run Time = [{}ms]", name, (System.currentTimeMillis() - time));
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

    /**
     * 关闭线程
     */
    @Override
    public boolean doStop() {
        this.isRunnable = false;
        return true;
    }

    public String getName() {
        return name;
    }

}
