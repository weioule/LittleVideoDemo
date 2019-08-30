package com.example.littlevideodemo.utils;

import android.util.Log;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author weioule
 * @date 2019/8/3.
 */
public class StringUtil {

    // 判断EditText内容为null或者""
    public static boolean isBlankEdit(TextView view) {
        return (view == null || view.getText() == null || view.getText().length() == 0);
    }

    // 判断字符串对象为null或者""
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0 || "null".equals(str) || "[]".equals(str) || "".equals(str));
    }

    // unicode转中文
    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }

        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5)
                        && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
                        .charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(
                                unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    /**
     * utf-8 转unicode
     *
     * @param str
     * @return String
     */
    public static String toUnicode(String str) {
        char[] arChar = str.toCharArray();
        int iValue = 0;
        String uStr = "";
        for (int i = 0; i < arChar.length; i++) {
            iValue = (int) str.charAt(i);
            char sValue = str.charAt(i);
            if (iValue <= 256) {
                // uStr+="& "+Integer.toHexString(iValue)+";";
                // uStr+="\\"+Integer.toHexString(iValue);
                uStr += sValue;
            } else {
                // uStr+="&#x"+Integer.toHexString(iValue)+";";
                uStr += "\\u" + Integer.toHexString(iValue);
            }
        }
        return uStr;
    }

    // 转32位大写MD5
    public final static String get32MD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            Log.e("Tool", "NoSuchAlgorithmException:" + e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("Tool", "UnsupportedEncodingException:" + e.toString());
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    // 判断是否是数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 判断是否是手机号码
    public static boolean isMobileNO(String mobiles) {
        if (isEmpty(mobiles))
            return false;
        Pattern p = Pattern.compile("^1[0-9]{10}$");
        // ^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // 判断是否是联系人手机号码
    public static boolean isContactMobileNO(String mobiles) {
        if (isEmpty(mobiles))
            return false;
        Pattern p = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[06-8])\\d{8}$");
//		 ^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // 判断是否是邮箱地址
    public static boolean isEmail(String email) {
        if (isEmpty(email))
            return false;
        String str = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 电话号码过滤特殊字符
    public static String toNum(String telephone) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(telephone);
        telephone = m.replaceAll("").trim();
        return telephone;
    }


    // 电话号码部分加*号
    public static String changeMobile(String telephone) {
        if (isEmpty(telephone)) {
            return "";
        }
        if (!isMobileNO(telephone))
            return telephone;
        return telephone.substring(0, 3) + "****"
                + telephone.substring(7, telephone.length());
    }

    // 姓名加*号
    public static String changeName(String name) {
        if (isEmpty(name)) {
            return "";
        }
        return "*" + name.substring(1, name.length());
    }

    // 身份证号加*号
    public static String changeIdentity(String identity) {
        if (isEmpty(identity)) {
            return "";
        }
        if (identity.length() != 15 && identity.length() != 18) {
            return "身份证号码异常";
        }
        return identity.substring(0, 4) + "************"
                + identity.substring(identity.length() - 2, identity.length());
    }

    /*********
     * 时间转换
     * @param time
     * @param matter
     * @return
     */
    public static String convertTimeStr(long time, String matter) {
        SimpleDateFormat formatter = new SimpleDateFormat(matter);
        Date curDate = new Date(time);//
        String str = formatter.format(curDate);
        return str;

    }


    /*********
     * 返回指定小数位的double
     * @param str
     * @param len
     * @return
     */
    public static double converStringToDouble(String str, int len) throws Exception {
        double val = 0.0;
        String format = "0";
        if (len > 0) {
            format = format + ".";
            for (int i = 0; i < len; i++)
                format = format + "0";

        }

        DecimalFormat df = new DecimalFormat(format);
        val = Double.parseDouble(df.format(Double.parseDouble(str)));
        return val;
    }

    /***********
     * 返回指定小数的string
     * @param value
     * @param len
     * @return
     */
    public static String converDoubleToString(double value, int len) {
        String format = "0";
        if (len > 0) {
            format = format + ".";
            for (int i = 0; i < len; i++)
                format = format + "0";

        }

        DecimalFormat df = new DecimalFormat(format);
        return df.format(value);
    }

    /***************
     * 时间字符串格式化
     * @param longTime
     * @param format
     * @return
     */
    public static String longToDate(String longTime, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Long time = Long.parseLong(longTime);
        Date curDate = new Date(time);//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**************
     * 转时间格式
     */
    public static String formatDateStr(String date, String formatBefor, String formatAfter) {
        String result = "";

        try {

            SimpleDateFormat formatter = new SimpleDateFormat(formatBefor);
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(date, pos);

            formatter = new SimpleDateFormat(formatAfter);
            result = formatter.format(strtodate);

        } catch (Exception e) {
            // TODO: handle exception
        }


        return result;
    }


    /********
     * 两个版本号比较   true需要更新 false不需要更新  mVersion<sVersion
     * @param mVersion
     * @param sVersion
     * @return true需要更新   false不需要更新
     */
    public static boolean compareAppVersion(String mVersion, String sVersion) {
        String[] strs1 = mVersion.split("\\.");
        String[] strs2 = sVersion.split("\\.");

        int length = strs1.length > strs2.length ? strs1.length : strs2.length;

        for (int i = 0; i < length; i++) {
            int vi1 = 0;
            int vi2 = 0;
            if (strs1.length >= i + 1) {
                vi1 = Integer.parseInt(strs1[i]);
            }
            if (strs2.length >= i + 1) {
                vi2 = Integer.parseInt(strs2[i]);
            }
            if (vi1 < vi2) {
                return true;
            } else if (vi1 > vi2) {
                return false;
            }
        }
        return false;
    }

    public static String formatDecimal2(double d) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(d);
    }

    public static String getMd5Value(String sSecret) {
        if (StringUtil.isEmpty(sSecret)) {
            return "";
        }
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
