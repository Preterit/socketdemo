// ISocket.aidl
package com.xiangxue.socketdemo;

// Declare any non-default types here with import statements

interface ISocket {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean sendMessage(String message);

}
