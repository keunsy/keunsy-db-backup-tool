/**    
 * 文件名：TableStatus.java    
 *    
 * 版本信息：    
 * 日期：2015-12-14    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.entity;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：TableStatus    
 * 类描述：    获取数据库表所有状态
 * 创建人：keunsy
 * 创建时间：2015-12-14 上午9:39:57    
 * 修改人：keunsy
 * 修改时间：2015-12-14 上午9:39:57    
 * 修改备注：    
 * @version     
 *     
 */
public class TableStatus {

    private String name;//表名
    private String create_table_sql;//建表语句
    private long rows;//数据行数
    private long data_length;//数据长度
    private long avg_data_length;//行平均数据长度
    private String engine;//使用存储引擎
    private String update_time;//更新时间
    private String create_time;//创建时间

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_table_sql() {
        return create_table_sql;
    }

    public void setCreate_table_sql(String create_table_sql) {
        this.create_table_sql = create_table_sql;
    }

    public long getRows() {
        return rows;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }

    public long getData_length() {
        return data_length;
    }

    public void setData_length(long data_length) {
        this.data_length = data_length;
    }

    public long getAvg_data_length() {
        return avg_data_length;
    }

    public void setAvg_data_length(long avg_data_length) {
        this.avg_data_length = avg_data_length;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "TableStatus [name=" + name + ", create_table_sql=" + create_table_sql + ", rows=" + rows + ", data_length=" + data_length + ", avg_data_length=" + avg_data_length + ", engine="
                + engine + ", update_time=" + update_time + ", create_time=" + create_time + "]";
    }

}
