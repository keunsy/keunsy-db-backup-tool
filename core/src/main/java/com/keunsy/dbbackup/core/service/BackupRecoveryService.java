/**    
 * 文件名：ExportService.java    
 *    
 * 版本信息：    
 * 日期：2015-12-15    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.service;

import com.keunsy.dbbackup.core.basic.BasicJDBC;
import com.keunsy.dbbackup.core.entity.TableStatus;
import com.keunsy.dbbackup.core.utils.DateUtil;
import com.keunsy.dbbackup.core.utils.FileUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：ExportService    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-15 上午10:08:21    
 * 修改人：keunsy
 * 修改时间：2015-12-15 上午10:08:21    
 * 修改备注：    
 * @version     
 *     
 */
public class BackupRecoveryService {

    /** 
     * 获取导出表
     * 
     * @param limitCount 
     * @method getNeedExportTableList         
     * @return List<String> 
    */
    public static List<String> getNeedExportTableList(Connection connection, String dbName, String tableNames, int limitCount) {
        //指定表
        String[] tableArray = null;
        if (StringUtils.isNotBlank(tableNames)) {
            tableArray = StringUtils.split(tableNames, ",");
        }

        //获取数量符合的所有库表
        List<String> tableList = new ArrayList<String>();
        String sql = getTableStatusSql(dbName, tableArray);
        List<TableStatus> tableStatuList = BasicJDBC.queryListNoCloseConn(connection, sql, TableStatus.class);
        if (tableStatuList != null && tableStatuList.size() > 0) {
            for (TableStatus tableStatus : tableStatuList) {
                if (tableStatus.getRows() <= limitCount) {
                    tableList.add(tableStatus.getName());
                } else {
                    LogUtil.MAIN_LOG.info("table [{}] count exceed the limit value [{}]", tableStatus.getName(), limitCount);
                }
            }
        }
        //交集
        List<String> finalList = new ArrayList<String>();
        if (tableArray != null && tableArray.length > 0 && tableList.size() > 0) {
            for (int i = 0; i < tableArray.length; i++) {
                for (String table : tableList) {
                    if (tableArray[i].equalsIgnoreCase(table)) {
                        finalList.add(table);
                    }
                }
            }
        } else {
            finalList = tableList;
        }
        return finalList;
    }

    /** 
     * 拼接获取表信息sql语句
     * 
     * @method getTableStatusSql         
     * @return String 
    */
    private static String getTableStatusSql(String dbName, String[] tableArray) {

        StringBuffer sb = new StringBuffer();
        sb.append("show table status from ").append(dbName);
        if (tableArray != null && tableArray.length > 0) {
            sb.append(" where Name in (");
            for (int i = 0, len = tableArray.length; i < len; i++) {
                sb.append("'").append(tableArray[i]).append("'");
                if (i != len - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    /** 
     * 
     * 
     * @method getFilePathPreAndCreate         
     * @return String 
    */
    public static String getFilePathPreAndCreate(String filePath, String ip, String port, String dbName) {

        //设定存储路径（用户输入或自动生成（默认格式：文件路径+ip-port+数据库+时间+表名.xml及表名.sql））
        if (StringUtils.isBlank(filePath)) {
            String filePathPre = ResourceService.configPro.getProperty("backup.filePathPre");//设定文件总路径
            if (StringUtils.isBlank(filePathPre)) {
                throw new RuntimeException("config filePre is null");
            }
            filePath = filePathPre + ip + "-" + port;//服务器路径区分路径 不能有冒号，否则无法创建文件夹
            filePath += File.separator + dbName + File.separator;//数据库路径
            String bakTime = DateUtil.getDateStr(new Date(), "yyyyMMddHHmmss");//备份时间
            filePath += bakTime + File.separator;//时间路径
        }
        if (StringUtils.isNotBlank(filePath)) {
            //防止文件不存在
            FileUtil.createFile(filePath);
        } else {
            throw new RuntimeException("file is null");
        }
        return filePath;
    }
}
