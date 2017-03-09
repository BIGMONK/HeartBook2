package com.youtu.heartbook2.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.youtu.heartbook2.SmctConstant;

import java.util.Iterator;
import java.util.List;

import static com.youtu.heartbook2.SmctConstant.CLIENT_CHARACTERISTIC_CONFIG;
import static com.youtu.heartbook2.SmctConstant.SERVIE_UUID;
import static com.youtu.heartbook2.SmctConstant.UUID_KEY_DATA_FFE2;
import static com.youtu.heartbook2.SmctConstant.UUID_KEY_DATA_FFE3;

/**
 * Created by djf on 2017/3/6.
 */

@SuppressLint({"NewApi"})
public class BluetoothLeService extends Service {
    private final String TAG = "HeartBand" + BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private volatile boolean mBusy = false;
    private IBleOperateCallback mBleOperateCallback;
    private IUpdateCallBack mUpdateCallback;
    public static final String ACTION_GATT_CONNECTED = "com.heartbook.smct.ble.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.heartbook.smct.ble.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.heartbook.smct.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "com.heartbook.smct.ble.ACTION_DATA_AVAILABLE";
    public static final String EXTRA_DATA = "com.heartbook.smct.ble.EXTRA_DATA";
    public static final int GATT_WRITE_TIMEOUT = 100;
    public int pkgIndex = 1;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "BluetoothGattCallback onConnectionStateChange status=" + status + "    newState=" + newState);
            if (BluetoothLeService.this.mBleOperateCallback != null) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    BluetoothLeService.this.pkgIndex = 1;
                    BluetoothLeService.this.mBleOperateCallback.bleData(SmctConstant.KEY_BLE_CONNECT_STATE, SmctConstant.VALUE_BLE_CONNECTED);
                    BluetoothLeService.this.mBluetoothGatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    BluetoothLeService.this.mBleOperateCallback.bleData(SmctConstant.KEY_BLE_CONNECT_STATE, SmctConstant.VALUE_BLE_DISCONNECTED);
                }

            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "BluetoothGattCallback onServicesDiscovered status=" + status);
            if (status == 0 && BluetoothLeService.this.mBleOperateCallback != null) {
                BluetoothLeService.this.mBleOperateCallback.bleData(SmctConstant.KEY_BLE_CONNECT_STATE, SmctConstant.VALUE_BLE_SERVICE_DISCOVERED);
            }

        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "BluetoothGattCallback onCharacteristicRead status=" + status);

            if (status == 0) {
                BluetoothLeService.this.broadcastUpdate("com.heartbook.smct.ble.ACTION_DATA_AVAILABLE", characteristic);
            }

            BluetoothLeService.this.mBusy = false;
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "BluetoothGattCallback onCharacteristicChanged ");
            BluetoothLeService.this.broadcastUpdate("com.heartbook.smct.ble.ACTION_DATA_AVAILABLE", characteristic);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "BluetoothGattCallback onCharacteristicWrite ");
            BluetoothLeService.this.mBusy = false;
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "BluetoothGattCallback onDescriptorRead ");
            BluetoothLeService.this.mBusy = false;
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "BluetoothGattCallback onDescriptorWrite ");
            BluetoothLeService.this.mBusy = false;
        }
    };
    private final IBinder mBinder = new BluetoothLeService.LocalBinder();

    public BluetoothLeService() {
    }

    public void setBleOperate(IBleOperateCallback mBleOperate) {
        this.mBleOperateCallback = mBleOperate;
    }

    public void setBleUpdate(IUpdateCallBack mBleUpdate) {
        this.mUpdateCallback = mBleUpdate;
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        if (UUID_KEY_DATA_FFE2.equals(characteristic.getUuid().toString()) && data != null) {
            HandleBleData.HandleData(data, this.mBleOperateCallback, (IUpdateCallBack) null);
            this.mBusy = false;
        }

    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        this.close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        Log.d(TAG, "initialize");
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                return false;
            }
        }

        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        return this.mBluetoothAdapter != null;
    }

    public boolean connect(String address) {
        Log.d(this.TAG, "connect:" + address);
        if (this.mBluetoothAdapter != null && address != null) {
            if (this.mBluetoothDeviceAddress != null && address.equals(this.mBluetoothDeviceAddress) && this.mBluetoothGatt != null) {
                Log.d(this.TAG, "Trying to use an existing mBluetoothGatt for connection.");
                if (this.mBluetoothGatt.connect()) {
                    Log.d(this.TAG, "mBluetoothGatt true");
                    return true;
                } else {
                    Log.d(this.TAG, "mBluetoothGatt false");
                    return false;
                }
            } else {
                BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
                if (device == null) {
                    Log.e(this.TAG, "Device not found. Unable to connect.");
                    return false;
                } else {
                    this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
                    Log.d(this.TAG, "Trying to create a new connection.");
                    this.mBluetoothDeviceAddress = address;
                    return true;
                }
            }
        } else {
            Log.d(this.TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
    }

    public void disconnect() {
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public synchronized boolean writeDataToDevice(byte[] bb) {
        boolean status = true;
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            BluetoothGattService mGattService = this.mBluetoothGatt.getService(SERVIE_UUID);
            if (mGattService == null) {
                return false;
            } else {
                BluetoothGattCharacteristic gattChar = null;
                gattChar = mGattService.getCharacteristic(UUID_KEY_DATA_FFE3);
                if (gattChar == null) {
                    return false;
                } else {
                    gattChar.setValue(bb);
                    gattChar.setWriteType(1);
                    this.mBusy = true;
                    status = this.mBluetoothGatt.writeCharacteristic(gattChar);
                    if (!status) {
                        this.waitIdle(100);
                        status = this.mBluetoothGatt.writeCharacteristic(gattChar);
                    }

                    if (status) {
                        ++this.pkgIndex;
                        if (this.pkgIndex > 127) {
                            this.pkgIndex = 1;
                        }
                    }

                    return status;
                }
            }
        } else {
            return false;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBusy = true;
            this.mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        Log.d(TAG, "setCharacteristicNotification characteristic.getUuid=" + characteristic.getUuid() + "   " + enable);
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            boolean ok = false;
            if (this.mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
                BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                if (clientConfig != null) {
                    if (enable) {
                        ok = clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                        Log.d(TAG, "setCharacteristicNotification characteristic.getUuid=" + characteristic.getUuid()
                                + "clientConfig.getUuid=" + clientConfig.getUuid());
                    } else {
                        ok = clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }

                    if (ok) {
                        this.mBusy = true;
                        ok = this.mBluetoothGatt.writeDescriptor(clientConfig);
                    }
                }
            }

            return ok;
        } else {
            return false;
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        return this.mBluetoothGatt == null ? null : this.mBluetoothGatt.getServices();
    }

    public void setCharacteristic(List<BluetoothGattService> gattServices, String uuid) {
        Log.d(TAG, "setCharacteristic " + uuid);
        if (gattServices != null) {
            Iterator var4 = gattServices.iterator();
            while (var4.hasNext()) {
                BluetoothGattService gattService = (BluetoothGattService) var4.next();
                List gattCharacteristics = gattService.getCharacteristics();
                Iterator var7 = gattCharacteristics.iterator();
                while (var7.hasNext()) {
                    BluetoothGattCharacteristic gattCharacteristic = (BluetoothGattCharacteristic) var7.next();
                    Log.d(TAG, "setCharacteristic gattService =" + gattService.getUuid()
                            + "gattCharacteristic =" + gattCharacteristic.getUuid());
                    if (gattCharacteristic.getUuid().toString().equals(uuid)) {
                        int charaProp = gattCharacteristic.getProperties();
                        Log.d(TAG, "gattCharacteristic.getUuid=" + gattCharacteristic.getUuid() + "  charaProp=" + charaProp);
                        if ((charaProp | 2) > 0) {
                            if (this.mNotifyCharacteristic != null) {
                                this.setCharacteristicNotification(this.mNotifyCharacteristic, false);
                                this.mNotifyCharacteristic = null;
                            }
                            this.readCharacteristic(gattCharacteristic);
                        }

                        if ((charaProp | 16) > 0) {
                            this.mNotifyCharacteristic = gattCharacteristic;
                            this.setCharacteristicNotification(gattCharacteristic, true);
                        }
                    }
                }
            }

        }
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte b) {
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            byte[] val = new byte[]{b};
            characteristic.setValue(val);
            this.mBusy = true;
            return this.mBluetoothGatt.writeCharacteristic(characteristic);
        } else {
            return false;
        }
    }

    public boolean writeCharacteristic(List<BluetoothGattService> gattServices, byte[] b) {
        Log.d(TAG, "writeCharacteristic");
        for (int i = 0; i < gattServices.size(); i++) {
            Log.d(TAG, "writeCharacteristic gattServices uuid=" + gattServices.get(i).getUuid());
            List<BluetoothGattCharacteristic> gattCharacteristics = gattServices.get(i).getCharacteristics();
            for (int j = 0; j < gattCharacteristics.size(); j++) {
                Log.d(TAG, "writeCharacteristic gattServices uuid=" + gattServices.get(i).getUuid()
                        + "gattCharacteristics uuid=" + gattCharacteristics.get(j).getUuid().toString());
                if (gattCharacteristics.get(j).getUuid().equals(UUID_KEY_DATA_FFE3)) {
                    int charaProp = gattCharacteristics.get(j).getProperties();
                    Log.d(TAG, "writeCharacteristic gattCharacteristic charaProp=" + charaProp);
                    if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
                        gattCharacteristics.get(j).setValue(b);
                        Log.d(TAG, "writeCharacteristic gattCharacteristic.setValue(b)");
                        this.mBusy = true;
                        return this.mBluetoothGatt.writeCharacteristic(gattCharacteristics.get(j));
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;

    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.mBusy = true;
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public BluetoothGattService getOadGattService() {
        List gattServices = this.getSupportedGattServices();

        for (int i = 0; i < gattServices.size(); ++i) {
            BluetoothGattService srv = (BluetoothGattService) gattServices.get(i);
            if (srv.getUuid().toString().equals("f000ffc0-0451-4000-b000-000000000000")) {
                return srv;
            }
        }

        return null;
    }

    public BluetoothGattService getDeviceInfoGattService() {
        List gattServices = this.getSupportedGattServices();

        for (int i = 0; i < gattServices.size(); ++i) {
            BluetoothGattService srv = (BluetoothGattService) gattServices.get(i);
            if (srv.getUuid().toString().equals("0000180a-0000-1000-8000-00805f9b34fb")) {
                return srv;
            }
        }

        return null;
    }

    public boolean waitIdle(int timeout) {
        timeout /= 10;

        while (true) {
            --timeout;
            if (timeout <= 0 || !this.mBusy) {
                return timeout > 0;
            }

            try {
                Thread.sleep(10L);
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }
        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}

