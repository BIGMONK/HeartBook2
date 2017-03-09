package com.youtu.heartbook2.ble;

/**
 * Created by djf on 2017/3/6.
 */

public interface IUpdateCallBack {
    void updateMsg(short var1, short var2);

    void updateVersionMsg(short var1, String var2);
}