package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Superman on 2018/9/13.
 */

public class AdcdTree implements Parcelable {

    /**
     * id : 341502000000
     * name : 金安区
     * pId :
     */

    private String id;
    private String name;
    private String pId;
    private List<AdcdTree>children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public List<AdcdTree> getChildren() {
        return children;
    }

    public void setChildren(List<AdcdTree> children) {
        this.children = children;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.pId);
        dest.writeList(this.children);
    }

    public AdcdTree() {
    }

    protected AdcdTree(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.pId = in.readString();
        this.children = new ArrayList<AdcdTree>();
        in.readList(this.children, AdcdTree.class.getClassLoader());
    }

    public static final Parcelable.Creator<AdcdTree> CREATOR = new Parcelable.Creator<AdcdTree>() {
        @Override
        public AdcdTree createFromParcel(Parcel source) {
            return new AdcdTree(source);
        }

        @Override
        public AdcdTree[] newArray(int size) {
            return new AdcdTree[size];
        }
    };
}
