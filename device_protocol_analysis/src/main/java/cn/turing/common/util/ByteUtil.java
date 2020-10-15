package cn.turing.common.util;


import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Byte 工具类
 */
public class ByteUtil {
    public static final int UNICODE_LEN = 2;
    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 将16进制字符串转成二进制字符串
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * @param data
     * @return
     */
    public static boolean checkXHSDeviceCRC(String data) {
        if (data == null) return false;
        data = data.trim();
        if (data.equals("") || data.length() <= 10 || data.startsWith("4040") == false) {
            return false;
        }
        int length = data.trim().length() - 4;
        String result = Integer.toHexString(calcCrc16(toBytes(data.trim().substring(0, length)))); //100次 只要4耗秒
        for (int i = 0; i < 4 && result.length() < 4; i++) {
            result = "0" + result;
        }
        if (data.substring(data.trim().length() - 4).equalsIgnoreCase(result.substring(2) + result.substring(0, 2)))
            return true;
        return false;
    }

    /**
     * @param data
     * @return
     */
    public static boolean checkARCM300DeviceCRC(String data) {
        if (data == null) return false;
        data = data.trim();
        if (data.equals("")) {
            return false;
        }
        int er = data.length();
        int length = data.trim().length() - 4;
        String datae = data.trim().substring(0, length);
        String result = Integer.toHexString(calcCrc16(toBytes(data.trim().substring(0, length)))); //100次 只要4耗秒
        for (int i = 0; i < 4 && result.length() < 4; i++) {
            result = "0" + result;
        }
        if (data.substring(data.trim().length() - 4).equalsIgnoreCase(result.substring(2) + result.substring(0, 2)))
            return true;
        return false;
    }

    /**
     * 生成验证码
     *
     * @param crc
     * @return
     */
    public static String proudceCrc(String crc) {
        return Integer.toHexString(calcCrc16(toBytes(crc.trim())));
    }


    public static boolean checkXHSDeviceCRC2(String data) {
        if (data == null) return false;
        data = data.trim();
        if (data.equals("") || data.length() <= 10 || data.startsWith("5050") == false) {
            return false;
        }
        int length = data.trim().length() - 4;
        String result = Integer.toHexString(calcCrc16(toBytes(data.trim().substring(0, length)))); //100次 只要4耗秒
        for (int i = 0; i < 4 && result.length() < 4; i++) {
            result = "0" + result;
        }
        if (data.substring(data.trim().length() - 4).equalsIgnoreCase(result))
            return true;
        return false;
    }

