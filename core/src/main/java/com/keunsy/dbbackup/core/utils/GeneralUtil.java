/**    
 * 文件名：CommonUtil.java    
 *    
 * 版本信息：    
 * 日期：2015-9-22    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.keunsy.dbbackup.core.utils;

import org.apache.commons.lang3.StringUtils;

/**    
 *     
 * 项目名称：syncPlateDataTool    
 * 类名称：CommonUtil    
 * 类描述：    
 * 创建人：keunsy
 * 创建时间：2015-9-22 上午10:27:41    
 * 修改人：keunsy
 * 修改时间：2015-9-22 上午10:27:41    
 * 修改备注：    
 * @version     
 *     
 */
public class GeneralUtil {

    /**
     * 首字母大写
     * @method FirstUpperCase         
     * @return String
     */
    public static String FirstUpperCase(String str) {

        if (StringUtils.isNotBlank(str)) {
            str = str.trim();
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    /**
     * 简单比较是否非未来时间
     * @method isNotFutureTime         
     * @return boolean
     */
    public static boolean isNotFutureTime(String str) {

        boolean flag = true;
        if (StringUtils.isBlank(str)) {
            return false;
        }
        int result = str.compareTo(DateUtil.getDateStr24());
        if (result > 0) {
            flag = false;
        }
        return flag;
    }

    /** 
     * 数组转字符串
     * 
     * @method arrayToString         
     * @return void 
     */
    public static String arrayToString(Object[] array) {

        if (null == array || array.length == 0) {
            return "";
        }
        return StringUtils.join(array, ",");
    }

    /**
     * 字符包裹
     * 
     * @method getBlock         
     * @return String
     */
    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }

    /** 
     * 数组拆分包裹[]
     * 
     * @method logArrayWithBlock         
     * @return void 
    */
    public static String arrayToStrWithBlock(Object[] array) {

        return getBlock(arrayToString(array));

    }

    /** 
     * @method main         
     * @return void 
    */
    public static void main(String[] args) {
        System.out.println(isNotFutureTime("fsfe"));

    }

}
