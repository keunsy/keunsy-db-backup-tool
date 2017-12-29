/**    
 * 文件名：FileUtil.java    
 *    
 * 版本信息：    
 * 日期：2015-12-11    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**    
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：FileUtil    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-12-11 上午10:06:39    
 * 修改人：keunsy
 * 修改时间：2015-12-11 上午10:06:39    
 * 修改备注：    
 * @version     
 *     
 */
public class FileUtil {

    static final int BUF_SIZE = 32768;

    public static URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unexpected exception on file [" + file + "]", e);
        }
    }

    /**
     * 是否需要创建文件夹
     * 
     * @method isDirectoryCreationRequired         
     * @return boolean
     */
    public static boolean isDirectoryCreationRequired(File file) {

        return ((file != null) && (!(file.exists())));
    }

    /**
     * 创建丢失的文件夹(父文件夹不能存在)
     * 
     * @method createMissingDirectories         
     * @return boolean
     */
    public static boolean createMissingDirectories(File file)
    {
        if (file == null) {
            throw new IllegalStateException(file + " should not have a null parent");
        }
        if (file.exists()) {
            throw new IllegalStateException(file + " should not have existing parent directory");
        }
        return file.mkdirs();
    }

    public String resourceAsString(ClassLoader classLoader, String resourceName)
    {
        URL url = classLoader.getResource(resourceName);
        if (url == null) {
            LogUtil.logError("Failed to find resource [" + resourceName + "]", null);
            return null;
        }

        InputStreamReader isr = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            isr = new InputStreamReader(urlConnection.getInputStream());
            char[] buf = new char[128];
            StringBuilder builder = new StringBuilder();
            int count = -1;
            while ((count = isr.read(buf, 0, buf.length)) != -1) {
                builder.append(buf, 0, count);
            }
            String str = builder.toString();

            return str;
        } catch (IOException e)
        {
            LogUtil.logError("Failed to open " + resourceName, e);
        } finally {
            if (isr != null)
                try {
                    isr.close();
                } catch (IOException e)
                {
                }
        }
        return null;
    }

    /**
     * 拷贝文件
     * 
     * @method copy         
     * @return void
     */
    public static void copy(String src, String destination)
    {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] inbuf = new byte[32768];

            int n;
            while ((n = bis.read(inbuf)) != -1)
            {
                bos.write(inbuf, 0, n);
            }

            bis.close();
            bis = null;
            bos.close();
            bos = null;
        } catch (IOException ioe)
        {
        } finally
        {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e)
                {
                }
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e)
                {
                }
        }
    }

    /** 
     * 创建父文件夹
     * 
     * @method createFile         
     * @return void 
    */
    public static void createFile(String filePath) {

        File file = new File(filePath);
        if (isDirectoryCreationRequired(file)) {
            createMissingDirectories(file);
        }
    }

    /** 
     * 获取目录下所有文件
     * 
     * @method getAllFile         
     * @return Object 
    */
    public static List<String> getAllFile(String path, List<String> fileList, boolean isDepth) {

        File file = new File(path);
        File[] fileArray = file.listFiles();
        if (fileArray != null) {
            for (File tempFile : fileArray) {
                if (tempFile.isDirectory()) {//是否为文件夹
                    if (isDepth) {//是否遍历子目录
                        getAllFile(tempFile.getAbsolutePath(), fileList, isDepth);
                    }
                } else {
                    fileList.add(tempFile.getAbsolutePath());
                }
            }
        }

        return fileList;
    }

    /** 
     * 获取目录下所有文件并组装
     * 
     * @method getAllFile         
     * @return Object 
     */
    public static Map<String, String[]> getFileMap(String path, Map<String, String[]> map, boolean isDepth) {

        File file = new File(path);
        File[] fileArray = file.listFiles();
        if (fileArray != null) {
            for (File tempFile : fileArray) {
                if (tempFile.isDirectory()) {//是否为文件夹
                    if (isDepth) {//是否遍历子目录
                        getFileMap(tempFile.getAbsolutePath(), map, isDepth);
                    }
                } else {
                    String fileName = tempFile.getName();
                    String fileNamePre = tempFile.getName().substring(0, tempFile.getName().lastIndexOf("."));
                    if (map.get(fileNamePre) == null) {
                        map.put(fileNamePre, new String[2]);
                    }
                    if (fileName.endsWith(".sql")) {
                        map.get(fileNamePre)[0] = tempFile.getAbsolutePath();
                    } else if (fileName.endsWith(".xml")) {
                        map.get(fileNamePre)[1] = tempFile.getAbsolutePath();
                    }
                }
            }
        }

        return map;
    }

    /** 
     * 获取文件名称 去除后缀
     * 
     * @method getFileRealName         
     * @return void 
    */
    public static String getFileRealName(String filePath) {
        String fileRealName = "";
        if (StringUtils.isNotBlank(filePath) && filePath.contains(".") && filePath.contains("/")) {
            fileRealName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        }
        return fileRealName;
    }

    public static void main(String[] args) {
        // copy("D:/DbBackupTool/backup/test_new_5.xml", "D:/DbBackupTool/backup/test_new_5_copy.xml");
        //createFile("D:/DbBackupTool/backup5/");
        //createFile("D:/DbBackupTool/backup/localhost:3306/dbup/");
        //System.out.println(fileToURL(file).getPath());

        //        List<String> fList = getAllFile("D:\\DbBackupTool\\backup\\localhost-3306\\dbup\\20151216091310\\", new ArrayList<String>(), true);
        //        for (String string : fList) {
        //            System.out.println(string);
        //        }

        //        Map<String, List<String>> map = new HashMap<String, List<String>>();
        //
        //        getFileMap("D:\\DbBackupTool\\backup\\localhost-3306\\dbup", map, false);
        //        for (Map.Entry<String, List<String>> temp : map.entrySet()) {
        //            System.out.println(temp.getKey() + "==" + temp.getValue().get(0) + "==" + (temp.getValue().get(1) != null ? temp.getValue().get(1) : ""));
        //        }

        String fileString = "D:/DbBackupTool/backup/test_new_5.xml";
        System.out.println(getFileRealName(fileString));
    }
}
