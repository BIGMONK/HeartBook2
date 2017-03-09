package com.youtu.heartbook2.ble;

import android.util.Log;

import com.youtu.heartbook2.SmctConstant;

/**
 * Created by djf on 2017/3/6.
 */
public class HandleBleData {
    private static final int blePkgLength = 19;
    private static HandleBleData.byteBuf packageBuf;
    private static int ecgIndex = 0;
    private static EncryptDecode encryptDecode = new EncryptDecode();

    public HandleBleData() {
    }

    public static void HandleData(byte[] rawData, IBleOperateCallback bleOperateCallback, IUpdateCallBack updateCallback) {

        HandleData_version(rawData, bleOperateCallback, updateCallback);

        if (rawData != null && rawData.length == 16) {
            byte[] decodeData = BLEMD5ces128.Decrypts(rawData);
            if (decodeData != null) {
                HandleData_version2(decodeData, bleOperateCallback, updateCallback);
            }
        }
    }

    /**
     * 完整数据回调
     *
     * @param rawData
     * @param bleOperateCallback
     * @param updateCallback
     */
    public static void HandleData_version(byte[] rawData, IBleOperateCallback bleOperateCallback, IUpdateCallBack updateCallback) {
        if (rawData != null) {
            bleOperateCallback.bleData(SmctConstant.VALUE_BLE_DATA_NOTIFY, rawData);
        }

    }

    public static void HandleData_version2(byte[] rawData, IBleOperateCallback bleOperateCallback, IUpdateCallBack updateCallback) {
        if (rawData != null) {
            if ((rawData[0] & 255) == 0 && (rawData[1] & 255) == 250 && (rawData[2] & 255) == 250) {
                int mpackageIndex = rawData[3] & 255;
                int mpackageLength = rawData[4] & 255;
                int mpackageCMD = rawData[5] & 255;
                int mblePkgNumInPkg = mpackageLength % 19 == 0 ? mpackageLength / 19 : mpackageLength / 19 + 1;
//                packageBuf = new HandleBleData.byteBuf((HandleBleData.byteBuf)null);
                packageBuf = new HandleBleData.byteBuf();
                packageBuf.init(mpackageIndex, mpackageLength, mpackageCMD, mblePkgNumInPkg);
            }

            packageBuf.addData(rawData);
            if (packageBuf.pointer == packageBuf.blePkgNumInPkg) {
                packageBuf.removeZero();
                if (packageBuf.verifyCRC()) {
                    packageBuf.AnalyzeData(packageBuf.pkgNoHeadCrcBuf, bleOperateCallback, updateCallback);
                }
            }
        }

    }

    private static void getHeartRate(byte[] raw, IBleOperateCallback callback) {
        for (int i = 0; i < raw.length; ++i) {
            if (i != raw.length) {
                int heartRate = (raw[i] & 255) * 256 + (raw[i + 1] & 255);
                callback.bleData(SmctConstant.KEY_ECG_DATA, (short) heartRate);
            }

            ++i;
        }

    }

    private static class byteBuf {
        int blePkgNumInPkg;
        int packageCMD;
        int packageLength;
        int packageIndex;
        byte[] packageBuffer;
        byte[] tempBuf;
        int[] unzipDataBuffer;
        byte[] pkgNoHeadCrcBuf;
        int pointer;
        int ecgDataKind;
        int ecgSampleRate;
        int ecgLeadNumber;
        int accSampleRate;
        int accCharLength;

        private byteBuf() {
            this.blePkgNumInPkg = 0;
            this.packageCMD = 0;
            this.packageLength = 0;
            this.packageIndex = 0;
            this.pointer = 0;
            this.ecgDataKind = 0;
            this.ecgSampleRate = 500;
            this.ecgLeadNumber = 0;
            this.accSampleRate = 20;
            this.accCharLength = 2;
        }

