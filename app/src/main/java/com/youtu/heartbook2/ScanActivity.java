package com.youtu.heartbook2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScanActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView deviceList;
    private Button button;
    //	private BleOperateFunction mBleOperate;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    protected boolean isScanning;
    protected BluetoothAdapter mBluetoothAdapter;
    private String TAG = this.getClass().getSimpleName();
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            Log.d(TAG, "LeScanCallback:" + device.getName() + "" + device.getAddress());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bledevices leDevice = new Bledevices();
                    leDevice.device = device;
                    leDevice.singal = rssi;
//                	mlxh.add("" + rssi + "dbm");
                    mLeDeviceListAdapter.addDevice(leDevice);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    public boolean isBleSupport() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        if (mBluetoothAdapter == null) {
            return false;
        }

        return true;
    }

    public boolean checkBleIsOpen() {

        return mBluetoothAdapter.isEnabled();
    }

    @SuppressWarnings("deprecation")
    public void BleScanDevice(boolean enable) {
        if (enable) {
            isScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            isScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        deviceList = (ListView) findViewById(R.id.device_list);
        button = (Button) findViewById(R.id.button);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        deviceList.setAdapter(mLeDeviceListAdapter);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isScanning) {
                    BleScanDevice(false);
                    button.setText("Start scan");
                } else {
                    mLeDeviceListAdapter.clear();
                    BleScanDevice(true);
                    button.setText("Stop scan");
                }
            }
        });

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Bledevices bleDevice = mLeDeviceListAdapter.getDevice(position);

                if (bleDevice == null)
                    return;
                String add = bleDevice.device.getAddress();
                if (isScanning) {
                    BleScanDevice(false);
                }
//		        if (mBleOperate.bleConnect(bleDevice.device.getAddress())) {

                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                intent.putExtra("address", add);
                startActivity(intent);
//		        }
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!isBleSupport()) {
            Toast.makeText(this, "blue tooth not support", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!checkBleIsOpen()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            BleScanDevice(true);
            if (isScanning) {
                button.setText("Stop scan");
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            BleScanDevice(true);
            if (isScanning) {
                button.setText("Stop scan");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mLeDeviceListAdapter.clear();
    }


    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<Bledevices> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<Bledevices>();
            mInflator = ScanActivity.this.getLayoutInflater();
        }

        public void addDevice(Bledevices dev) {
            int i = 0;
            int listSize = mLeDevices.size();
            for (i = 0; i < listSize; i++) {
                if (mLeDevices.get(i).device.equals(dev.device)) {
                    mLeDevices.get(i).singal = dev.singal;
                    break;
                }
            }

            if (i >= listSize) {
                mLeDevices.add(dev);
            }

        }

        public Bledevices getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceSignal = (TextView) view.findViewById(R.id.signal);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Bledevices bleDevice = mLeDevices.get(i);
            final String deviceName = bleDevice.device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("Unknow device");
            }
            viewHolder.deviceAddress.setText(bleDevice.device.getAddress());
            viewHolder.deviceSignal.setText("" + bleDevice.singal + "dBm");
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceSignal;
    }

    public class Bledevices {
        BluetoothDevice device;
        int singal;
    }
}