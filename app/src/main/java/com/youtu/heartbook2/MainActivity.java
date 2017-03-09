package com.youtu.heartbook2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youtu.heartbook2.ble.BleOperateFunction;
import com.youtu.heartbook2.ble.IBleOperateCallback;

public class MainActivity extends Activity implements View.OnClickListener, IBleOperateCallback {
    private Button btnConn;
    private TextView tvEcgData, tvHeartRate, tvPowerLevel;
    private String address;
    private ProgressDialog mProgressDialog;
    private BleOperateFunction mBleOperate;
    private String TAG = "HeartBand" + this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initDatas();
        address = getIntent().getStringExtra("address");
//        address = "98:7B:F3:C4:D5:7E";
//        address = "D3:CB:8F:A3:20:BA";

        this.btnConn.setOnClickListener(this);
        mBleOperate = BleOperateFunction.getInstance(getApplicationContext());
        mBleOperate.addBleOperateCallback(this);
        connBle();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    private void initView() {
        this.btnConn = (Button) this.findViewById(R.id.btn_ble_conn);
        this.tvEcgData = (TextView) this.findViewById(R.id.tv_ecg_data);
        this.tvHeartRate = (TextView) this.findViewById(R.id.tv_heart_rate);
        this.tvPowerLevel = (TextView) this.findViewById(R.id.power_level);
    }

    private void initDatas() {
        tvEcgData.setText(formatString(R.string.ecg_data, 0));
        tvHeartRate.setText(formatString(R.string.heart_rate, 0));
        tvPowerLevel.setText(formatString(R.string.power_level, 0));
    }

    /**
     * 连接蓝牙
     */
    private void connBle() {
        mBleOperate.BindBleService(address);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("蓝牙连接中……");
        mProgressDialog.show();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_ble_conn:// 连接蓝牙
                connBle();
                break;
            default:
                break;
        }
    }

    /**
     * 格式化字符
     */
    private String formatString(int id, int data) {
        return String.format(getResources().getString(id), data);
    }


    private void disDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mBleOperate != null) {
            mBleOperate.bleDisconnect();
            mBleOperate.UnBindBleService();
        }
    }

    int reConnectTimes = 1;

    @Override
    public void bleData(final short key, final short value) {
        Log.d(TAG, "bleData  key=" + key + "   value=" + value);
        // TODO Auto-generated method stub
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                switch (key) {
                    case SmctConstant.KEY_ECG_DATA://  26  实时ECG数据
                        tvEcgData.setText(formatString(R.string.ecg_data, value));
                        break;
                    case SmctConstant.KEY_BLE_CONNECT_STATE://   10   蓝牙连接状态
                        disDialog();
                        if (value == SmctConstant.VALUE_BLE_CONNECTED) {//   0 连接
                            Toast.makeText(MainActivity.this, "蓝牙连接成功！！！", Toast.LENGTH_SHORT).show();
                            btnConn.setEnabled(false);
                        } else if (value == SmctConstant.VALUE_BLE_DISCONNECTED) {// 1  断开
//                            mBleOperate.bleClose();

                            mBleOperate.bleConnect();

                            mBleOperate.BindBleService(address);

                            btnConn.setEnabled(true);
                            Toast.makeText(MainActivity.this, "蓝牙连接失败！！！", Toast.LENGTH_SHORT).show();
                        } else if (value == SmctConstant.VALUE_BLE_SERVICE_DISCOVERED) {// 2   发现服务
                            Toast.makeText(MainActivity.this, "发现蓝牙服务！！！", Toast.LENGTH_SHORT).show();
                            btnConn.setEnabled(false);
                            //进入测量模式，接收ffe2的数据
                            mBleOperate.bleGotoMeasureMode();

                        } else if (value == SmctConstant.VALUE_BLE_DATA_AVAILABLE) {// 3  发现数据
                            Toast.makeText(MainActivity.this, "开始接收数据！！！", Toast.LENGTH_SHORT).show();
                        }

                        break;
//					case SmctConstant.KEY_DEVICE_ELECTRODE_DROP:
//						Toast.makeText(MainActivity.this, "电极脱落！！！", 2000).show();
//						break;
                    case SmctConstant.KEY_DEVICE_POWER_LEVEL://12
                        tvPowerLevel.setText(formatString(R.string.power_level, value));
                        break;
                    case SmctConstant.KEY_HEARTRATE_FROM_DEVICE://49
                        tvHeartRate.setText(formatString(R.string.heart_rate, value));
                        break;
//					case SmctConstant.KEY_BODY_POSE:
//						break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void bleData(short key, float[] value) {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder();
        if (value == null) {
            return;
        }
        for (int i = 0; i < value.length; i++) {
            stringBuilder.append(value[i] + "    ");
        }
        Log.d(TAG, "bleData  key=" + key + "   float[] value =" + stringBuilder.toString());

    }

    @Override
    public void bleData(short key, short[] value) {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder();
        if (value == null) {
            return;
        }
        for (int i = 0; i < value.length; i++) {
            stringBuilder.append(value[i] + "    ");
        }
        Log.d(TAG, "bleData  key=" + key + "  short[] value =" + stringBuilder.toString());

    }

    private boolean isFirstReceived;

    @Override
    public void bleData(int var1, final byte[] value) {
        StringBuilder stringBuilder = new StringBuilder();
        if (value == null) {
            return;
        }
        for (int i = 0; i < value.length; i++) {
            stringBuilder.append(value[i] + "    ");
        }
        Log.d(TAG, "bleData  var1=" + var1 + "  byte[] value =" + stringBuilder.toString());
        switch (var1) {
            case SmctConstant.VALUE_BLE_DATA_NOTIFY:
                if (!isFirstReceived) {
                    isFirstReceived = true;
                    byte[] bytes = new byte[]{(byte) -85, (byte) 0, (byte) 4, (byte) -1, (byte) 49, (byte) 0x0a, (byte) 1};
                    mBleOperate.bleGotoWriteData(bytes);
                } else if (value.length == 8) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvHeartRate.setText(formatString(R.string.heart_rate, value[6] < 0 ? 256 + value[6] : value[6]));
                        }
                    });
                }
                break;
        }

    }

}