        void init(int mpackageIndex, int mpackageLength, int mpackageCMD, int mblePkgNumInPkg) {
            this.packageIndex = mpackageIndex;
            this.packageLength = mpackageLength;
            this.packageCMD = mpackageCMD;
            this.blePkgNumInPkg = mblePkgNumInPkg;
            this.packageBuffer = new byte[this.packageLength];

            for (int i = 0; i < this.packageBuffer.length; ++i) {
                this.packageBuffer[i] = 0;
            }

            this.pointer = 0;
        }

        void addData(byte[] data) {
            int max = data.length;
            if (this.pointer == this.blePkgNumInPkg - 1) {
                max = this.packageLength - this.pointer * 19;
                ++max;
            }

            int blePkgIndex = this.pointer * 19;
            int i = 0;

            for (int j = 1; j < max; ++i) {
                this.packageBuffer[blePkgIndex + i] = data[j++];
            }

            ++this.pointer;
        }

        void removeZero() {
            int num = 0;
            int oldLength = this.packageBuffer.length;

            int i;
            for (i = 0; i < oldLength - 1; ++i) {
                if ((this.packageBuffer[i] & 255) == 250 && (this.packageBuffer[i + 1] & 255) == 0) {
                    ++num;
                }
            }

            this.tempBuf = new byte[oldLength - num];
            i = 1;

            for (int j = 1; i < oldLength; ++i) {
                if ((this.packageBuffer[i - 1] & 255) != 250 || (this.packageBuffer[i] & 255) != 0) {
                    this.tempBuf[j++] = this.packageBuffer[i];
                }
            }

            this.tempBuf[0] = this.packageBuffer[0];
        }

        boolean verifyCRC() {
            this.getValidData();
            byte[] byteverify = HandleBleData.encryptDecode.crc16_get(this.pkgNoHeadCrcBuf, this.pkgNoHeadCrcBuf.length);
            return this.tempBuf[this.tempBuf.length - 1] == byteverify[0] && this.tempBuf[this.tempBuf.length - 2] == byteverify[1];
        }

        void getValidData() {
            this.pkgNoHeadCrcBuf = new byte[this.tempBuf.length - 5 - 2];
            int i = 0;

            for (int j = 5; j < this.tempBuf.length - 2; ++i) {
                this.pkgNoHeadCrcBuf[i] = this.tempBuf[j++];
            }

        }

