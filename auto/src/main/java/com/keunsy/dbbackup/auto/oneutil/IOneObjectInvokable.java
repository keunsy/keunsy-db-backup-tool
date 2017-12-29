package com.keunsy.dbbackup.auto.oneutil;

/**
 * 单启动程序调用接口
 * 
 */
public interface IOneObjectInvokable {

    /**
     * 回调接口，当程序启动的时的启动接口
     */
    void start();

    /**
     * 回调接口，当程序停止时要调用的接口
     */
    void stop();
}
