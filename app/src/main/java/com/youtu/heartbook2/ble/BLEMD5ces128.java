package com.youtu.heartbook2.ble;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by djf on 2017/3/6.
 */

public class BLEMD5ces128 {
    private byte[] encrypt_key_1 = new byte[]{(byte)70, (byte)117, (byte)99, (byte)107, (byte)32, (byte)121, (byte)111, (byte)117, (byte)33, (byte)32, (byte)84, (byte)104, (byte)105, (byte)101, (byte)102, (byte)33};
    private byte[] encrypt_key_2 = new byte[]{(byte)67, (byte)80, (byte)84, (byte)66, (byte)84, (byte)80, (byte)84, (byte)80, (byte)66, (byte)67, (byte)80, (byte)84, (byte)84, (byte)80, (byte)84, (byte)80};
    private static String key_1 = "Fuck you! Thief!";
    private static String key_2 = "CPTBTPTPBCPTTPTP";
    private byte[] table_crc_h = new byte[]{(byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)1, (byte)-64, (byte)-128, (byte)65, (byte)0, (byte)-63, (byte)-127, (byte)64};
    private byte[] table_crc_l = new byte[]{(byte)0, (byte)-64, (byte)-63, (byte)1, (byte)-61, (byte)3, (byte)2, (byte)-62, (byte)-58, (byte)6, (byte)7, (byte)-57, (byte)5, (byte)-59, (byte)-60, (byte)4, (byte)-52, (byte)12, (byte)13, (byte)-51, (byte)15, (byte)-49, (byte)-50, (byte)14, (byte)10, (byte)-54, (byte)-53, (byte)11, (byte)-55, (byte)9, (byte)8, (byte)-56, (byte)-40, (byte)24, (byte)25, (byte)-39, (byte)27, (byte)-37, (byte)-38, (byte)26, (byte)30, (byte)-34, (byte)-33, (byte)31, (byte)-35, (byte)29, (byte)28, (byte)-36, (byte)20, (byte)-44, (byte)-43, (byte)21, (byte)-41, (byte)23, (byte)22, (byte)-42, (byte)-46, (byte)18, (byte)19, (byte)-45, (byte)17, (byte)-47, (byte)-48, (byte)16, (byte)-16, (byte)48, (byte)49, (byte)-15, (byte)51, (byte)-13, (byte)-14, (byte)50, (byte)54, (byte)-10, (byte)-9, (byte)55, (byte)-11, (byte)53, (byte)52, (byte)-12, (byte)60, (byte)-4, (byte)-3, (byte)61, (byte)-1, (byte)63, (byte)62, (byte)-2, (byte)-6, (byte)58, (byte)59, (byte)-5, (byte)57, (byte)-7, (byte)-8, (byte)56, (byte)40, (byte)-24, (byte)-23, (byte)41, (byte)-21, (byte)43, (byte)42, (byte)-22, (byte)-18, (byte)46, (byte)47, (byte)-17, (byte)45, (byte)-19, (byte)-20, (byte)44, (byte)-28, (byte)36, (byte)37, (byte)-27, (byte)39, (byte)-25, (byte)-26, (byte)38, (byte)34, (byte)-30, (byte)-29, (byte)35, (byte)-31, (byte)33, (byte)32, (byte)-32, (byte)-96, (byte)96, (byte)97, (byte)-95, (byte)99, (byte)-93, (byte)-94, (byte)98, (byte)102, (byte)-90, (byte)-89, (byte)103, (byte)-91, (byte)101, (byte)100, (byte)-92, (byte)108, (byte)-84, (byte)-83, (byte)109, (byte)-81, (byte)111, (byte)110, (byte)-82, (byte)-86, (byte)106, (byte)107, (byte)-85, (byte)105, (byte)-87, (byte)-88, (byte)104, (byte)120, (byte)-72, (byte)-71, (byte)121, (byte)-69, (byte)123, (byte)122, (byte)-70, (byte)-66, (byte)126, (byte)127, (byte)-65, (byte)125, (byte)-67, (byte)-68, (byte)124, (byte)-76, (byte)116, (byte)117, (byte)-75, (byte)119, (byte)-73, (byte)-74, (byte)118, (byte)114, (byte)-78, (byte)-77, (byte)115, (byte)-79, (byte)113, (byte)112, (byte)-80, (byte)80, (byte)-112, (byte)-111, (byte)81, (byte)-109, (byte)83, (byte)82, (byte)-110, (byte)-106, (byte)86, (byte)87, (byte)-105, (byte)85, (byte)-107, (byte)-108, (byte)84, (byte)-100, (byte)92, (byte)93, (byte)-99, (byte)95, (byte)-97, (byte)-98, (byte)94, (byte)90, (byte)-102, (byte)-101, (byte)91, (byte)-103, (byte)89, (byte)88, (byte)-104, (byte)-120, (byte)72, (byte)73, (byte)-119, (byte)75, (byte)-117, (byte)-118, (byte)74, (byte)78, (byte)-114, (byte)-113, (byte)79, (byte)-115, (byte)77, (byte)76, (byte)-116, (byte)68, (byte)-124, (byte)-123, (byte)69, (byte)-121, (byte)71, (byte)70, (byte)-122, (byte)-126, (byte)66, (byte)67, (byte)-125, (byte)65, (byte)-127, (byte)-128, (byte)64};
    private String TAG=this.getClass().getName();

