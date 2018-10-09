package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Superman on 2018/8/28.
 */

public class TakeWater implements Parcelable {

    /**
     * Q : 7.982
     * SUMQ : 89180
     * NT : 地表水
     * ADAG : 金安区水利局
     * LGTD : 116.5707
     * SWFCD : 15020066
     * ADCD : 341502000000
     * SWFNM : 安徽康泰玻业科技有限公司
     * LTTD : 31.7786
     * MNTM : 2018-08-21 02:23:43
     */

    private double Q;//瞬时流量
    private String SUMQ;//累计取水量
    private String NT;
    private String ADAG;//管理机构
    private double LGTD;
    private String SWFCD;//取水口编码
    private String ADCD;
    private String SWFNM;//取水口名称
    private double LTTD;
    private String MNTM;//时间
    private String NFWUSE;//取水口用途
    private String ADNM;//行政区划
    private String DYFWQ;//最大取水量
    private String REACH_NAME;//河段名称

    public String getREACH_NAME() {
        return REACH_NAME;
    }

    public void setREACH_NAME(String REACH_NAME) {
        this.REACH_NAME = REACH_NAME;
    }

    public String getADNM() {
        return ADNM;
    }

    public void setADNM(String ADNM) {
        this.ADNM = ADNM;
    }

    public String getDYFWQ() {
        return DYFWQ;
    }

    public void setDYFWQ(String DYFWQ) {
        this.DYFWQ = DYFWQ;
    }

    public String getNFWUSE() {
        return NFWUSE;
    }

    public void setNFWUSE(String NFWUSE) {
        this.NFWUSE = NFWUSE;
    }

    public double getQ() {
        return Q;
    }

    public void setQ(double Q) {
        this.Q = Q;
    }

    public String getSUMQ() {
        return SUMQ;
    }

    public void setSUMQ(String SUMQ) {
        this.SUMQ = SUMQ;
    }

    public String getNT() {
        return NT;
    }

    public void setNT(String NT) {
        this.NT = NT;
    }

    public String getADAG() {
        return ADAG;
    }

    public void setADAG(String ADAG) {
        this.ADAG = ADAG;
    }

    public double getLGTD() {
        return LGTD;
    }

    public void setLGTD(double LGTD) {
        this.LGTD = LGTD;
    }

    public String getSWFCD() {
        return SWFCD;
    }

    public void setSWFCD(String SWFCD) {
        this.SWFCD = SWFCD;
    }

    public String getADCD() {
        return ADCD;
    }

    public void setADCD(String ADCD) {
        this.ADCD = ADCD;
    }

    public String getSWFNM() {
        return SWFNM;
    }

    public void setSWFNM(String SWFNM) {
        this.SWFNM = SWFNM;
    }

    public double getLTTD() {
        return LTTD;
    }

    public void setLTTD(double LTTD) {
        this.LTTD = LTTD;
    }

    public String getMNTM() {
        return MNTM;
    }

    public void setMNTM(String MNTM) {
        this.MNTM = MNTM;
    }

    public TakeWater() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.Q);
        dest.writeString(this.SUMQ);
        dest.writeString(this.NT);
        dest.writeString(this.ADAG);
        dest.writeDouble(this.LGTD);
        dest.writeString(this.SWFCD);
        dest.writeString(this.ADCD);
        dest.writeString(this.SWFNM);
        dest.writeDouble(this.LTTD);
        dest.writeString(this.MNTM);
        dest.writeString(this.NFWUSE);
        dest.writeString(this.ADNM);
        dest.writeString(this.DYFWQ);
        dest.writeString(this.REACH_NAME);
    }

    protected TakeWater(Parcel in) {
        this.Q = in.readDouble();
        this.SUMQ = in.readString();
        this.NT = in.readString();
        this.ADAG = in.readString();
        this.LGTD = in.readDouble();
        this.SWFCD = in.readString();
        this.ADCD = in.readString();
        this.SWFNM = in.readString();
        this.LTTD = in.readDouble();
        this.MNTM = in.readString();
        this.NFWUSE = in.readString();
        this.ADNM = in.readString();
        this.DYFWQ = in.readString();
        this.REACH_NAME = in.readString();
    }

    public static final Creator<TakeWater> CREATOR = new Creator<TakeWater>() {
        @Override
        public TakeWater createFromParcel(Parcel source) {
            return new TakeWater(source);
        }

        @Override
        public TakeWater[] newArray(int size) {
            return new TakeWater[size];
        }
    };
}
