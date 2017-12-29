/**    
 * 文件名：DoBackup.java    
 *    
 * 版本信息：    
 * 日期：2015-12-14    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.main;

import com.keunsy.dbbackup.core.executor.DbRecoveryExecutor;
import com.keunsy.dbbackup.core.service.ResourceService;
import com.keunsy.dbbackup.core.utils.DateUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：DoBackup    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-14 下午3:58:08    
 * 修改人：keunsy
 * 修改时间：2015-12-14 下午3:58:08    
 * 修改备注：    
 * @version     
 *     
 */
public class RecoveryMain {

    /** 
     * @method main         
     * @return void 
    */
    public static void main(String[] args) {

        if (args != null && args.length < 8) {
            System.out.println("请检查参数是否正确，空值请填写为 null");
            LogUtil.MANUAL_LOG.info("recovery args length not correct!");
        } else {
            String ip = args[0];
            String port = args[1];
            String username = args[2];
            String password = args[3];
            String database = args[4];
            String strcPath = args[5].equalsIgnoreCase("null") ? null : args[5];
            String dataPath = args[6].equalsIgnoreCase("null") ? null : args[6];
            String directoryPath = args[7].equalsIgnoreCase("null") ? null : args[7];

            LogUtil.MANUAL_LOG.info("recovery args are [ip:{}],[port:{}],[username:{}],[password:{}],[database:{}],[strcPath:{}],[dataPath:{},[directoryPath:{}]",
                    new Object[] { ip, port, username, password, database, strcPath, dataPath, directoryPath });

            System.out.println(DateUtil.getDateStr24() + "  参数：[ip:" + ip + "],[port:" + port + "],[username:" + username + "],[password:" + password + "],[database:" + database
                    + "],[strcPath:" + strcPath + "],[dataPath:" + dataPath + "],[directoryPath:" + directoryPath + "]");
            try {
                ResourceService.loadConfigProperties();
                boolean result = DbRecoveryExecutor.doRecovery(ip, port, username, password, database, strcPath, dataPath, directoryPath);
                //                DbRecoveryExecutor.doRecovery("localhost", "3306", "root", "root", "dbup",
                //                        "D:/DbBackupTool/backup/localhost-3306/dbup/test-20151215135146.sql", "D:/DbBackupTool/backup/localhost-3306/dbup/test-20151215135146.xml",
                //                        "E:\\DbBackupTool\\backup\\localhost-3306\\dbup\\20151221165911");
                if (result) {
                    System.out.println(DateUtil.getDateStr24() + " 执行成功！");
                    LogUtil.MANUAL_LOG.info("excete success!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.ERROR_LOG.error(null, e);
                LogUtil.MANUAL_LOG.info("excete fail!");
                System.out.println(DateUtil.getDateStr24() + " 请检查参数是否正确！");
            }
        }

    }
}
