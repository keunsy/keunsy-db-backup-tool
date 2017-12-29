/**    
 * 文件名：ResourceService.java    
 *    
 * 版本信息：    
 * 日期：2015-12-16    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：ResourceService    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-16 上午10:18:36    
 * 修改人：keunsy
 * 修改时间：2015-12-16 上午10:18:36    
 * 修改备注：    
 * @version     
 *     
 */
public class ResourceService {

    public static Properties configPro = new Properties();

    /**
     * 加载配置
     * 
     * @method loadProperties         
     * @return void
     */
    public static void loadProperties(Properties p, String name) {
        InputStream ins = null;
        try {
            String path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
            File file = new File(path + name);
            ins = new FileInputStream(file);
            p.load(ins);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ins.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 加载config配置文件配置
     * 
     * @method loadConfigProperties         
     * @return void
     */
    public static void loadConfigProperties() {
        loadProperties(configPro, "config/config.properties");
    }

    public static void main(String[] args) {
        loadConfigProperties();
        System.out.println(configPro.get("backup.filePathPre"));
    }
}
