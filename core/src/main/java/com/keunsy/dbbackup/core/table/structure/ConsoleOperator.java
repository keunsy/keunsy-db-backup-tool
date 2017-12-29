/**    
 * 文件名：CommandOperate.java    
 *    
 * 版本信息：    
 * 日期：2015-12-11    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.table.structure;

import com.keunsy.dbbackup.core.utils.GeneralUtil;
import com.keunsy.dbbackup.core.utils.LogUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：CommandOperate    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-11 上午11:34:28    
 * 修改人：keunsy
 * 修改时间：2015-12-11 上午11:34:28    
 * 修改备注：    
 * @version     
 *     
 */
public class ConsoleOperator {

    /**
     * 执行命令
     * 
     * @method executeCommand         
     * @return int
     */
    public static boolean executeCommand(String cmd) {

        boolean result = false;

        if (StringUtils.isBlank(cmd)) {
            return result;
        }
        String[] exeCmd = new String[] { "sh", "-c", cmd };
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOW") > -1) {
            exeCmd[0] = "cmd.exe";
            exeCmd[1] = "/c";
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(exeCmd);
            int waitForRst = process.waitFor();
            if (waitForRst == 0) {//只有返回值为0，方为成功执行
                result = true;
                LogUtil.MAIN_LOG.info("excute command [{}] success", cmd);
            } else {
                LogUtil.MAIN_LOG.info("excute command [{}] fail,reason for: [{}]", cmd, waitForRst);
            }
        } catch (Exception e) {
            LogUtil.logError(null, e);
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
            } catch (Exception e) {
            }
        }
        return result;

    }

    /**
     * 执行多命令（mysql仅限于文件与数据库在同一个服务器上）
     * 
     * @method executeCommand         
     * @return int
     */
    public static boolean executeCommand(String[] cmds) {

        boolean result = false;

        String[] exeCmd = new String[] { "sh", "-c", cmds[0] };
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOW") > -1) {
            exeCmd[0] = "cmd.exe";
            exeCmd[1] = "/c";
        }

        StringBuilder sbBuilder = new StringBuilder();
        for (int i = 1, len = exeCmd.length; i < len; i++) {
            sbBuilder.append(cmds[i]);
            if (i != len - 1) {
                sbBuilder.append("\r\n");
            }
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        OutputStream os = null;
        OutputStreamWriter writer = null;

        try {
            //e：  执行了第一条命令以后已经登录到mysql了，所以之后就是利用mysql的命令窗口
            process = runtime.exec(exeCmd);
            //进程执行后面的代码
            os = process.getOutputStream();
            writer = new OutputStreamWriter(os);
            //写入命令
            writer.write(sbBuilder.toString());

            LogUtil.IMPORT_LOG.info("excute command [{}] success", GeneralUtil.arrayToString(cmds));
        } catch (Exception e) {
            LogUtil.logError(null, e);
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
                writer.flush();
                writer.close();
                os.close();
            } catch (Exception e) {
            }
        }
        return result;

    }

    /** 
     * 获取表结构导出命令
     * 
     * @method getStrucExportCmd         
     * @return void 
    */
    public static String getStrucExportCmd(String ip, String username, String password, String dbName, String tableName, String filePath) {
        StringBuilder sBuilder = new StringBuilder();
        //sBuilder.append("C:\\Program Files (x86)\\MySQL\\MySQL Server 5.0\\bin\\");
        sBuilder.append("mysqldump ").append(" -h ").append(ip).append(" -u").append(username).append(" -p").append(password).append(" -d ").append(dbName);
        if (StringUtils.isNotBlank(tableName)) {
            sBuilder.append(" ").append(tableName);
        }
        sBuilder.append("  >  ").append(filePath);

        return sBuilder.toString();
    }

    /**
     * 执行导出表结构
     * 
     * @method executeStrucExportCmd         
     * @return boolean
     */
    public static boolean executeStrucExportCmd(String ip, String username, String password, String dbName, String tableName, String filePath) {

        String cmd = getStrucExportCmd(ip, username, password, dbName, tableName, filePath);

        return executeCommand(cmd);
    }

    /**
     * 获取导入命令组（不登录数据库的方法）
     * 
     * @method getStrucImportCmd         
     * @return String[]
     */
    public static String getStrucImportCmd(String ip, String username, String password, String dbName, String filePath) {

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("mysql ").append(" -h ").append(ip).append(" -u").append(username).append(" -p").append(password).append(" ").append(dbName);
        sBuilder.append("  <  ").append(filePath);

        return sBuilder.toString();
    }

    /**
     *  执行导入命令 不登陆
     * 
     * @method executeStrucImportCmdByLogin         
     * @return boolean
     */
    public static boolean executeStrucImportCmd(String ip, String username, String password, String dbName, String filePath) {

        String command = getStrucImportCmd(ip, username, password, dbName, filePath);

        return executeCommand(command);
    }

    /**
     * 获取导入命令组（登录数据库的方法）
     * 
     * @method getStrucImportCmd         
     * @return String[]
     */
    public static String[] getStrucImportCmdByLogin(String ip, String username, String password, String dbName, String filePath) {

        //获取登录命令语句
        String loginCommand = new StringBuilder().append("mysql -u").append(username).append(" -p").append(password).append(" -h").append(ip)
                .toString();
        //获取切换数据库到目标数据库的命令语句
        String switchCommand = new StringBuilder("use ").append(dbName).toString();
        //获取导入的命令语句
        String importCommand = new StringBuilder("source ").append(filePath).toString();
        //需要返回的命令语句数组
        String[] commands = new String[] { loginCommand, switchCommand, importCommand };

        return commands;
    }

    /**
     * 执行导入命令，由于需要导入文件，因此仅限于本地服务器
     * 
     * @method executeStrucImportCmd         
     * @return boolean
     */
    public static boolean executeStrucImportCmdByLogin(String host, String username, String password, String dbName, String filePath) {

        String[] commands = getStrucImportCmdByLogin(host, username, password, dbName, filePath);

        return executeCommand(commands);
    }

    public static void main(String[] args) {
        //String cmd = getStrucExportCmd("localhost", "root", "root", "dbup", "test", "D:/DbBackupTool/backup/testtest1.sql");
        //String cmd = getStrucExportCmd("127.0.0.1", "root", "root", "dbup", "test", "D:/DbBackupTool/backup/localhost-3306/dbup/role-20151214163547.sql");
        //String cmd = getStrucExportCmd("192.168.60.101", "root", "hstest2014", "super_plate", "admin_user", "D:/DbBackupTool/backup/testtest.sql");
        //        System.out.println(cmd);
        //        System.out.println(executeCommand(cmd));

        executeStrucImportCmd("localhost", "root", "root", "dbup", "D:/DbBackupTool/backup/localhost-3306/dbup/test-20151214164311.sql");

    }
}
