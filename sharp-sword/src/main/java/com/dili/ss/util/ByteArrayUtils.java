package com.dili.ss.util;

/**
 * byte数组工具
 */
public class ByteArrayUtils {
    /**
     * int --> byte[] 整形转byte[]
     * @param res
     * @return
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * byte[] -->int byte[]转整形
     * @param res
     * @return
     */
    public static int byte2int(byte[] res) {
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示按位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }

    public static String toHexString(byte[] ba) {
        StringBuilder sbuf = new StringBuilder();
        for (byte b : ba) {
            String s = Integer.toHexString((int) (b & 0xff));
            if (s.length() == 1) {
                sbuf.append('0');
            }
            sbuf.append(s);
        }
        return sbuf.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] ba = new byte[len / 2];

        for (int i = 0; i < ba.length; i++) {
            int j = i * 2;
            int t = Integer.parseInt(s.substring(j, j + 2), 16);
            byte b = (byte) (t & 0xFF);
            ba[i] = b;
        }
        return ba;
    }
}
