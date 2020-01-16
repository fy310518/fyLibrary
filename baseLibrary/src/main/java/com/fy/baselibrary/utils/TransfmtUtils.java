package com.fy.baselibrary.utils;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 数据转换 工具类
 * Created by fangs on 2017/3/22.
 */
public class TransfmtUtils {

    private TransfmtUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * double类型数字  保留一位小数(四舍五入)
     * DecimalFormat转换最简便
     *
     * @param doubleDigital
     * @return String
     */
    public static String doubleToKeepTwoDecimalPlaces(double doubleDigital) {
        DecimalFormat df = new DecimalFormat("##0.0");

        return df.format(doubleDigital);
    }

    /**
     * 把十进制字符串转化成 16进制字符串 (两两截取转换)
     *
     * String str1=    "07101311"
     * 要转换成16进制的  070A0D0B
     *
     * @return 不够两位的前面 补零
     */
    public static String num2HexStr(String data) {
        StringBuffer sb = new StringBuffer(data.length());
        String tmp = null;
        for (int i = 0; i < data.length(); i = i + 2) {
            tmp = data.substring(i, i + 2);
            String strtmp = Integer.toHexString(Integer.parseInt(tmp));

            strtmp = strtmp.length() == 1 ? "0" + strtmp : strtmp;
            sb.append(strtmp);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转十进制字符串 并且 小于十的在前加 “0”
     *
     * @param data
     * @return
     */
    public static String hexToInt(String data) {
        StringBuffer sb = new StringBuffer(data.length());
        String tmp = null;
        for (int i = 0; i < data.length(); i += 2) {
            tmp = data.substring(i, i + 2);
            String strtmp = toInt(tmp);

            sb.append(strtmp);
        }

        return sb.toString();
    }

    /**
     * 十六进制字符串转十进制字符串 并且 小于十的在前加 “0”
     *
     * @param data
     * @return
     */
    public static String toInt(String data) {

        // 十六进制转化为十进制
        int intData = Integer.parseInt(data, 16);

        return TwoBitInteger(intData);
    }

    /**
     * 月份 日期保留两位整数
     *
     * @param data
     * @return
     */
    public static String TwoBitInteger(int data) {
        String result = "";

        if (data < 10) {
            result = "0" + data;
        } else {
            result = data + "";
        }

        return result;
    }

    /**
     * 二进制字符串 转化成 十六进制字符串
     *
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 把16进制字符串转换成  字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        //将字符串中的字符 都转换为大写
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 把字节数组转换成 16进制字符串
     * @param bytes
     * @return
     */
    public static String byte2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1){// 每个字节8为，转为16进制标志，2个16进制位
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }

        return sb.toString();
    }


    /**
     * 将字节数组转换为字符串
     */
    public static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    /**
     * 利用反射实现对象之间属性复制
     * @param from
     * @param to
     */
    public static void copyProperties(Object from, Object to) {
        try {
            copyPropertiesExclude(from, to, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制对象属性
     * @param from
     * @param to
     * @param excludsArray 排除属性列表
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void copyPropertiesExclude(Object from, Object to, String[] excludsArray) throws Exception {
        List<String> excludesList = null;
        if(excludsArray != null && excludsArray.length > 0) {
            excludesList = Arrays.asList(excludsArray); //构造列表对象
        }
        Method[] fromMethods = from.getClass().getDeclaredMethods();
        Method[] toMethods = to.getClass().getDeclaredMethods();
        Method fromMethod = null, toMethod = null;
        String fromMethodName = null, toMethodName = null;
        for (int i = 0; i < fromMethods.length; i++) {
            fromMethod = fromMethods[i];
            fromMethodName = fromMethod.getName();
            if (!fromMethodName.contains("get") || fromMethodName.contains("getId"))
                continue;
            //排除列表检测
            if(excludesList != null && excludesList.contains(fromMethodName.substring(3).toLowerCase())) {
                continue;
            }
            toMethodName = "set" + fromMethodName.substring(3);
            toMethod = findMethodByName(toMethods, toMethodName);
            if (toMethod == null)
                continue;
            Object value = fromMethod.invoke(from, new Object[0]);
            if(value == null)
                continue;
            //集合类判空处理
            if(value instanceof Collection) {
                Collection newValue = (Collection)value;
                if(newValue.size() <= 0)
                    continue;
            }
            toMethod.invoke(to, new Object[] {value});
        }
    }

    /**
     * 从方法数组中获取指定名称的方法
     *
     * @param methods
     * @param name
     * @return
     */
    public static Method findMethodByName(Method[] methods, String name) {
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(name))
                return methods[j];
        }
        return null;
    }
}
