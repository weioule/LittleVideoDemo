package com.example.littlevideodemo.utils;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class ViewUtil {

    /*
     * 获取内存(RAM)大小
     */
    public static String getTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        long availMem = mi.availMem;
        long totalMem = mi.totalMem;
//		Log.e("TAG","totalMem = "+Formatter.formatFileSize(context, totalMem));
//		Log.e("TAG","availMem = "+Formatter.formatFileSize(context, availMem));
//		return Formatter.formatFileSize(context, availMem)+"/"+Formatter.formatFileSize(context, totalMem);
        return Formatter.formatFileSize(context, totalMem);
    }

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, availableBlocks * blockSize);
    }

    /**
     * 获取手机外部存储空间
     *
     * @param context
     * @return 以M, G为单位的容量
     */
    public static String getExternalMemorySize(Context context) {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        return Formatter
                .formatFileSize(context, blockCountLong * blockSizeLong);
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static String getTotalInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, totalBlocks * blockSize);
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public static String getAvailableExternalMemorySize(Context context) {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return Formatter.formatFileSize(context, availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public static String getTotalExternalMemorySize(Context context) {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return Formatter.formatFileSize(context, totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    //获取当前app的版本号
    public static String getAppVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取当前设备ID
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String deviceId = tm.getDeviceId();
            if (StringUtil.isEmpty(deviceId)) return "";
            return deviceId;
        } catch (Exception e) {
            return "";
        }
    }

    //获取当前设备imsi
    @SuppressLint("MissingPermission")
    public static String getIMSI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAndroidId(Context context) {
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return androidId;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Mac地址
    public static String getLocalMac(Context context) {
//        try {
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            return info.getMacAddress();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }

        //上面的方法在6.0以上版本存在mac地址获取不到情况
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    //获取当前设备ID
    public static String getInstalledTime(Context context) {
        String installedTime = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long firstInstallTime = packageInfo.firstInstallTime;//应用第一次安装的时间
            installedTime = sdf.format(firstInstallTime);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return installedTime;
    }

    //获取当前设备型号
    public static String getROMName() {
        try {
            return android.os.Build.MANUFACTURER;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    //获取当前设备型号
    public static String getDeviceName() {
        try {
            return android.os.Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取当前设备品牌
    public static String getDeviceBrand() {
        try {
            return android.os.Build.BRAND;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取当前设备厂商
    public static String getDeviceCarrier() {
        try {
            return android.os.Build.MANUFACTURER;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取当前系统的版本号
    public static String getOsVersion() {
        try {
            return android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断手机是否root，不弹出root请求框
     */
    public static int getDeviceisRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
        if (new File(binPath).exists() && isExecutable(binPath))
            return 1;
        if (new File(xBinPath).exists() && isExecutable(xBinPath))
            return 1;
        return 0;
    }

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String getBSSID(Context context) {
        String bssid = null;
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                bssid = wifiInfo.getBSSID();
                return bssid;
            }
        }
        return bssid;
    }

    public static String getWifi(Context context) {
        String wifi = "";
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                wifi = wifiInfo.getSSID();
                return wifi.replace("\"", "");
            }
        }
        return wifi.replace("\"", "");
    }

    public static String getDns(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                return intToIp(dhcpInfo.dns1) + "," + intToIp(dhcpInfo.dns2);
            }
        }
        return "";
    }

    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
     *
     * @param context
     * @return
     */
    public static int isEmulator(Context context) {
        String imei = getDeviceId(context);
        String imsi = getIMSI(context);
        if ((imei != null && "000000000000000".equals(imei)) || (imsi != null && "310260000000000".equals(imsi))) {
            return 1;
        }
        return 0;
    }

    /*
     * @return 1,代表中国移动，2，代表中国联通，3，代表中国电信，0，代表未知
     * @author youzc@yiche.com
     * 46000 中国移动 （GSM）
     * 46001 中国联通 （GSM）
     * 46002 中国移动 （TD-S）
     * 46003 中国电信（CDMA）
     * 46004 空（似乎是专门用来做测试的）
     * 46005 中国电信 （CDMA）
     * 46006 中国联通 （WCDMA）
     * 46007 中国移动 （TD-S）
     * 46008
     * 46009
     * 46010
     * 46011 中国电信 （FDD-LTE）
     */
    public static String getOperatorsCode(Context context) {
        try {
            // 移动设备网络代码（英语：Mobile Network Code，MNC）是与移动设备国家代码（Mobile Country Code，MCC）（也称为“MCC /
            // MNC”）相结合, 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String IMSI = telephonyManager.getSubscriberId();
            if (IMSI != null && IMSI.length() >= 5) {
                return IMSI.substring(0, 5);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getOperators(Context context) {
        String carrier = "";
        try {
            // 移动设备网络代码（英语：Mobile Network Code，MNC）是与移动设备国家代码（Mobile Country Code，MCC）（也称为“MCC /
            // MNC”）相结合, 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 运营商代码
            if (IMSI == null) {
                return carrier;
            }
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                carrier = "中国移动";
            } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                carrier = "中国联通";
            } else if (IMSI.startsWith("46003") || IMSI.startsWith("46005") || IMSI.startsWith("46011")) {
                carrier = "中国电信";
            } else if (IMSI.length() >= 5) {
                carrier = IMSI.substring(0, 5);
            } else {
                carrier = "未知的运营商";
            }
            return carrier;
        } catch (Exception e) {
            e.printStackTrace();
            return carrier;
        }
    }

    /*********
     * 获取当前网络信息
     * @return
     */
    public static String getNetworkType(Context context) {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

            }
        }

        return strNetworkType;
    }


    /*********
     * 获取当前进程名称
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();

        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    /**************
     * 获取sdk路径
     * @return
     */
    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    /**
     * 还原View的触摸和点击响应范围,最小不小于View自身范围
     *
     * @param view
     */
    public static void restoreViewTouchDelegate(final View view) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                bounds.setEmpty();
                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(final View view, final int top, final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public static void hideSystemUI(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /*
     * 计算ListView内容高度
     */
    public static void setListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        params.height = height + listView.getPaddingTop() + listView.getPaddingBottom();
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static int getImageCount() {
        // TODO Auto-generated method stub
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
        int count = 0;
        try {
            File file = new File(path);
            File[] files = file.listFiles();
            for (int j = 0; j < files.length; j++) {
                String name = files[j].getName();
                if (files[j].isFile() & name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
                    count++;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return count;
    }

    /**
     * 获取手机总内存大小
     *
     * @return
     */
    public static String getTotalRam() {//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";//返回1GB/2GB/3GB/4GB
    }

    /**
     * 获得总存储控件
     *
     * @param context
     * @return
     */
    public String getDataTotalSize(Context context) {
        StatFs sf = new StatFs(context.getCacheDir().getAbsolutePath());
        long blockSize = sf.getBlockSize();
        long totalBlocks = sf.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }
}
