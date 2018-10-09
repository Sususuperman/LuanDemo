package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Superman on 2018/8/29.
 * 用户树对象
 */

public class UserTree implements Parcelable {

    /**
     * ICON_URL : 341500000000
     * iconSkin :
     * id : 341500000000
     * name : 六安市
     * MENU_ORDER :
     * pId :
     * checked : false
     * parentMenu : null
     * NAME_CODE : 2
     * target : treeFrame
     */

    private String ICON_URL;
    private String iconSkin;
    private String id;
    private String name;
    private String MENU_ORDER;
    private String pId;
    private boolean checked;
    private String NAME_CODE;
    private String target;
    private List<UserTree> children;

    public String getICON_URL() {
        return ICON_URL;
    }

    public void setICON_URL(String ICON_URL) {
        this.ICON_URL = ICON_URL;
    }

    public String getIconSkin() {
        return iconSkin;
    }

    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

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

    public String getMENU_ORDER() {
        return MENU_ORDER;
    }

    public void setMENU_ORDER(String MENU_ORDER) {
        this.MENU_ORDER = MENU_ORDER;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public String getNAME_CODE() {
        return NAME_CODE;
    }

    public void setNAME_CODE(String NAME_CODE) {
        this.NAME_CODE = NAME_CODE;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public List<UserTree> getChildren() {
        return children;
    }

    public void setChildren(List<UserTree> children) {
        this.children = children;
    }

    public UserTree() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ICON_URL);
        dest.writeString(this.iconSkin);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.MENU_ORDER);
        dest.writeString(this.pId);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeString(this.NAME_CODE);
        dest.writeString(this.target);
        dest.writeTypedList(this.children);
    }

    protected UserTree(Parcel in) {
        this.ICON_URL = in.readString();
        this.iconSkin = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.MENU_ORDER = in.readString();
        this.pId = in.readString();
        this.checked = in.readByte() != 0;
        this.NAME_CODE = in.readString();
        this.target = in.readString();
        this.children = in.createTypedArrayList(UserTree.CREATOR);
    }

    public static final Creator<UserTree> CREATOR = new Creator<UserTree>() {
        @Override
        public UserTree createFromParcel(Parcel source) {
            return new UserTree(source);
        }

        @Override
        public UserTree[] newArray(int size) {
            return new UserTree[size];
        }
    };
}
