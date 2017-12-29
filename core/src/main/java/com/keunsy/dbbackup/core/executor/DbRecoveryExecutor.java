/**    
 * 文件名：DbRecoveryExecutor.java    
 *    
 * 版本信息：    
 * 日期：2015-12-15    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.executor;

import com.keunsy.dbbackup.core.service.ResourceService;
import com.keunsy.dbbackup.core.table.data.DbUnitTool;
import com.keunsy.dbbackup.core.table.structure.ConsoleOperator;
import com.keunsy.dbbackup.core.utils.FileUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.IDatabaseConnection;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：DbRecoveryExecutor    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-15 上午9:36:26    
 * 修改人：keunsy
 * 修改时间：2015-12-15 上午9:36:26    
 * 修改备注：    
 * @version     
 *     
 */
public class DbRecoveryExecutor {

    /**
     * 执行导入
     * 
     * @method doRecovery         
     * @return void
     * @throws Exception 
     */
    public static boolean doRecovery(String ip, String port, String username, String password, String dbName, String strcPath, String dataPath, String directoryPath) throws Exception {

        IDatabaseConnection iconn = null;
        Map<String, String[]> map = new HashMap<String, String[]>();
        boolean result;

        try {
            if (StringUtils.isNotBlank(directoryPath)) {
                FileUtil.getFileMap(directoryPath, map, false);
            } else {
                String tableName = FileUtil.getFileRealName(strcPath);
                map.put(tableName, new String[2]);
                map.get(tableName)[0] = strcPath;
                map.get(tableName)[1] = dataPath;
            }

            //设置连接参数
            String driverClass = ResourceService.configPro.getProperty("jdbc.driverClass");//数据库驱动
            String connectionUrl = "jdbc:mysql://" + ip + ":" + (port == null ? 3306 : port) + "/" + dbName;
            //循环所有进行恢复
            for (Map.Entry<String, String[]> entry : map.entrySet()) {
                //导入表结构
                result = ConsoleOperator.executeStrucImportCmd(ip, username, password, dbName, entry.getValue()[0]);
                //导入表数据
                if (result) {
                    //必须重新连接，否则无法识别新建的表
                    iconn = DbUnitTool.getIConnection(driverClass, connectionUrl, username, password);
                    DbUnitTool.importDataByCleanInsert(iconn, new File(entry.getValue()[1]));
                    LogUtil.IMPORT_LOG.info("data from [{}] had imported to [{}]", entry.getValue()[0] + "," + entry.getValue()[1], ip + ":" + dbName);
                }
            }
            result = true;
        } finally {
            if (iconn != null) {
                try {
                    iconn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