        void AnalyzeData(byte[] data, IBleOperateCallback callback, IUpdateCallBack updateCallback) {
            int i;
            switch (this.packageCMD) {
                case 1:
                    int j;
                    byte[] var10;
                    if ((data[0] & 255) == 1) {
                        this.ecgDataKind = data[1] & 255;
                        this.ecgLeadNumber = data[3] & 255;
                        var10 = new byte[data.length - 4];
                        i = 0;

                        for (j = 4; j < data.length; ++i) {
                            var10[i] = data[j++];
                        }

                        this.HandleEcgData(var10, callback);
                    } else if ((data[0] & 255) == 2) {
                        if ((data[1] & 255) == 129) {
                            var10 = new byte[data.length - 2];
                            i = 0;

                            for (j = 2; j < data.length; ++i) {
                                var10[i] = data[j++];
                            }

                            if (var10.length % 15 != 0) {
                                return;
                            }

                            for (i = 0; i < var10.length; i += 15) {
                                j = var10[i] & 255;
                                int sport_status = var10[i + 2] & 255;
                                if (j == 0) {
                                    if (sport_status == 0) {
                                        callback.bleData(SmctConstant.KEY_ALGO_MAXRR, SmctConstant.KEY_ALGO_HEART_RATE);
                                    } else if (sport_status == 2) {
                                        callback.bleData(SmctConstant.KEY_ALGO_MAXRR, (short) 4);
                                    } else if (sport_status == 5) {
                                        callback.bleData(SmctConstant.KEY_ALGO_MAXRR, (short) 5);
                                    }
                                } else if (j == 1) {
                                    callback.bleData(SmctConstant.KEY_ALGO_MAXRR, (short) 1);
                                } else if (j == 2) {
                                    callback.bleData(SmctConstant.KEY_ALGO_MAXRR, (short) 2);
                                } else if (j == 3) {
                                    callback.bleData(SmctConstant.KEY_ALGO_MAXRR, (short) 3);
                                }
                            }
                        } else if ((data[1] & 255) == 1) {
                            this.accSampleRate = data[2] & 255;
                            this.accCharLength = data[3] & 255;
                            var10 = new byte[data.length - 4];
                            i = 0;

                            for (j = 4; j < data.length; ++i) {
                                var10[i] = data[j++];
                            }

                            this.HandleAccData(var10, callback);
                        }
                    }
                    break;
                case 2:
                    if ((data[0] & 255) == 129) {
                        if ((data[1] & 255) == 1) {
                            updateCallback.updateMsg((short) 25, (short) 0);
                        } else if ((data[1] & 255) == 2) {
                            Log.e("heartbook", "You can not go to update firmware");
                        }
                    } else if ((data[0] & 255) == 130) {
                        if ((data[1] & 255) == 1) {
                            updateCallback.updateMsg((short) 25, (short) 2);
                        } else if ((data[1] & 255) == 2) {
                            updateCallback.updateMsg((short) 25, (short) 1);
                        }
                    } else if ((data[0] & 255) == 131) {
                        updateCallback.updateMsg((short) 25, (short) 3);
                    } else if ((data[0] & 255) == 132) {
                        if ((data[1] & 255) == 1) {
                            updateCallback.updateMsg((short) 25, (short) 4);
                        } else if ((data[1] & 255) == 2) {
                            updateCallback.updateMsg((short) 25, (short) 5);
                        }
                    }
                    break;
                case 3:
                    if ((data[0] & 255) == 130 && (data[1] & 255) == 7) {
                        String var9 = String.valueOf((char) (data[2] & 255)) + (char) (data[3] & 255) + (char) (data[4] & 255) + "." + (char) (data[5] & 255) + (char) (data[6] & 255) + "." + (char) (data[7] & 255) + (char) (data[8] & 255);
                        updateCallback.updateVersionMsg((short) 15, var9);
                        updateCallback.updateMsg((short) 16, (short) (data[9] & 255));
                    } else if ((data[0] & 255) == 131) {
                        if ((data[1] & 255) == 2) {
                            if ((data[2] & 255) != 0 && (data[2] & 255) != 1) {
                                if ((data[2] & 255) == 2) {
                                    Log.i("HandleBleData", "electrode is ok");
                                }
                            } else {
                                callback.bleData((short) 13, (short) -1);
                            }
                        }
                    } else if ((data[0] & 255) == 133) {
                        if ((data[1] & 255) == 2) {
                            int time = data[2] & 255;
                            callback.bleData((short) 12, (short) time);
                        }
                    } else if ((data[0] & 255) != 128 || (data[1] & 255) != 2) {
                        if ((data[0] & 255) == 135) {
                            if ((data[1] & 255) == 1) {
                                callback.bleData((short) 28, (short) -1);
                            } else if ((data[1] & 255) == 2) {
                                short[] var8 = new short[6];

                                for (i = 0; i < 6; ++i) {
                                    var8[i] = (short) (data[i + 2] & 255);
                                }

                                callback.bleData((short) 29, var8);
                            }
                        } else if ((data[0] & 255) == 136 && (data[1] & 255) == 1) {
                            callback.bleData((short) 30, (short) -1);
                        }
                    }
                case 81:
                case 82:
            }

        }

