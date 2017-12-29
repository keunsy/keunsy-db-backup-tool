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

import com.keunsy.dbbackup.core.executor.DbBackupExecuter;
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
public class BackupMain {

    /** 
     * 
     * 
     * @method main         
     * @return void 
    */
    public static void main(String[] args) {
        //        args = new String[7];
        //        args[0] = "192.168.60.103";
        //        args[1] = "3306";
        //        args[2] = "root";
        //        args[3] = "hstest2014";
        //        args[4] = "phone_recharge";
        //        args[5] = "price_list";
        //        args[6] = "/keunsy/20160226/";

        if (args != null && args.length < 7) {
            System.out.println("请检查参数是否正确，空值请填写为 null");
            LogUtil.MANUAL_LOG.info("backup args length not correct!");
        } else {
            String ip = args[0];
            String port = args[1];
            String username = args[2];
            String password = args[3];
            String database = args[4];
            String tables = args[5].equalsIgnoreCase("null") ? null : args[5];
            String filePath = args[6].equalsIgnoreCase("null") ? null : args[6];

            LogUtil.MANUAL_LOG.info("backup args are [ip:{}],[port:{}],[username:{}],[password:{}],[database:{}],[tables:{}],[filePath:{}]",
                    new Object[] { ip, port, username, password, database, tables, filePath });
            System.out.println(DateUtil.getDateStr24() + "  参数：[ip:" + ip + "],[port:" + port + "],[username:" + username + "],[password:" + password + "],[database:" + database
                    + "],[tables:" + tables + "],[filePath:" + filePath + "]");
            try {
                if (ip.contains(",")) {//多个ip中间有逗号，即便ip格式错误也可以连接数据库
                    throw new RuntimeException();
                }
                ResourceService.loadConfigProperties();
                DbBackupExecuter.doBackup(ip, port, username, password, database, tables, filePath);
                // DbBackupExecuter.doBackup("localhost", "3306", "root", "root", "dbup", "role,test", "");
                System.out.println(DateUtil.getDateStr24() + "  执行成功！");
                LogUtil.MANUAL_LOG.info("excete success!");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.ERROR_LOG.error(null, e);
                LogUtil.MANUAL_LOG.info("excete fail!");
                System.out.println(DateUtil.getDateStr24() + "请检查参数是否正确！");
            }
        }
    }
}