    /**
     * 外部判断是否是blank
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
//        if(str == null || str.trim().equals("")) {
//            return new byte[0];
//        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    static byte[] crc16_tab_h = {(byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40};

    static byte[] crc16_tab_l = {(byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04, (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8, (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC, (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12,
            (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10, (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4, (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38, (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C, (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7,
            (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0, (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4, (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68, (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C, (byte) 0xB4, (byte) 0x74,
            (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0, (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54, (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98, (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D,
            (byte) 0x4C, (byte) 0x8C, (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40};

    /**
     * 计算CRC16校验
     *
     * @param data 需要计算的数组
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data) {
        return calcCrc16(data, 0, data.length);
    }

    /**
     * 计算CRC16校验
     *
     * @param data   需要计算的数组
     * @param offset 起始位置
     * @param len    长度
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len) {
        return calcCrc16(data, offset, len, 0xffff);
    }

    /**
     * 计算CRC16校验
     *
     * @param data   需要计算的数组
     * @param offset 起始位置
     * @param len    长度
     * @param preval 之前的校验值
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len, int preval) {
        int ucCRCHi = (preval & 0xff00) >> 8;
        int ucCRCLo = preval & 0x00ff;
        int iIndex;
        for (int i = 0; i < len; ++i) {
            iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];
            ucCRCHi = crc16_tab_l[iIndex];
        }
        return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
    }

    public static char Crc16Calc(byte[] data_arr, int data_len) {
        char crc16 = 0;
        int i;
        for (i = 0; i < (data_len); i++) {
            crc16 = (char) ((crc16 >> 8) | (crc16 << 8));
            crc16 ^= data_arr[i] & 0xFF;
            crc16 ^= (char) ((crc16 & 0xFF) >> 4);
            crc16 ^= (char) ((crc16 << 8) << 4);
            crc16 ^= (char) (((crc16 & 0xFF) << 4) << 1);
        }
        return crc16;
    }

    private static String getCrc(byte[] data) {
        int high;
        int flag;

        // 16位寄存器，所有数位均为1
        int wcrc = 0xffff;
        for (int i = 0; i < data.length; i++) {
            // 16 位寄存器的高位字节
            high = wcrc >> 8;
            // 取被校验串的一个字节与 16 位寄存器的高位字节进行“异或”运算
            wcrc = high ^ data[i];

            for (int j = 0; j < 8; j++) {
                flag = wcrc & 0x0001;
                // 把这个 16 寄存器向右移一位
                wcrc = wcrc >> 1;
                // 若向右(标记位)移出的数位是 1,则生成多项式 1010 0000 0000 0001 和这个寄存器进行“异或”运算
                if (flag == 1) {
                    wcrc ^= 0xa001;
                }
            }
        }

        return Integer.toHexString(wcrc);
    }

    // 测试
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    public static String buqi(String s) {
        int count = 16 - s.length();
        String ret = "";
        while (count > 0) {
            ret += "0";
            count--;
        }
        return ret + s;
    }

    public static String buqi2(String s) {
        int count = 8 - s.length();
        String ret = "";
        while (count > 0) {
            ret += "0";
            count--;
        }
        return ret + s;
    }

    public static String buqi5(String s) {
        int count = 32 - s.length();
        String ret = "";
        while (count > 0) {
            ret += "0";
            count--;
        }
        return ret + s;
    }

    public static String buqi3(String s) {
        int count = 2 - s.length();
        String ret = "";
        while (count > 0) {
            ret += "0";
            count--;
        }
        return ret + s;
    }

    public static String buqi4(String s) {
        int count = 4 - s.length();
        String ret = "";
        while (count > 0) {
            ret += "0";
            count--;
        }
        return ret + s;
    }

    /**
     * AD8081数据解析判断校验
     *
     * @param hexString
     * @return
     */
    public static boolean charkAiDeDeviceCRC(String hexString) {
        String eventHex = hexString.substring(18, 20);//事件16进制
        Integer loopHex = Integer.parseInt(hexString.substring(21, 23), 16);//回路10进制
        Integer locationHex = Integer.parseInt(hexString.substring(24, 26), 16);

        String isSum = Integer.toHexString(Integer.parseInt(hexString.substring(6, 8), 16) //帧标志
                + Integer.parseInt(hexString.substring(9, 11), 16)//目的地址
                + Integer.parseInt(hexString.substring(12, 14), 16)//源地址
                + Integer.parseInt(hexString.substring(15, 17), 16) //数据类型
                + Integer.parseInt(eventHex, 16)//事件10进制
                + loopHex
                + locationHex//地址10进制
                + Integer.parseInt(hexString.substring(27, 29), 16));//设备类

        String isSum1 = isSum.substring(isSum.length() - 2, isSum.length());//C-G项之和
        String check = hexString.substring(hexString.length() - 3, hexString.length() - 1).toLowerCase();//效验码

        return check.equals(isSum1);
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static Double shuiYaYunSuan(int input, int input1) {//更改了水压计算公式
        Double num = 1.6 * (input * 2.448 / input1 - 0.5) * 1.25 / 5;
        if (num >= 1.6) {
            return 1.6;
        } else if (num <= 0.0) {
            return 0.0;
        } else {
            return new BigDecimal(num).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

    }

    public static int dianLiangYunSuan(int input) {
        Double num = (4096 * 1.224 / input - 2.8) / 0.8 * 100;

        if (num >= 100.0) {
            return 100;
        } else if (num <= 0) {
            return 0;
        } else {
            return new BigDecimal(num).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
    }

    public static Double wenduYunSuan(int input, int input1) {
        Double num1 = 47.5 / (input * 1.224 / input1) - 9.5;
        return new BigDecimal(OperationUtil.xfsWenDu(num1)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    public static boolean checkXHSDeviceCRC8(String data) {
        String cmmd = data.substring(2, 6).substring(1, 2) + data.substring(2, 6).substring(3);//报警命令
        int num1 = Integer.parseInt(cmmd, 16);
        System.out.println("num1:" + num1);
        String controllerNo = data.substring(6, 10).substring(1, 2) + data.substring(6, 10).substring(3);//控制器号
        int num2 = Integer.parseInt(controllerNo, 16);
        System.out.println("num2" + num2);
        //回路号
        int num3 = Integer.parseInt(data.substring(10, 14).substring(1, 2) + data.substring(10, 14).substring(3), 16);
        System.out.println("num3" + num3);
        //部位号
        int num4 = Integer.parseInt(data.substring(14, 18).substring(1, 2) + data.substring(14, 18).substring(3), 16);
        System.out.println("num4" + num4);
        String partType = data.substring(18, 22).substring(1, 2) + data.substring(18, 22).substring(3); //部件类型
        int num5 = Integer.parseInt(partType, 16);
        System.out.println("num5" + num5);
        String year = data.substring(22, 26).substring(1, 2) + data.substring(22, 26).substring(3);//年
        int num6 = Integer.parseInt(year, 16);
        System.out.println("num6" + num6);
        String str2 = data.substring(26, 30).substring(1, 2) + data.substring(26, 30).substring(3);//月
        int num7 = Integer.parseInt(str2, 16);
        System.out.println("num7" + num7);
        String str3 = data.substring(30, 34).substring(1, 2) + data.substring(30, 34).substring(3);//日
        int num8 = Integer.parseInt(str3, 16);
        System.out.println("num8" + num8);
        String str4 = data.substring(34, 38).substring(1, 2) + data.substring(34, 38).substring(3);//时

        int num9 = Integer.parseInt(str4, 16);
        System.out.println("num9" + num9);
        String str5 = data.substring(38, 42).substring(1, 2) + data.substring(38, 42).substring(3);//分

        int num10 = Integer.parseInt(str5, 16);
        System.out.println("num10" + num10);
        String str6 = data.substring(42, 46).substring(1, 2) + data.substring(42, 46).substring(3);//秒

        int num11 = Integer.parseInt(str6, 16);
        System.out.println("num11" + num11);
        int sum = num1 + num2 + num3 + num4 + num5 + num6 + num7 + num8 + num9 + num10 + num11;

        String str = data.substring(46, 50).substring(1, 2) + data.substring(46, 50).substring(3);
        int sum1 = Integer.parseInt(str, 16);
        System.out.println("sum" + sum);
        System.out.println("sum1" + sum1);
        if (sum == sum1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 赛特威尔的烟感协议校验
     *
     * @param data
     * @return
     */
    public static boolean HexadecimalSumCheck(String data) {
        boolean flag = false;
        if (data == null || data.equals("")) {
            return flag;
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        if (hex.equals("00")) {//协议规定00
            flag = true;
        }
        return flag;

    }

    public static String quling(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * 计算字符串出现的次数
     *
     * @param s
     * @param sub
     * @return
     */
    public static int filter(String s, String sub) {
        int old_length = s.length();
        String replace = "";
        if (s.contains(sub)) {
            replace = s.replace(sub, "");//将需要查找的字符串替换为空
        }
        int new_length = replace.length();//用原来字符串的长度去减替换过的字符串就是该字符串中字符出现的次数
        int count = (old_length - new_length) / (sub.length());//因为是字符串中字符出现的次数,所以要除以字符串你的长度最后就是字符串在另一个字符串中出现的次数
        return count;
    }

    /**
     * CRC16 MODBUS
     *
     * @param bytes
     * @return
     */
    public static String getCRC2(byte[] bytes) {
//        ModBus 通信协议的 CRC ( 冗余循环校验码含2个字节, 即 16 位二进制数。
//        CRC 码由发送设备计算, 放置于所发送信息帧的尾部。
//        接收信息设备再重新计算所接收信息 (除 CRC 之外的部分）的 CRC,
//        比较计算得到的 CRC 是否与接收到CRC相符, 如果两者不相符, 则认为数据出错。
//
//        1) 预置 1 个 16 位的寄存器为十六进制FFFF(即全为 1) , 称此寄存器为 CRC寄存器。
//        2) 把第一个 8 位二进制数据 (通信信息帧的第一个字节) 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器。
//        3) 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位。
//        4) 如果移出位为 0, 重复第 3 步 ( 再次右移一位); 如果移出位为 1, CRC 寄存器与多项式A001 ( 1010 0000 0000 0001) 进行异或。
//        5) 重复步骤 3 和步骤 4, 直到右移 8 次,这样整个8位数据全部进行了处理。
//        6) 重复步骤 2 到步骤 5, 进行通信信息帧下一个字节的处理。
//        7) 将该通信信息帧所有字节按上述步骤计算完成后,得到的16位CRC寄存器的高、低字节进行交换。
//        8) 最后得到的 CRC寄存器内容即为 CRC码。

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= (int) bytes[i];
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) == 1) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //高低位转换
        CRC = ((CRC & 0x0000FF00) >> 8) | ((CRC & 0x000000FF) << 8);
        return Integer.toHexString(CRC);
    }



    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 16进制相加求和 校验
     *
     * @param data
     * @return
     */
    public static String hexSum(String data) {
        if (data != null && data != "") {
            return "";
        }
        int iTotal = 0;
        int iLen = data.length();
        int iNum = 0;

        while (iNum < iLen) {
            String s = data.substring(iNum, iNum + 2);
            iTotal += Integer.parseInt(s, 16);
            iNum = iNum + 2;
        }

        /**
         * 用256求余最大是255，即16进制的FF
         */
        int iMod = iTotal % 256;
        String sHex = Integer.toHexString(iMod);
        iLen = sHex.length();
        //如果不够校验位的长度，补0,这里用的是两位校验
        if (iLen < 2) {
            sHex = "0" + sHex;
        }
        return sHex;

    }

    public static Double setScaleDouble(Double f) {
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static String stringToString(String d){
        String s4=d.substring(6);
        String s3=d.substring(4,6);
        String s2=d.substring(2,4);
        String s1=d.substring(0,2);
        String s=s4+s3+s2+s1;

        return s;
    }

    /**
     * 字符串转化成为16进制字符串
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


    /**
     * 将16进制的字符串转换成文本
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 反转 16进制数据
     * @param datss
     * @return
     */
    public static String reversalHex(String datss){
        LinkedList<String>  linkedList=new LinkedList();
        for (int i=1;i<=datss.length()/2;i++){
            String v= String.valueOf(Integer.parseInt(datss.substring(2*i-2,2*i))-33)  ;
            v=buqi3(v);
            linkedList.addFirst(v);
        }
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=0;i<linkedList.size();i++){
            String message=linkedList.get(i);
            stringBuffer.append(message);
        }
        return stringBuffer.toString();
    }

    public static String reversalHexByNo(String datss){
        LinkedList<String>  linkedList=new LinkedList();
        for (int i=1;i<=datss.length()/2;i++){
            String v= String.valueOf(Integer.parseInt(datss.substring(2*i-2,2*i)))  ;
            v=buqi3(v);
            linkedList.addFirst(v);
        }
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=0;i<linkedList.size();i++){
            String message=linkedList.get(i);
            stringBuffer.append(message);
        }
        return stringBuffer.toString();
    }

    public static String insertToThree(String string){
        StringBuffer stringBuffer=new StringBuffer(string);
        stringBuffer.insert(3,".");

        return  stringBuffer.toString();
    }
    public static String insertToTwo(String string){
        StringBuffer stringBuffer=new StringBuffer(string);
        stringBuffer.insert(2,".");

        return  stringBuffer.toString();
    }

    public static String insertToOne(String string){
        StringBuffer stringBuffer=new StringBuffer(string);
        stringBuffer.insert(1,".");

        return  stringBuffer.toString();
    }
    public static String insertToSix(String string){
        StringBuffer stringBuffer=new StringBuffer(string);
        stringBuffer.insert(6,".");

        return  stringBuffer.toString();
    }

}
