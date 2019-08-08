package com.example.smallvideodemo.net.okhttp;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class HeaderBean {

    /**
     * device : M9
     * os_type : Android
     * os :
     * os_ver : 7.0
     * market : oppo
     * name : jrtt
     * ver : 1.6.6
     * brand: 品牌
     * model: 型号
     * size: 内存大小
     */

    private String device;
    private String os_type;
    private String os;
    private String os_ver;
    private String market;
    private String name;
    private String ver;
    private String brand;
    private String model;
    private String size;
    private String deviceId;
    private String net_type;
    private String sm_deviceId;

    public String getSm_deviceId() {
        return sm_deviceId;
    }

    public void setSm_deviceId(String sm_deviceId) {
        this.sm_deviceId = sm_deviceId;
    }

    public String getNet_type() {
        return net_type;
    }

    public void setNet_type(String net_type) {
        this.net_type = net_type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOs_type() {
        return os_type;
    }

    public void setOs_type(String os_type) {
        this.os_type = os_type;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOs_ver() {
        return os_ver;
    }

    public void setOs_ver(String os_ver) {
        this.os_ver = os_ver;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}
