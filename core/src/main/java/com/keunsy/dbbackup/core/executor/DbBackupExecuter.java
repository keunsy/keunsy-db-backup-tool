/**    
 * 文件名：Backuper.java    
 *    
 * 版本信息：    
 * 日期：2015-12-14    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.executor;

import com.keunsy.dbbackup.core.service.BackupRecoveryService;
import com.keunsy.dbbackup.core.service.ResourceService;
import com.keunsy.dbbackup.core.table.data.DbUnitTool;
import com.keunsy.dbbackup.core.table.structure.ConsoleOperator;
import com.keunsy.dbbackup.core.utils.LogUtil;

import org.dbunit.database.IDatabaseConnection;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：Backuper    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-14 上午11:50:36    
 * 修改人：keunsy
 * 修改时间：2015-12-14 上午11:50:36    
 * 修改备注：    
 * @version     
 *     
 */
public class DbBackupExecuter {

    /**
     * 数据备份，文件名称自动
     * 
     * @method doBackup         
     * @return void
     * @throws Exception 
     */
    public static void doBackup(String ip, String port, String username, String password, String dbName, String tableNames) throws Exception {
        doBackup(ip, port, username, password, dbName, tableNames, null);
    }

    /**
     * 进行数据备份
     * 
     * @method doBackup         
     * @return void
     * @throws Exception 
     */
    public static void doBackup(String ip, String port, String username, String password, String dbName, String tableNames, String filePath) throws Exception {

        IDatabaseConnection iconn = null;

        try {

            //获取连接
            String driverClass = ResourceService.configPro.getProperty("jdbc.driverClass");//数据库驱动
            String connectionUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dbName;
            iconn = DbUnitTool.getIConnection(driverClass, connectionUrl, username, password);
            //获取存储路径
            filePath = BackupRecoveryService.getFilePathPreAndCreate(filePath, ip, port, dbName);
            //获取限制数量
            int limitCount = Integer.parseInt(ResourceService.configPro.getProperty("backup.tableLimitCount"));//设定文件总路径
            //获取导出表
            List<String> tableList = BackupRecoveryService.getNeedExportTableList(iconn.getConnection(), dbName, tableNames, limitCount);

            boolean result = false;

            for (String table : tableList) {
                String finalFilePath = filePath + table;
                //导出表结构
                String sqlFilePath = finalFilePath + ".sql";
                result = ConsoleOperator.executeStrucExportCmd(ip, username, password, dbName, table, sqlFilePath);
                if (result) {
                    try {
                        //导出表数据
                        String xmlFilePath = finalFilePath + ".xml";
                        DbUnitTool.exportSingleData(iconn, table, new File(xmlFilePath));
                    } catch (Exception e) {
                        result = false;
                        LogUtil.ERROR_LOG.error("export table [{}] fail", table, e);
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            if (iconn != null) {
                try {
                    iconn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
