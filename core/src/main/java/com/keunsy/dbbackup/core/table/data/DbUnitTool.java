/**    
 * 文件名：DbUnitTool.java    
 *    
 * 版本信息：    
 * 日期：2015-12-10    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.table.data;

/**    
 *     
 * 项目名称：DbBackupTool    
 * 类名称：DbUnitTool    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-10 下午1:48:10    
 * 修改人：keunsy
 * 修改时间：2015-12-10 下午1:48:10    
 * 修改备注：    
 * @version     
 *     
 */

import com.keunsy.dbbackup.core.utils.FileUtil;
import com.keunsy.dbbackup.core.utils.GeneralUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.xml.XmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.FileHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class DbUnitTool {

    /** 
     * 获取IDatabase连接
     * 
     * @method getIConnection         
     * @return void 
     * @throws Exception 
    */
    public static IDatabaseConnection getIConnection(String driverClass, String connectionUrl, String username, String password) throws Exception {

        IDatabaseTester databaseTester = new JdbcDatabaseTester(driverClass, connectionUrl, username, password);
        IDatabaseConnection iconn = databaseTester.getConnection();
        return iconn;
    }

    /** 
     * 从普通连接获取IDatabase连接
     * 
     * @method getIConnection         
     * @return void 
     * @throws Exception 
     */
    public static IDatabaseConnection getIConnection(Connection conn) throws Exception {

        return new DatabaseConnection(conn);
    }

    /**
     * 获取普通连接
     * @method getConnection         
     * @return Connection
     */
    public static Connection getConnection(String driverClass, String connectionUrl, String username, String password) {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(connectionUrl, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /** 
     * 从iconn获取普通连接
     * 
     * @method getIConnection         
     * @return void 
     * @throws SQLException 
     * @throws Exception 
     */
    public static Connection getConnection(IDatabaseConnection iconn) throws SQLException {
        return iconn.getConnection();
    }

    /**
     * 将itable中的数据转化为list形式
     * 
     * @method getDataFromTable         
     * @return List<Map<?,?>>
     */
    public static List<Map<?, ?>> getDataFromTable(ITable table) throws Exception {

        List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
        int count_table = table.getRowCount();
        if (count_table > 0) {
            Column[] columns = table.getTableMetaData().getColumns();
            for (int i = 0; i < count_table; i++) {
                Map<String, Object> map = new TreeMap<String, Object>();
                for (Column column : columns) {
                    map.put(column.getColumnName().toUpperCase(), table.getValue(i, column.getColumnName()));
                }
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 根据sql 获取table 数据 并转化为list形式
     * 
     * @method getTableDataFromSql         
     * @return List<Map<?,?>>
     */
    public static List<Map<?, ?>> getTableDataFromSql(IDatabaseConnection iconn, String tableName, String sql) throws Exception {
        ITable table = iconn.createQueryTable(tableName, sql);
        return getDataFromTable(table);
    }

    /**
     * 将数据以xml形式输出
     * 
     * @method printDataAsXml         
     * @return void
     */
    public static void printDataAsXml(IDatabaseConnection iconn, String tableName, String sql) throws Exception {
        List<Map<?, ?>> datas = getTableDataFromSql(iconn, tableName, sql);
        StringBuffer sb;
        for (Map<?, ?> data : datas) {
            sb = new StringBuffer();
            sb.append("<" + tableName.toUpperCase() + " ");
            for (Object o : data.keySet()) {
                sb.append(o + "=\"" + data.get(o) + "\" ");
            }
            sb.append("/>");
            System.out.println(sb.toString());
        }
    }

    /**
     * 导出数据到文件
     * 
     * @method exportData         
     * @return void
     */
    public static void exportData(IDatabaseConnection iconn, List<String> tableList, File destFile) throws SQLException, DatabaseUnitException, FileNotFoundException, IOException {

        QueryDataSet dataSet = null;
        try {
            dataSet = new QueryDataSet(iconn);
            for (String tableName : tableList) {
                dataSet.addTable(tableName);
            }
        } finally {
            exportData(dataSet, destFile);
        }
    }

    /**
     * 导出单表数据到文件
     * 
     * @method exportData         
     * @return void
     */
    public static void exportSingleData(IDatabaseConnection iconn, String tableName, File destFile) throws SQLException, DatabaseUnitException, FileNotFoundException, IOException {

        QueryDataSet dataSet = null;
        try {
            dataSet = new QueryDataSet(iconn);
            dataSet.addTable(tableName);
        } finally {
            exportData(dataSet, destFile);
        }
    }

    /**
     * 导出所有数据到文件
     * 
     * @method exportData         
     * @return void
     */
    public static void exportAllData(IDatabaseConnection iconn, File destFile) throws SQLException, DatabaseUnitException, FileNotFoundException, IOException {
        IDataSet dataSet = iconn.createDataSet();
        exportData(dataSet, destFile);
    }

    /**
     * 执行导出
     * 
     * @method exportData         
     * @return void
     */
    public static void exportData(IDataSet dataSet, File destFile) throws DataSetException, FileNotFoundException, IOException {

        if (dataSet != null) {
            //采用Flat时，可节约空间，但 存在数据导入bug
            //FlatXmlDataSet.write(dataSet, new FileWriter(destFile), "UTF8");
            XmlDataSet.write(dataSet, new FileWriter(destFile), "UTF8");
            LogUtil.EXPORT_LOG.info("table {} data had exported to {}", GeneralUtil.arrayToStrWithBlock(dataSet.getTableNames()), GeneralUtil.getBlock(destFile.getPath()));
        }
    }

    /** 
     * 获取dataset
     * 
     * @method getDataSetProducer         
     * @return void 
     * @throws MalformedURLException 
    */
    public static IDataSet getCachedDataSet(File file) throws MalformedURLException, DataSetException {

        return new CachedDataSet(new XmlProducer(FileHelper.createInputSource(file)));
    }

    /**
     * 导入数据(清除后追加数据模式)
     * 
     * @method importData         
     * @return void
     */
    public static void importDataByCleanInsert(IDatabaseConnection iconn, File file) throws DatabaseUnitException, IOException, SQLException {

        IDataSet dataSet = getCachedDataSet(file);
        DatabaseOperation operation = DatabaseOperation.CLEAN_INSERT;
        importData(iconn, dataSet, operation);
    }

    /**
     * 导入数据(非重复添加模式模式)
     * 
     * @method importData         
     * @return void
     */
    public static void importDataByRefresh(IDatabaseConnection iconn, File file) throws DatabaseUnitException, IOException, SQLException {

        IDataSet dataSet = getCachedDataSet(file);
        DatabaseOperation operation = DatabaseOperation.REFRESH;
        importData(iconn, dataSet, operation);
    }

    /**
     * 导入数据(追加数据模式)
     * 
     * @method importData         
     * @return void
     */
    public static void importDataByInsert(IDatabaseConnection iconn, File file) throws DatabaseUnitException, IOException, SQLException {

        IDataSet dataSet = getCachedDataSet(file);
        DatabaseOperation operation = DatabaseOperation.INSERT;
        importData(iconn, dataSet, operation);
    }

    /**
     * 导入数据
     * 
     * @method importData         
     * @return void
     */
    public static void importData(IDatabaseConnection databaseConnection, IDataSet dataSet, DatabaseOperation operation) throws DatabaseUnitException, IOException, SQLException {

        try {
            DatabaseOperation.TRANSACTION(operation);
            operation.execute(databaseConnection, dataSet);
        } finally {
            databaseConnection.close();
        }
    }

    public static void main(String[] args) {

        ResourceBundle rs = ResourceBundle.getBundle("config");

        String dir = rs.getString("filePath");
        String destFileStr = dir + "test_new_5.xml";

        List<String> tableList = new ArrayList<String>();
        tableList.add("user");
        tableList.add("test");

        IDatabaseConnection iconn;
        try {

            FileUtil.createFile(destFileStr);
            LogUtil.MAIN_LOG.info("test{}粉色", "====");
            iconn = getIConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/dbup?characterEncoding=UTF-8", "root", "root");
            //一般导出
            //printDataAsXml(iconn, "user", "select * from user");
            //exportData(iconn, tableList, destFile);
            //全部导出
            //exportAllData(iconn, destFile);
            //单表单文件导出
            for (String tableName : tableList) {
                String fileStr = dir + System.currentTimeMillis() + "_" + tableName + ".xml";
                File file = new File(fileStr);
                exportSingleData(iconn, tableName, file);
            }
            //导入
            //            if (destFile.exists()) {
            //                importDataByRefresh(iconn, destFile);
            //            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseUnitException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
