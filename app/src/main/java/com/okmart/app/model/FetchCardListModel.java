package com.okmart.app.model;

import java.io.Serializable;
import java.util.List;

public class FetchCardListModel {

    /**
     * object : list
     * url : /v1/customers/cus_EZf9AfhQ6rnWYQ/sources
     * has_more : false
     * data : [{"id":"card_1E6VovCMlSeWl0mJZAB6e4u3","object":"card","address_city":null,"address_country":null,"address_line1":null,"address_line1_check":null,"address_line2":null,"address_state":null,"address_zip":null,"address_zip_check":null,"brand":"Visa","country":"US","customer":"cus_EZf9AfhQ6rnWYQ","cvc_check":null,"dynamic_last4":null,"exp_month":8,"exp_year":2020,"fingerprint":"acy0NRICehJwhCmK","funding":"credit","last4":"4242","metadata":{},"name":null,"tokenization_method":null}]
     */

    private String object;
    private String url;
    private boolean has_more;
    private List<DataBean> data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }




    public static class DataBean implements Serializable {
        /**
         * id : card_1E6VovCMlSeWl0mJZAB6e4u3
         * object : card
         * address_city : null
         * address_country : null
         * address_line1 : null
         * address_line1_check : null
         * address_line2 : null
         * address_state : null
         * address_zip : null
         * address_zip_check : null
         * brand : Visa
         * country : US
         * customer : cus_EZf9AfhQ6rnWYQ
         * cvc_check : null
         * dynamic_last4 : null
         * exp_month : 8
         * exp_year : 2020
         * fingerprint : acy0NRICehJwhCmK
         * funding : credit
         * last4 : 4242
         * metadata : {}
         * name : null
         * tokenization_method : null
         */

        private String id;
        private String object;
        private Object address_city;
        private Object address_country;
        private Object address_line1;
        private Object address_line1_check;
        private Object address_line2;
        private Object address_state;
        private Object address_zip;
        private Object address_zip_check;
        private String brand;
        private String country;
        private String customer;
        private Object cvc_check;
        private Object dynamic_last4;
        private int exp_month;
        private int exp_year;
        private String fingerprint;
        private String funding;
        private String last4;
        private MetadataBean metadata;
        private Object name;
        private Object tokenization_method;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Object getAddress_city() {
            return address_city;
        }

        public void setAddress_city(Object address_city) {
            this.address_city = address_city;
        }

        public Object getAddress_country() {
            return address_country;
        }

        public void setAddress_country(Object address_country) {
            this.address_country = address_country;
        }

        public Object getAddress_line1() {
            return address_line1;
        }

        public void setAddress_line1(Object address_line1) {
            this.address_line1 = address_line1;
        }

        public Object getAddress_line1_check() {
            return address_line1_check;
        }

        public void setAddress_line1_check(Object address_line1_check) {
            this.address_line1_check = address_line1_check;
        }

        public Object getAddress_line2() {
            return address_line2;
        }

        public void setAddress_line2(Object address_line2) {
            this.address_line2 = address_line2;
        }

        public Object getAddress_state() {
            return address_state;
        }

        public void setAddress_state(Object address_state) {
            this.address_state = address_state;
        }

        public Object getAddress_zip() {
            return address_zip;
        }

        public void setAddress_zip(Object address_zip) {
            this.address_zip = address_zip;
        }

        public Object getAddress_zip_check() {
            return address_zip_check;
        }

        public void setAddress_zip_check(Object address_zip_check) {
            this.address_zip_check = address_zip_check;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public Object getCvc_check() {
            return cvc_check;
        }

        public void setCvc_check(Object cvc_check) {
            this.cvc_check = cvc_check;
        }

        public Object getDynamic_last4() {
            return dynamic_last4;
        }

        public void setDynamic_last4(Object dynamic_last4) {
            this.dynamic_last4 = dynamic_last4;
        }

        public int getExp_month() {
            return exp_month;
        }

        public void setExp_month(int exp_month) {
            this.exp_month = exp_month;
        }

        public int getExp_year() {
            return exp_year;
        }

        public void setExp_year(int exp_year) {
            this.exp_year = exp_year;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public String getFunding() {
            return funding;
        }

        public void setFunding(String funding) {
            this.funding = funding;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public MetadataBean getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataBean metadata) {
            this.metadata = metadata;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getTokenization_method() {
            return tokenization_method;
        }

        public void setTokenization_method(Object tokenization_method) {
            this.tokenization_method = tokenization_method;
        }

        public static class MetadataBean {
        }
    }
}