        void HandleEcgData(byte[] data, IBleOperateCallback callback) {
            if (this.ecgDataKind == 1) {
                HandleBleData.getHeartRate(data, callback);
            } else {
                int hr;
                int rr;
                int i;
                int e;
                if (this.ecgDataKind == 2) {
                    if (data.length % 3 != 0) {
                        Log.e("liufa", "data length is incorrect,array may out of bound!!");
                    }

                    this.unzipDataBuffer = new int[data.length / 3 * 2];
                    hr = 0;

                    for (rr = 0; hr < data.length; hr += 3) {
                        i = (data[hr] & 255) << 4 | (data[hr + 1] & 255) >> 4;
                        this.unzipDataBuffer[rr++] = i;
                        e = (data[hr + 1] & 255 & 15) << 8 | data[hr + 2] & 255;
                        this.unzipDataBuffer[rr++] = e;
                    }

                    for (hr = 0; hr < this.unzipDataBuffer.length; ++hr) {
                        if (hr % 2 == 0) {
                            try {
                                Thread.sleep(3L);
                            } catch (InterruptedException var10) {
                                var10.printStackTrace();
                            }
                        }

                        callback.bleData(SmctConstant.KEY_ECG_DATA, (short) this.unzipDataBuffer[hr]);
                    }
                } else if (this.ecgDataKind == 3) {
                    if (data.length - 1 != 0) {
                        Log.e("liufa", "data length is incorrect,array may out of bound!!");
                    }

                    hr = data[0] & 255;
                    if (HandleBleData.ecgIndex + 1 != hr && (HandleBleData.ecgIndex != 127 || hr != 1)) {
                        rr = hr - HandleBleData.ecgIndex - 1;
                        if (HandleBleData.ecgIndex >= hr) {
                            rr = 127 - HandleBleData.ecgIndex + hr - 1;
                        }

                        i = rr * 72;

                        for (e = 0; e < i; ++e) {
                            callback.bleData(SmctConstant.KEY_ECG_DATA, (short) 0);
                        }
                    }

                    byte[] var11 = new byte[data.length - 1];
                    i = 0;

                    for (e = 1; e < data.length; ++i) {
                        var11[i] = data[e++];
                    }

                    this.unzipDataBuffer = new int[var11.length / 3 * 2];
                    i = 0;

                    for (e = 0; i < var11.length; i += 3) {
                        int ecg1 = (var11[i] & 255) << 4 | (var11[i + 1] & 255) >> 4;
                        this.unzipDataBuffer[e++] = ecg1;
                        int ecg2 = (var11[i + 1] & 255 & 15) << 8 | var11[i + 2] & 255;
                        this.unzipDataBuffer[e++] = ecg2;
                    }

                    for (i = 0; i < this.unzipDataBuffer.length; ++i) {
                        if (i % 2 == 0) {
                            try {
                                Thread.sleep(3L);
                            } catch (InterruptedException var9) {
                                var9.printStackTrace();
                            }
                        }

                        callback.bleData((short) 26, (short) this.unzipDataBuffer[i]);
                    }

                    HandleBleData.ecgIndex = hr;
                } else if (this.ecgDataKind == 129) {
                    hr = (data[0] & 255) * 256 + (data[1] & 255);
                    callback.bleData((short) 49, (short) hr);
                    rr = (data[2] & 255) * 256 + (data[3] & 255);
                    callback.bleData((short) 50, (short) rr);
                }
            }

        }

        public static float toAccG(byte[] bRefArr) {
            int iOutcome = 0;

            int temp;
            for (temp = 0; temp < bRefArr.length; ++temp) {
                byte bLoop = bRefArr[temp];
                iOutcome += (bLoop & 255) << 8 * temp;
            }

            if (iOutcome >= '耀') {
                temp = 0;
                --iOutcome;

                for (int i = 0; i < 16; ++i) {
                    temp += ((iOutcome & 1 << i) >> i == 1 ? 0 : 1) << i;
                }

                iOutcome = temp * -1;
            }

            return (float) iOutcome * 2.0F / 32768.0F * 9.8F;
        }

        void HandleAccData(byte[] raw, IBleOperateCallback callback) {
            if (raw.length % 6 != 0) {
                Log.e("liufa", "data length is incorrect,array may out of bound!!");
            }

            float[] acc = new float[3];

            for (int i = 0; i < raw.length; ++i) {
                if (i != raw.length) {
                    byte[] x_byte = new byte[]{raw[i + 1], raw[i]};
                    acc[0] = toAccG(x_byte);
                    i += 2;
                    byte[] y_byte = new byte[]{raw[i + 1], raw[i]};
                    acc[1] = toAccG(y_byte);
                    i += 2;
                    byte[] z_byte = new byte[]{raw[i + 1], raw[i]};
                    acc[2] = toAccG(z_byte);
                    ++i;
                    callback.bleData((short) 27, acc);
                }
            }

        }
    }
}