    public BLEMD5ces128() {
    }

    public byte[] Encrypt(byte[] raw, byte[] key) {
        byte[] encrypted = new byte[16];
        byte[] encry = null;

        try {
            SecretKeySpec i = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(key);
            cipher.init(1, i, iv);
            encry = cipher.doFinal(raw);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        for(int var9 = 0; var9 < encrypted.length; ++var9) {
            encrypted[var9] = encry[var9];
        }

        return encrypted;
    }

    public byte[] Encrypt(byte[] raw) {
        byte[] encrypted = new byte[16];

        int i;
        for(i = 0; i < encrypted.length; ++i) {
            encrypted[i] = raw[i];
        }

        encrypted = this.Encrypt(encrypted, this.encrypt_key_1);

        for(i = 0; i < encrypted.length; ++i) {
            raw[i] = encrypted[i];
        }

        i = 4;

        int j;
        for(j = 0; i < raw.length; ++j) {
            encrypted[j] = raw[i];
            ++i;
        }

        encrypted = this.Encrypt(encrypted, this.encrypt_key_2);
        i = 4;

        for(j = 0; i < raw.length; ++j) {
            raw[i] = encrypted[j];
            ++i;
        }

        return raw;
    }

    public static byte[] Decrypt(byte[] sSrc, String sKey) {
        byte[] original = null;

        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec(raw);
            cipher.init(2, skeySpec, iv);
            original = cipher.doFinal(sSrc);
            new String(original, "utf-8");
        } catch (Exception var8) {
            ;
        }

        return original;
    }

    public static byte[] Decrypts(byte[] raw) {
        byte[] encrypted = new byte[16];
        int i = 4;

        int j;
        for(j = 0; i < raw.length; ++j) {
            encrypted[j] = raw[i];
            ++i;
        }

        encrypted = Decrypt(encrypted, key_2);
        i = 4;

        for(j = 0; i < raw.length; ++j) {
            raw[i] = encrypted[j];
            ++i;
        }

        for(i = 0; i < encrypted.length; ++i) {
            encrypted[i] = raw[i];
        }

        encrypted = Decrypt(encrypted, key_1);

        for(i = 0; i < encrypted.length; ++i) {
            raw[i] = encrypted[i];
        }

        return raw;
    }

    public byte[] crc16_get(byte[] data, int len) {
        byte[] b = new byte[2];
        boolean iIndex = false;
        short crc_h = 255;
        int crc_l = 255;

        for(int i = 0; i < len; ++i) {
            int var8 = (crc_l ^ data[i]) & 255;
            crc_l = crc_h ^ this.table_crc_h[var8];
            crc_h = this.table_crc_l[var8];
        }

        b[0] = (byte)crc_h;
        b[1] = (byte)crc_l;
        return b;
    }
}

