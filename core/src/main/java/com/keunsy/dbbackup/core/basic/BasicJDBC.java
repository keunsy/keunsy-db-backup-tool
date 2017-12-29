/**    
 * 文件名：Basic.java    
 *    
 * 版本信息：    
 * 日期：2015-12-14    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.basic;

import com.keunsy.dbbackup.core.entity.TableStatus;
import com.keunsy.dbbackup.core.table.data.DbUnitTool;
import com.keunsy.dbbackup.core.utils.GeneralUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：Basic    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-14 上午9:37:57    
 * 修改人：keunsy
 * 修改时间：2015-12-14 上午9:37:57    
 * 修改备注：    
 * @version     
 *     
 */
public class BasicJDBC {

    /**
     * 执行sql语句
     * 
     * @method execute         
     * @return int
     */
    public static int execute(Connection conn, String sql) {

        QueryRunner queryRunner = null;
        int result = 0;

        try {
            queryRunner = new QueryRunner();
            result = queryRunner.update(conn, sql);
        } catch (SQLException e) {
            LogUtil.logError(sql, e);
            e.printStackTrace();
        } finally {
            doFinally(conn, sql, null);
        }
        return result;
    }

    /**
     * 执行sql语句,带参数
     * 
     * @method execute         
     * @return int
     */
    public static int execute(Connection conn, String sql, Object[] params) {

        QueryRunner queryRunner = null;
        int result = 0;

        try {
            queryRunner = new QueryRunner();
            result = queryRunner.update(conn, sql, params);
        } catch (SQLException e) {
            LogUtil.logError(sql, e);
            e.printStackTrace();
        } finally {
            doFinally(conn, sql, params);
        }
        return result;
    }

    /** 
     * finally 操作
     * 
     * @method doFinally         
     * @return void 
    */
    private static void doFinally(Connection conn, String sql, Object[] params) {

        LogUtil.JDBC_LOG.info("sql:[{}];params:[{}]", sql, GeneralUtil.arrayToString(params));
        try {
            DbUtils.close(conn);
        } catch (SQLException e) {
        }
    }

    /**
     * 获取list数据
     * @param <T>
     * @method queryData         
     * @return List<T>
     */
    public static <T> List<T> queryList(Connection conn, String sql, Class<T> clz) {

        List<T> resultList = null;
        QueryRunner queryRunner = null;

        try {
            queryRunner = new QueryRunner();
            resultList = queryRunner.query(conn, sql, new BeanListHandler<T>(clz));

        } catch (SQLException e) {
            LogUtil.logError(sql, e);
            e.printStackTrace();
        } finally {
            doFinally(conn, sql, null);
        }
        return resultList;
    }

    /**
     * 获取list数据
     * @param <T>
     * @method queryData         
     * @return List<T>
     */
    public static <T> List<T> queryListNoCloseConn(Connection conn, String sql, Class<T> clz) {

        List<T> resultList = null;
        QueryRunner queryRunner = null;

        try {
            queryRunner = new QueryRunner();
            resultList = queryRunner.query(conn, sql, new BeanListHandler<T>(clz));

        } catch (SQLException e) {
            LogUtil.logError(sql, e);
            e.printStackTrace();
        } finally {
            LogUtil.JDBC_LOG.info(sql);
        }
        return resultList;
    }

    /**
     * 单个查询
     * 
     * @method query         
     * @return T
     */
    public static <T> T query(Connection conn, String sql, Class<T> clz) {

        T t = null;
        QueryRunner queryRunner = null;

        try {
            queryRunner = new QueryRunner();
            t = queryRunner.query(conn, sql, new BeanHandler<T>(clz));

        } catch (SQLException e) {
            LogUtil.logError(sql, e);
            e.printStackTrace();
        } finally {
            doFinally(conn, sql, null);
        }
        return t;
    }

    public static void main(String[] args) {
        Connection connection = DbUnitTool.getConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/dbup?characterEncoding=UTF-8", "root", "root");
        List<TableStatus> users = queryList(connection, "show table status from dbup", TableStatus.class);
        for (TableStatus user : users) {
            System.out.println(user.getRows());
        }
    }
}
