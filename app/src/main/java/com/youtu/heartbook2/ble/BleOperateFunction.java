package com.youtu.heartbook2.ble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by djf on 2017/3/6.
 */

public class BleOperateFunction extends AbstractBleOperateFunction {
    private String TAG = "HeartBand"+this.getClass().getSimpleName();

    public BleOperateFunction() {
    }

    public BleOperateFunction(Context mContext) {
        super(mContext);
    }

    public static synchronized BleOperateFunction getInstance(Context context) {
        if (instance == null) {
            instance = new BleOperateFunction(context);
        }
        Log.d("BleOperateFunction","getInstance");
        return (BleOperateFunction) instance;
    }

    public void BindBleService(@NonNull String address) {
        Log.i(TAG, "@#:BindBleService");
        super.BindBleService(address);
    }

    public boolean bleConnect() {
        Log.d(TAG, "@#:bleConnect2");
        return this.mBluetoothLeService != null ? this.mBluetoothLeService.connect(this.address) : false;
    }
}
