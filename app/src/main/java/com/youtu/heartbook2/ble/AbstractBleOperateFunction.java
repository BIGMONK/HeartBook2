package com.youtu.heartbook2.ble;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.youtu.heartbook2.SmctConstant;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.youtu.heartbook2.SmctConstant.UUID_KEY_DATA_FFE2;
import static com.youtu.heartbook2.SmctConstant.UUID_KEY_READ_BLE_AREA;

/**
 * Created by djf on 2017/3/6.
 */

public abstract class AbstractBleOperateFunction {
    protected static AbstractBleOperateFunction instance;
    protected BluetoothAdapter.LeScanCallback bleScanCallback;
    protected BluetoothAdapter mBluetoothAdapter;
    protected Context mContext;
    protected boolean isScanning;
    protected BluetoothLeService mBluetoothLeService;
    protected EncryptDecode encryptDecode;
    protected IBleOperateCallback mBleOperateCallback;
    protected String address;
    private String TAG = "HeartBand" + this.getClass().getSimpleName();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            AbstractBleOperateFunction.this.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            AbstractBleOperateFunction.this.mBluetoothLeService.initialize();
            AbstractBleOperateFunction.this.mBluetoothLeService.setBleOperate(AbstractBleOperateFunction.this.mBleOperateCallback);
            if (!AbstractBleOperateFunction.this.bleConnect()) {
                AbstractBleOperateFunction.this.mBleOperateCallback.bleData(SmctConstant.KEY_BLE_CONNECT_STATE, SmctConstant.VALUE_BLE_DISCONNECTED);
                Log.d(TAG, "@#:ServiceConnection false");
            } else {
                Log.d(TAG, "@#:ServiceConnection true");
            }

        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "hulanlan onServiceDisconnected");
            AbstractBleOperateFunction.this.mBluetoothLeService = null;
        }
    };

    protected AbstractBleOperateFunction() {
    }

    protected AbstractBleOperateFunction(Context context) {
        this.mContext = context;
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = bluetoothManager.getAdapter();
        this.encryptDecode = new EncryptDecode();
    }

    private String getPackName() {
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List taskInfo = activityManager.getRunningTasks(1);
        ComponentName componentInfo = ((ActivityManager.RunningTaskInfo) taskInfo.get(0)).topActivity;
        return componentInfo.getPackageName();
    }

//    private void verifyPackageName() {
//        if(!this.getPackName().toString().equals("com.ut.vrautocyclingfamily") && !this.getPackName().toString().equals("com.ut.vrautoboatingfamily") && !this.getPackName().toString().equals("com.ut.vrautocycling") && !this.getPackName().toString().equals("com.km1930.dynamicbicycleclient")) {
//            throw new IllegalArgumentException("The package name is not reasonable.");
//        }
//    }

    public void addLeScanCallback(BluetoothAdapter.LeScanCallback callback) {
        this.bleScanCallback = callback;
    }

    public void addBleOperateCallback(IBleOperateCallback bleOperate) {
        this.mBleOperateCallback = bleOperate;
    }

    public void BindBleService(@NonNull String add) {
        this.address = add;
        if (this.mContext != null && this.mBluetoothLeService == null) {
            Log.d(TAG, "BindBleService bindService ");
            Intent gattServiceIntent = new Intent(this.mContext, BluetoothLeService.class);
            this.mContext.bindService(gattServiceIntent, this.mServiceConnection, 1);
        } else if (this.mContext != null && this.mBluetoothLeService != null && !this.bleConnect()) {
            Log.d(TAG, "BindBleService bleData ");
            this.mBleOperateCallback.bleData(SmctConstant.KEY_BLE_CONNECT_STATE, SmctConstant.VALUE_BLE_DISCONNECTED);
        }
    }

    public void UnBindBleService() {
        if (this.mServiceConnection != null) {
            Log.d(TAG, "BindBleService UnBindBleService ");
            this.mContext.unbindService(this.mServiceConnection);
        }

        this.mBluetoothLeService = null;
    }

    public void BleScanDevice(boolean enable) {
        if (enable) {
            this.isScanning = true;
            this.mBluetoothAdapter.startLeScan(this.bleScanCallback);
        } else {
            this.isScanning = false;
            this.mBluetoothAdapter.stopLeScan(this.bleScanCallback);
        }

    }

    public boolean isScanning() {
        return this.isScanning;
    }

    public void setScanning(boolean isScanning) {
        this.isScanning = isScanning;
    }

    public boolean isBleSupport() {
        return !this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le") ? false : this.mBluetoothAdapter != null;
    }

    protected boolean bleConnect() {
        Log.d(TAG, "bleConnect");
        return this.mBluetoothLeService != null ? this.mBluetoothLeService.connect(this.address) : false;
    }

    public void bleDisconnect() {
        if (this.mBluetoothLeService != null) {
            this.mBluetoothLeService.disconnect();
        }

    }

    public void bleClose() {
        if (this.mBluetoothLeService != null) {
            this.mBluetoothLeService.close();
        }

    }

    public boolean checkBleIsOpen() {
        return this.mBluetoothAdapter.isEnabled();
    }

    public void requestPowerLevel() {
        byte[] bytesn = new byte[20];
        bytesn[0] = -6;
        bytesn[1] = -6;
        bytesn[2] = 3;
        bytesn[3] = 3;
        if (this.mBluetoothLeService != null && this.mBluetoothLeService.writeDataToDevice(this.encryptDecode.Encrypt(bytesn))) {
            this.mBluetoothLeService.waitIdle(20);
        }

    }

    public int getBleServicedataIndex() {
        return this.mBluetoothLeService.pkgIndex;
    }

    public void bleGotoMeasureMode() {
        this.mBluetoothLeService.setCharacteristic(this.mBluetoothLeService.getSupportedGattServices(), UUID_KEY_DATA_FFE2);
    }

    public void bleGotoWriteData(byte[] b) {
        Log.d(TAG,"bleGotoWriteData");
        this.mBluetoothLeService.writeCharacteristic(this.mBluetoothLeService.getSupportedGattServices(), b);
    }

    public void bleGotoUpdateMode() {
        this.mBluetoothLeService.setCharacteristic(this.mBluetoothLeService.getSupportedGattServices(), UUID_KEY_READ_BLE_AREA);
        this.mBluetoothLeService.waitIdle(100);
    }

    public void BleSendCharacteristicIdentify(byte[] buf) {
        BluetoothGattCharacteristic mCharIdentify = (BluetoothGattCharacteristic) this.mBluetoothLeService.getOadGattService().getCharacteristics().get(0);
        mCharIdentify.setValue(buf);
        if (this.mBluetoothLeService.writeCharacteristic(mCharIdentify)) {
            this.mBluetoothLeService.waitIdle(20);
        }

    }

    public boolean BleSendCharacteristicValue(byte[] buf) {
        BluetoothGattCharacteristic mCharValue = (BluetoothGattCharacteristic) this.mBluetoothLeService.getOadGattService().getCharacteristics().get(1);
        mCharValue.setValue(buf);
        boolean success = this.mBluetoothLeService.writeCharacteristic(mCharValue);
        this.mBluetoothLeService.waitIdle(20);
        return success;
    }

    public boolean waitIdle(int time) {
        return this.mBluetoothLeService.waitIdle(time);
    }
}
