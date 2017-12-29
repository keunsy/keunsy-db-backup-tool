package com.keunsy.dbbackup.core.utils;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 
 *     
 * 项目名称：DbBackupTool-core    
 * 类名称：UnEscapeXmlUtil    
 * 类描述：    解析xml中的中文转化码为中文  如：&#26159;&#25171;&#21457;&#31532;&#19977;&#26041;&#37117;&#26159;
 * 创建人：keunsy
 * 创建时间：2015-12-21 下午4:29:24    
 * 修改人：keunsy
 * 修改时间：2015-12-21 下午4:29:24    
 * 修改备注：    
 * @version     
 *
 */
public class UnEscapeXmlUtil {

    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && !args[i].equals("")) {
                System.out.println(StringEscapeUtils.unescapeXml(args[i]));
            }
        }

    }

}
