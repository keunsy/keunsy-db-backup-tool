/**    
 * 文件名：RemoteDbConfig.java    
 *    
 * 版本信息：    
 * 日期：2015-12-14    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.auto.entity;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：RemoteDbConfig    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-14 上午11:56:03    
 * 修改人：keunsy
 * 修改时间：2015-12-14 上午11:56:03    
 * 修改备注：    
 * @version     
 *     
 */
public class RemoteDbConfig {

    private int id;//
    private String remote_ip;//远程服务器ip
    private int remote_port;//远程服务器端口
    private String remote_db;//远程服务器数据库名
    private String remote_last_bak_time;//该数据最后备份时间
    private String remote_bak_data_tables;//需要备份数据的数据表
    private int interval_time;//执行间隔

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemote_ip() {
        return remote_ip;
    }

    public void setRemote_ip(String remote_ip) {
        this.remote_ip = remote_ip;
    }

    public int getRemote_port() {
        return remote_port;
    }

    public void setRemote_port(int remote_port) {
        this.remote_port = remote_port;
    }

    public String getRemote_db() {
        return remote_db;
    }

    public void setRemote_db(String remote_db) {
        this.remote_db = remote_db;
    }

    public String getRemote_last_bak_time() {
        return remote_last_bak_time;
    }

    public void setRemote_last_bak_time(String remote_last_bak_time) {
        this.remote_last_bak_time = remote_last_bak_time;
    }

    public String getRemote_bak_data_tables() {
        return remote_bak_data_tables;
    }

    public void setRemote_bak_data_tables(String remote_bak_data_tables) {
        this.remote_bak_data_tables = remote_bak_data_tables;
    }

    public int getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(int interval_time) {
        this.interval_time = interval_time;
    }

    @Override
    public String toString() {
        return "RemoteDbConfig [id=" + id + ", remote_ip=" + remote_ip + ", remote_port=" + remote_port + ", remote_db=" + remote_db + ", remote_last_bak_time=" + remote_last_bak_time
                + ", remote_bak_data_tables=" + remote_bak_data_tables + ", interval_time=" + interval_time + "]";
    }

}
