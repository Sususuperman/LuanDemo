package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 行政区划图层信息
 * Created by Superman on 2018/8/28.
 */

public class AdcdLayerInfo implements Parcelable {
    private int TYPE;
    private String URL;
    private String NAME;
    private String LTTD;
    private String LGTD;
    private String NT;//地图服务类型，1为影像图层

    public String getNT() {
        return NT;
    }

    public void setNT(String NT) {
        this.NT = NT;
    }

    public String getLTTD() {
        return LTTD;
    }

    public void setLTTD(String LTTD) {
        this.LTTD = LTTD;
    }

    public String getLGTD() {
        return LGTD;
    }

    public void setLGTD(String LGTD) {
        this.LGTD = LGTD;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.TYPE);
        dest.writeString(this.URL);
        dest.writeString(this.NAME);
        dest.writeString(this.LTTD);
        dest.writeString(this.LGTD);
        dest.writeString(this.NT);
    }

    public AdcdLayerInfo() {
    }

    protected AdcdLayerInfo(Parcel in) {
        this.TYPE = in.readInt();
        this.URL = in.readString();
        this.NAME = in.readString();
        this.LTTD = in.readString();
        this.LGTD = in.readString();
        this.NT = in.readString();
    }

    public static final Parcelable.Creator<AdcdLayerInfo> CREATOR = new Parcelable.Creator<AdcdLayerInfo>() {
        @Override
        public AdcdLayerInfo createFromParcel(Parcel source) {
            return new AdcdLayerInfo(source);
        }

        @Override
        public AdcdLayerInfo[] newArray(int size) {
            return new AdcdLayerInfo[size];
        }
    };
}
