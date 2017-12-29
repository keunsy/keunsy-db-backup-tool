/**    
 * 文件名：AutoBackupService.java    
 *    
 * 版本信息：    
 * 日期：2015-12-17    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.auto.service;

import com.keunsy.dbbackup.auto.entity.RemoteDbConfig;
import com.keunsy.dbbackup.core.basic.BasicJDBC;
import com.keunsy.dbbackup.core.entity.TableStatus;
import com.keunsy.dbbackup.core.service.ResourceService;
import com.keunsy.dbbackup.core.table.data.DbUnitTool;

import java.sql.Connection;
import java.util.List;

/**    
 *     
 * 项目名称：DbBackupTool-auto    
 * 类名称：AutoBackupService    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-17 下午3:00:13    
 * 修改人：keunsy
 * 修改时间：2015-12-17 下午3:00:13    
 * 修改备注：    
 * @version     
 *     
 */
public class AutoBackupService {

    private static AutoBackupService autoBackupService = new AutoBackupService();

    public static AutoBackupService getInstance() {
        return autoBackupService;
    }

    //本地数据库连接参数
    public String driverClass;
    public String url;
    public String username;
    public String password;
    public String remote_username;
    public String remote_password;

    public String getDriverClass() {
        return driverClass;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRemote_username() {
        return remote_username;
    }

    public String getRemote_password() {
        return remote_password;
    }

    /** 
     * 获取数据库配置
     * 
     * @method getJdbcConfig         
     * @return void 
    */
    public void getJdbcConfig() {
        driverClass = ResourceService.configPro.getProperty("jdbc.driverClass");
        url = ResourceService.configPro.getProperty("jdbc.url");
        username = ResourceService.configPro.getProperty("jdbc.username");
        password = ResourceService.configPro.getProperty("jdbc.password");
        remote_username = ResourceService.configPro.getProperty("jdbc.remote.username");
        remote_password = ResourceService.configPro.getProperty("jdbc.remote.password");
    }

    /**
     * 获取数据配置 
     * 
     * @method getRemoteDbConfigList         
     * @return List<RemoteDbConfig> 
    */
    public List<RemoteDbConfig> getRemoteDbConfigList() {

        Connection conn = getChangedConn();
        String sql = "select id, remote_ip, remote_port, remote_db, remote_last_bak_time, remote_bak_data_tables from remote_db_config where status = 0 ";

        return BasicJDBC.queryList(conn, sql, RemoteDbConfig.class);
    }

    /**
     * 获取数据配置 
     * 
     * @method getRemoteDbConfigList         
     * @return List<RemoteDbConfig> 
     */
    public List<RemoteDbConfig> getRemoteDbSpecConfigList() {

        Connection conn = getChangedConn();
        String sql = "select id, remote_ip, remote_port, remote_db, remote_last_bak_time, remote_bak_data_tables,interval_time from remote_db_spec_config where status = 0 ";

        return BasicJDBC.queryList(conn, sql, RemoteDbConfig.class);
    }

    /**
     * 获取变更的本地连接
     * 
     * @method getChangedConn         
     * @return Connection
     */
    public Connection getChangedConn() {

        getJdbcConfig();
        return DbUnitTool.getConnection(driverClass, url, username, password);
    }

    /** 
     * 更新最后备份时间
     * 
     * @method uptBackupTime         
     * @return void 
    */
    public int uptBackupTime(String backupTime, String tableName) {

        Connection conn = getChangedConn();
        String sql = "update " + tableName + " set  remote_last_bak_time=?";
        return BasicJDBC.execute(conn, sql, new String[] { backupTime });
    }

    /** 
     * 获取本地某表状态
     * 
     * @method getLocalTableStatus         
     * @return TableStatus 
    */
    public TableStatus getLocalTableStatus(String sql) {
        getJdbcConfig();
        return BasicJDBC.query(getChangedConn(), sql, TableStatus.class);
    }

    /** 
     * 拼接任务名称
     * 
     * @method getTaskName         
     * @return String 
    */
    public String getTaskName(RemoteDbConfig cfg) {

        return cfg.getRemote_ip() + "-" + cfg.getRemote_port() + "-" + cfg.getRemote_db() + "-" + cfg.getRemote_bak_data_tables() + "-" + cfg.getInterval_time();

    }
}
