package com.keunsy.dbbackup.auto.thread.control;

public interface Stoppable {
  /**
   * 在线程或服务关闭时候需要执行的操作
   */
  boolean doStop();

}
