package cn.turing.common.entity;

/**
 * 网关信息
 */
public class Gateways {

    private String fcntdown;         //下行区号
    private String fcntup;           //上行区号
    private String gweui;            //网关名称
    private String rssi;             //信号强度
    private String lsnr;             //信噪比
    private String alti;             //高度
    private String lng;              //经度
    private String lati;             //纬度

    public String getFcntdown() {
        return fcntdown;
    }

    public void setFcntdown(String fcntdown) {
        this.fcntdown = fcntdown;
    }

    public String getFcntup() {
        return fcntup;
    }

    public void setFcntup(String fcntup) {
        this.fcntup = fcntup;
    }

    public String getGweui() {
        return gweui;
    }

    public void setGweui(String gweui) {
        this.gweui = gweui;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getLsnr() {
        return lsnr;
    }

    public void setLsnr(String lsnr) {
        this.lsnr = lsnr;
    }

    public String getAlti() {
        return alti;
    }

    public void setAlti(String alti) {
        this.alti = alti;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }



}
