package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Superman on 2018/8/24.
 */

public class RiverFile implements Parcelable {

    /**
     * URL : 文档路径
     * FILE_NAME : 文件名称
     * 全名(包含后缀) FILE_FOLDER : 河道ID
     * HTML_URL : word转HTML后路径
     * FILE_TYPE : 文件类型
     * ID : 文件ID
     * TM : 上传时间
     */

    private String URL;
    private String FILE_NAME;
    @SerializedName("全名(包含后缀) FILE_FOLDER")
    private String _$FILE_FOLDER22; // FIXME check this code
    private String HTML_URL;
    private String FILE_TYPE;
    private String ID;
    private String TM;

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public String get_$FILE_FOLDER22() {
        return _$FILE_FOLDER22;
    }

    public void set_$FILE_FOLDER22(String _$FILE_FOLDER22) {
        this._$FILE_FOLDER22 = _$FILE_FOLDER22;
    }

    public String getHTML_URL() {
        return HTML_URL;
    }

    public void setHTML_URL(String HTML_URL) {
        this.HTML_URL = HTML_URL;
    }

    public String getFILE_TYPE() {
        return FILE_TYPE;
    }

    public void setFILE_TYPE(String FILE_TYPE) {
        this.FILE_TYPE = FILE_TYPE;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTM() {
        return TM;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.URL);
        dest.writeString(this.FILE_NAME);
        dest.writeString(this._$FILE_FOLDER22);
        dest.writeString(this.HTML_URL);
        dest.writeString(this.FILE_TYPE);
        dest.writeString(this.ID);
        dest.writeString(this.TM);
    }

    public RiverFile() {
    }

    protected RiverFile(Parcel in) {
        this.URL = in.readString();
        this.FILE_NAME = in.readString();
        this._$FILE_FOLDER22 = in.readString();
        this.HTML_URL = in.readString();
        this.FILE_TYPE = in.readString();
        this.ID = in.readString();
        this.TM = in.readString();
    }

    public static final Parcelable.Creator<RiverFile> CREATOR = new Parcelable.Creator<RiverFile>() {
        @Override
        public RiverFile createFromParcel(Parcel source) {
            return new RiverFile(source);
        }

        @Override
        public RiverFile[] newArray(int size) {
            return new RiverFile[size];
        }
    };
}
