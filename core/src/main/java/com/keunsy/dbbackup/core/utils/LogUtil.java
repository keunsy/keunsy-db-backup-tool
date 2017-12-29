package com.keunsy.dbbackup.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

public class LogUtil {

    public static final Logger ERROR_LOG = LoggerFactory.getLogger("log-error");
    public static final Logger MAIN_LOG = LoggerFactory.getLogger("log-main");
    public static final Logger JDBC_LOG = LoggerFactory.getLogger("log-jdbc");
    public static final Logger MONITOR_LOG = LoggerFactory.getLogger("log-monitor");
    public static final Logger EXPORT_LOG = LoggerFactory.getLogger("log-export");
    public static final Logger IMPORT_LOG = LoggerFactory.getLogger("log-import");
    public static final Logger MANUAL_LOG = LoggerFactory.getLogger("log-manual");

    /**
     * 指定加载logback.xml 配置
     */
    static {
        try {
            String path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
            File file = new File(path + "config/logback.xml");
            if (file.exists()) {
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator jc = new JoranConfigurator();
                jc.setContext(lc);
                lc.reset();
                jc.doConfigure(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录异常错误
     * 格式 [exception]
     *
     * @param message
     * @param e
     */
    public static void logError(String message, Throwable e) {
        StringBuilder s = new StringBuilder();
        s.append(GeneralUtil.getBlock("exception"));
        s.append(GeneralUtil.getBlock(message));
        ERROR_LOG.error(s.toString(), e);
    }

    public static void main(String[] args) {

        MAIN_LOG.error("fsfefe");
        try {
            int a = 10 / 0;
        } catch (Exception e2) {
            logError(null, e2);
        }

    }
}
