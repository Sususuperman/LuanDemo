package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Superman on 2018/7/17.
 */

public class YuJing implements Parcelable {

    /**
     * WARNCITY : 341502000000
     * WARNPEOPLE : 西淠河测站
     * WARNINFOCODE : 10028
     * TIME : 2018-07-06 13:51:00
     * WARNTYPEDES : 雨量预警
     * WARNTHEME : 西淠河测站预警
     * WARNCONTENT : 测试
     * WARNSOUCRE:人工预警 1   自动预警 空
     */

    private String WARNCITY;
    private String WARNPEOPLE;
    private int WARNINFOCODE;
    private String TIME;
    private String ADNM;

    public String getADNM() {
        return ADNM;
    }

    public void setADNM(String ADNM) {
        this.ADNM = ADNM;
    }

    private String WARNTYPEDES;
    private String WARNTHEME;
    private String WARNCONTENT;
    private String WARNSOUCRE;

    public String getWARNSOUCRE() {
        return WARNSOUCRE;
    }

    public void setWARNSOUCRE(String WARNSOUCRE) {
        this.WARNSOUCRE = WARNSOUCRE;
    }

    public String getWARNCITY() {
        return WARNCITY;
    }

    public void setWARNCITY(String WARNCITY) {
        this.WARNCITY = WARNCITY;
    }

    public String getWARNPEOPLE() {
        return WARNPEOPLE;
    }

    public void setWARNPEOPLE(String WARNPEOPLE) {
        this.WARNPEOPLE = WARNPEOPLE;
    }

    public int getWARNINFOCODE() {
        return WARNINFOCODE;
    }

    public void setWARNINFOCODE(int WARNINFOCODE) {
        this.WARNINFOCODE = WARNINFOCODE;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getWARNTYPEDES() {
        return WARNTYPEDES;
    }

    public void setWARNTYPEDES(String WARNTYPEDES) {
        this.WARNTYPEDES = WARNTYPEDES;
    }

    public String getWARNTHEME() {
        return WARNTHEME;
    }

    public void setWARNTHEME(String WARNTHEME) {
        this.WARNTHEME = WARNTHEME;
    }

    public String getWARNCONTENT() {
        return WARNCONTENT;
    }

    public void setWARNCONTENT(String WARNCONTENT) {
        this.WARNCONTENT = WARNCONTENT;
    }

    public YuJing() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.WARNCITY);
        dest.writeString(this.WARNPEOPLE);
        dest.writeInt(this.WARNINFOCODE);
        dest.writeString(this.TIME);
        dest.writeString(this.ADNM);
        dest.writeString(this.WARNTYPEDES);
        dest.writeString(this.WARNTHEME);
        dest.writeString(this.WARNCONTENT);
        dest.writeString(this.WARNSOUCRE);
    }

    protected YuJing(Parcel in) {
        this.WARNCITY = in.readString();
        this.WARNPEOPLE = in.readString();
        this.WARNINFOCODE = in.readInt();
        this.TIME = in.readString();
        this.ADNM = in.readString();
        this.WARNTYPEDES = in.readString();
        this.WARNTHEME = in.readString();
        this.WARNCONTENT = in.readString();
        this.WARNSOUCRE = in.readString();
    }

    public static final Creator<YuJing> CREATOR = new Creator<YuJing>() {
        @Override
        public YuJing createFromParcel(Parcel source) {
            return new YuJing(source);
        }

        @Override
        public YuJing[] newArray(int size) {
            return new YuJing[size];
        }
    };
}
