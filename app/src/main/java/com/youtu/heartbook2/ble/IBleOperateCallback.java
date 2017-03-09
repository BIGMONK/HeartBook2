package com.youtu.heartbook2.ble;

/**
 * Created by djf on 2017/3/6.
 */

public interface IBleOperateCallback {
    void bleData(short var1, short var2);

    void bleData(short var1, float[] var2);

    void bleData(short var1, short[] var2);
    void bleData(int var1, byte[] var2);
}