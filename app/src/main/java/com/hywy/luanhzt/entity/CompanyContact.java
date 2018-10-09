package com.hywy.luanhzt.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 协作单位通讯录
 *
 * @author Superman
 * @date 2018/7/24
 */

public class CompanyContact implements Parcelable {

    private String ADNM;
    private String ADCD;
    private List<DealsBean> deals;

    public String getADNM() {
        return ADNM;
    }

    public void setADNM(String ADNM) {
        this.ADNM = ADNM;
    }

    public String getADCD() {
        return ADCD;
    }

    public void setADCD(String ADCD) {
        this.ADCD = ADCD;
    }

    public List<DealsBean> getDeals() {
        return deals;
    }

    public void setDeals(List<DealsBean> deals) {
        this.deals = deals;
    }

    public static class DealsBean implements Parcelable {

        /**
         * DEPT_NAME : 霍山县城管执法局
         * AREA : 341500000000
         * PHONE : 18788888888
         * NAME : 市住建委
         */

        private String DEPT_NAME;
        private String AREA;
        private String PHONE;
        private String NAME;
        private String ADNM;
        private String ID;

        public String getADNM() {
            return ADNM;
        }

        public void setADNM(String ADNM) {
            this.ADNM = ADNM;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getDEPT_NAME() {
            return DEPT_NAME;
        }

        public void setDEPT_NAME(String DEPT_NAME) {
            this.DEPT_NAME = DEPT_NAME;
        }

        public String getAREA() {
            return AREA;
        }

        public void setAREA(String AREA) {
            this.AREA = AREA;
        }

        public String getPHONE() {
            return PHONE;
        }

        public void setPHONE(String PHONE) {
            this.PHONE = PHONE;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public DealsBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.DEPT_NAME);
            dest.writeString(this.AREA);
            dest.writeString(this.PHONE);
            dest.writeString(this.NAME);
            dest.writeString(this.ADNM);
            dest.writeString(this.ID);
        }

        protected DealsBean(Parcel in) {
            this.DEPT_NAME = in.readString();
            this.AREA = in.readString();
            this.PHONE = in.readString();
            this.NAME = in.readString();
            this.ADNM = in.readString();
            this.ID = in.readString();
        }

        public static final Creator<DealsBean> CREATOR = new Creator<DealsBean>() {
            @Override
            public DealsBean createFromParcel(Parcel source) {
                return new DealsBean(source);
            }

            @Override
            public DealsBean[] newArray(int size) {
                return new DealsBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ADNM);
        dest.writeString(this.ADCD);
        dest.writeTypedList(this.deals);
    }

    public CompanyContact() {
    }

    protected CompanyContact(Parcel in) {
        this.ADNM = in.readString();
        this.ADCD = in.readString();
        this.deals = in.createTypedArrayList(DealsBean.CREATOR);
    }

    public static final Creator<CompanyContact> CREATOR = new Creator<CompanyContact>() {
        @Override
        public CompanyContact createFromParcel(Parcel source) {
            return new CompanyContact(source);
        }

        @Override
        public CompanyContact[] newArray(int size) {
            return new CompanyContact[size];
        }
    };
}
