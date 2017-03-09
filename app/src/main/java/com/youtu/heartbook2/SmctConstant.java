package com.youtu.heartbook2;

/**
 * Created by djf on 2017/3/6.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.UUID;

public abstract class SmctConstant {
    public static final short KEY_ALGO_HEART_RATE = 0;
    public static final short KEY_ALGO_RESP_RATE = 1;
    public static final short KEY_ALGO_HRV = 2;
    public static final short KEY_ALGO_ARRHYTHMIA = 3;
    public static final short KEY_ALGO_PREMATURE_BEAT = 5;
    public static final short KEY_ALGO_MISSED_BEAT = 6;
    public static final short KEY_ALGO_RR_INTERVAL = 7;
    public static final short KEY_ALGO_QRS_INTERVAL = 8;
    public static final short KEY_ALGO_QT_INTERVAL = 9;
    public static final short KEY_ALGO_P_INTERVAL = 10;
    public static final short KEY_ALGO_PR_INTERVAL = 11;
    public static final short KEY_ALGO_T_TYPE = 12;
    public static final short KEY_ALGO_P_TYPE = 13;
    public static final short KEY_ALGO_MAXRR = 14;
    public static final short KEY_ALGO_MINRR = 15;
    public static final short KEY_ALGO_MEANRR = 16;
    public static final short KEY_ALGO_SDNN = 17;
    public static final short KEY_ALGO_RMSSD = 18;
    public static final short KEY_ALGO_NNX = 19;
    public static final short KEY_ALGO_PNNX = 20;
    public static final short KEY_ALGO_ULF = 21;
    public static final short KEY_ALGO_VLF = 22;
    public static final short KEY_ALGO_LF = 23;
    public static final short KEY_ALGO_HF = 24;
    public static final short KEY_ALGO_TP = 25;
    public static final short KEY_ALGO_LF_HF = 26;
    public static final short KEY_ALGO_STRESS_SCORE = 27;
    public static final short KEY_ALGO_STRESS_LEVEL = 28;
    public static final short KEY_BLE_CONNECT_STATE = 10;
    public static final short KEY_BLE_ECG_DATA = 11;
    public static final short KEY_DEVICE_POWER_LEVEL = 12;
    public static final short KEY_DEVICE_ELECTRODE_DROP = 13;
    public static final short KEY_BODY_POSE = 14;
    public static final short KEY_DEVICE_FIRMWARE_VERSION = 15;
    public static final short KEY_DEVICE_FIRMWARE_AREA = 16;
    public static final short KEY_DEVICE_BLE_VERSION = 17;
    public static final short KEY_DEVICE_BLE_AREA = 18;
    public static final short KEY_DEVICE_UPDATE_FIRMWARE_PROCESS = 19;
    public static final short KEY_DEVICE_UPDATE_BLE_PROCESS = 20;
    public static final short KEY_DEVICE_UPDATE_FIRMWARE_RESULT = 21;
    public static final short KEY_TYPE_FIRMWARE = 22;
    public static final short KEY_TYPE_BLE = 23;
    public static final short KEY_DEVICE_UPDATE_BLE_RESULT = 24;
    public static final short KEY_DEVICE_UPDATE_MSG = 25;
    public static final short KEY_ECG_DATA = 26;
    public static final short KEY_ACC_DATA = 27;
    public static final short KEY_DEVICE_REQUEST_TIMESTAMP_ON_PHONE = 28;
    public static final short KEY_DEVICE_RETURN_TIME_FOR_VERIFY = 29;
    public static final short KEY_USER_KICK_DEVICE_THAN_NOTIFY_APP = 30;
    public static final short KEY_ALGO_ATRIAL_PREMATURE_BEAT_ALARM = 31;
    public static final short KEY_ALGO_VENTRIVULAR_PREMATURE_BEAT = 32;
    public static final short KEY_ALGO_LONG_STOP_ALARM = 33;
    public static final short KEY_ALGO_BEAT_TOO_FAST_ALARM = 34;
    public static final short KEY_ALGO_BEAT_TOO_SLOW_ALARM = 35;
    public static final short KEY_ALGO_BEAT_TOO_NORMAL_ALARM = 36;
    public static final short KEY_ALGO_RESP_LOW_ALARM = 37;
    public static final short KEY_ALGO_RESP_HIGH_ALARM = 38;
    public static final short KEY_ALGO_RESP_NORMAL_ALARM = 39;
    public static final short KEY_ALGO_AVR_HEART_RATE = 40;
    public static final short KEY_ALGO_APB_AMOUNT = 41;
    public static final short KEY_ALGO_PVC_AMOUNT = 42;
    public static final short KEY_ALGO_FAST_AMOUNT = 43;
    public static final short KEY_ALGO_SLOW_AMOUNT = 44;
    public static final short KEY_ALGO_LONG_STOP_AMOUNT = 45;
    public static final short KEY_ALGO_BEAT_NUMBER = 46;
    public static final short KEY_ALGO_AVR_RESP_RATE = 47;
    public static final short KEY_ALGO_HEALTH_SCORE = 48;
    public static final short KEY_HEARTRATE_FROM_DEVICE = 49;
    public static final short KEY_RR_INTERVAL_FROM_DEVICE = 50;
    public static final short VALUE_BLE_CONNECTED = 0;
    public static final short VALUE_BLE_DISCONNECTED = 1;
    public static final short VALUE_BLE_SERVICE_DISCOVERED = 2;
    public static final short VALUE_BLE_DATA_AVAILABLE = 3;
    public static final short VALUE_BLE_DATA_NOTIFY = 6666;
    public static final short VALUE_POSE_STAND = 0;
    public static final short VALUE_POSE_SIDE_DECUBITUS = 1;
    public static final short VALUE_POSE_LIE_LOW = 2;
    public static final short VALUE_POSE_FALL_DOWN = 3;
    public static final short VALUE_POSE_STOP = 0;
    public static final short VALUE_POSE_WALK = 4;
    public static final short VALUE_POSE_RUN = 5;
    public static final short VALUE_T_TYPE_CAN_NOT_DETECT = 0;
    public static final short VALUE_T_TYPE_UPWARD = 1;
    public static final short VALUE_T_TYPE_DOWNWARD = 2;
    public static final short VALUE_T_TYPE_UPWARD_DOWNWARD = 3;
    public static final short VALUE_T_TYPE_DOWNWARD_UPWARD = 4;
    public static final short VALUE_P_TYPE_CAN_NOT_DETECT = 0;
    public static final short VALUE_P_TYPE_UPWARD = 1;
    public static final short VALUE_P_TYPE_DOWNWARD = 2;
    public static final short VALUE_P_TYPE_UPWARD_DOWNWARD = 3;
    public static final short VALUE_P_TYPE_DOWNWARD_UPWARD = 4;
    public static final short VALUE_RELAXATION = 0;
    public static final short VALUE_LIGHT_STRESS = 1;
    public static final short VALUE_MODERATE_STRESS = 2;
    public static final short VALUE_HEAVY_STRESS = 3;
    public static final short VALUE_STRESS_INITIALIZING = 4;
    public static final short VALUE_UPDATE_FIRMWARE_IS_READY_FOR_UPDATE = 0;
    public static final short VALUE_UPDATE_SEND_DATA_FAILED = 1;
    public static final short VALUE_UPDATE_SEND_DATA_SUCCESS = 2;
    public static final short VALUE_UPDATE_SEND_LAST_DATA_SUCCESS = 3;
    public static final short VALUE_UPDATE_MD5_CHECK_SUCCESS = 4;
    public static final short VALUE_UPDATE_MD5_CHECK_FAILED = 5;
    public static final String UUID_KEY_DATA_FFE2 =
//            "0000ffe2-0000-1000-8000-00805f9b34fb";//HB心率计 notify
            "6e400003-b5a3-f393-e0a9-e50e24dcca9e";//s手环 notify通道特征
    public static final String UUID_KEY_GET_BLE_VERSION =
            "00002a26-0000-1000-8000-00805f9b34fb";//HB心率计
    public static final String UUID_KEY_READ_BLE_AREA =
            "f000ffc1-0451-4000-b000-000000000000";//HB心率计
    public static final String UUID_KEY_OAD_SERVICE =
            "f000ffc0-0451-4000-b000-000000000000";//HB心率计
    public static final String UUID_KEY_DEVICE_INFO_SERVICE =
            "0000180a-0000-1000-8000-00805f9b34fb";//HB心率计
    public static final UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");//HB心率计
    public static final UUID SERVIE_UUID =
//            UUID.fromString("0000FFE0-0000-1000-8000-00805f9b34fb");//HB心率计
            UUID.fromString("6e40ffe1-b5a3-f393-e0a9-e50e24dcca9e");//手环ble服务
    public static final UUID UUID_KEY_DATA_FFE3 =
//            UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");//HB心率计write
            UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");//手环写通道WRITE
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");//HB心率计/S2手环
    public static final String ACTION_GATT_CONNECTED =
            "com.heartbook.smct.ble.ACTION_GATT_CONNECTED";//HB心率计
    public static final String ACTION_GATT_DISCONNECTED =
            "com.heartbook.smct.ble.ACTION_GATT_DISCONNECTED";//HB心率计
    public static final String ACTION_GATT_SERVICES_DISCOVERED =
            "com.heartbook.smct.ble.ACTION_GATT_SERVICES_DISCOVERED";//HB心率计
    public static final String ACTION_DATA_AVAILABLE =
            "com.heartbook.smct.ble.ACTION_DATA_AVAILABLE";//HB心率计
    public static final String EXTRA_DATA =
            "com.heartbook.smct.ble.EXTRA_DATA";//HB心率计

    public SmctConstant() {
    }
}
