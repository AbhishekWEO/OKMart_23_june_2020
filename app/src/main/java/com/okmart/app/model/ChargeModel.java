package com.okmart.app.model;

import java.util.List;

public class ChargeModel {



    private String id;
    private String object;
    private int amount;
    private int amount_refunded;
    private Object application;
    private Object application_fee;
    private Object application_fee_amount;
    private String balance_transaction;
    private BillingDetailsBean billing_details;
    private boolean captured;
    private int created;
    private String currency;
    private String customer;
    private Object description;
    private Object destination;
    private Object dispute;
    private Object failure_code;
    private Object failure_message;
    private FraudDetailsBean fraud_details;
    private String invoice;
    private boolean livemode;
    private MetadataBean metadata;
    private Object on_behalf_of;
    private Object order;
    private OutcomeBean outcome;
    private boolean paid;
    private Object payment_intent;
    private PaymentMethodDetailsBean payment_method_details;
    private Object receipt_email;
    private String receipt_number;
    private String receipt_url;
    private boolean refunded;
    private RefundsBean refunds;
    private Object review;
    private Object shipping;
    private SourceBean source;
    private Object source_transfer;
    private Object statement_descriptor;
    private String status;
    private Object transfer_data;
    private Object transfer_group;

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount_refunded() {
        return amount_refunded;
    }

    public void setAmount_refunded(int amount_refunded) {
        this.amount_refunded = amount_refunded;
    }

    public Object getApplication() {
        return application;
    }

    public void setApplication(Object application) {
        this.application = application;
    }

    public Object getApplication_fee() {
        return application_fee;
    }

    public void setApplication_fee(Object application_fee) {
        this.application_fee = application_fee;
    }

    public Object getApplication_fee_amount() {
        return application_fee_amount;
    }

    public void setApplication_fee_amount(Object application_fee_amount) {
        this.application_fee_amount = application_fee_amount;
    }

    public String getBalance_transaction() {
        return balance_transaction;
    }

    public void setBalance_transaction(String balance_transaction) {
        this.balance_transaction = balance_transaction;
    }

    public BillingDetailsBean getBilling_details() {
        return billing_details;
    }

    public void setBilling_details(BillingDetailsBean billing_details) {
        this.billing_details = billing_details;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getDestination() {
        return destination;
    }

    public void setDestination(Object destination) {
        this.destination = destination;
    }

    public Object getDispute() {
        return dispute;
    }

    public void setDispute(Object dispute) {
        this.dispute = dispute;
    }

    public Object getFailure_code() {
        return failure_code;
    }

    public void setFailure_code(Object failure_code) {
        this.failure_code = failure_code;
    }

    public Object getFailure_message() {
        return failure_message;
    }

    public void setFailure_message(Object failure_message) {
        this.failure_message = failure_message;
    }

    public FraudDetailsBean getFraud_details() {
        return fraud_details;
    }

    public void setFraud_details(FraudDetailsBean fraud_details) {
        this.fraud_details = fraud_details;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public boolean isLivemode() {
        return livemode;
    }

    public void setLivemode(boolean livemode) {
        this.livemode = livemode;
    }

    public MetadataBean getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataBean metadata) {
        this.metadata = metadata;
    }

    public Object getOn_behalf_of() {
        return on_behalf_of;
    }

    public void setOn_behalf_of(Object on_behalf_of) {
        this.on_behalf_of = on_behalf_of;
    }

    public Object getOrder() {
        return order;
    }

    public void setOrder(Object order) {
        this.order = order;
    }

    public OutcomeBean getOutcome() {
        return outcome;
    }

    public void setOutcome(OutcomeBean outcome) {
        this.outcome = outcome;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Object getPayment_intent() {
        return payment_intent;
    }

    public void setPayment_intent(Object payment_intent) {
        this.payment_intent = payment_intent;
    }

    public PaymentMethodDetailsBean getPayment_method_details() {
        return payment_method_details;
    }

    public void setPayment_method_details(PaymentMethodDetailsBean payment_method_details) {
        this.payment_method_details = payment_method_details;
    }

    public Object getReceipt_email() {
        return receipt_email;
    }

    public void setReceipt_email(Object receipt_email) {
        this.receipt_email = receipt_email;
    }

    public String getReceipt_number() {
        return receipt_number;
    }

    public void setReceipt_number(String receipt_number) {
        this.receipt_number = receipt_number;
    }

    public String getReceipt_url() {
        return receipt_url;
    }

    public void setReceipt_url(String receipt_url) {
        this.receipt_url = receipt_url;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    public RefundsBean getRefunds() {
        return refunds;
    }

    public void setRefunds(RefundsBean refunds) {
        this.refunds = refunds;
    }

    public Object getReview() {
        return review;
    }

    public void setReview(Object review) {
        this.review = review;
    }

    public Object getShipping() {
        return shipping;
    }

    public void setShipping(Object shipping) {
        this.shipping = shipping;
    }

    public SourceBean getSource() {
        return source;
    }

    public void setSource(SourceBean source) {
        this.source = source;
    }

    public Object getSource_transfer() {
        return source_transfer;
    }

    public void setSource_transfer(Object source_transfer) {
        this.source_transfer = source_transfer;
    }

    public Object getStatement_descriptor() {
        return statement_descriptor;
    }

    public void setStatement_descriptor(Object statement_descriptor) {
        this.statement_descriptor = statement_descriptor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getTransfer_data() {
        return transfer_data;
    }

    public void setTransfer_data(Object transfer_data) {
        this.transfer_data = transfer_data;
    }

    public Object getTransfer_group() {
        return transfer_group;
    }

    public void setTransfer_group(Object transfer_group) {
        this.transfer_group = transfer_group;
    }

    public static class BillingDetailsBean {
    }

    public static class FraudDetailsBean {
    }

    public static class MetadataBean {
    }

    public static class OutcomeBean {
        /**
         * network_status : approved_by_network
         * reason : null
         * risk_level : normal
         * seller_message : Payment complete.
         * type : authorized
         */

        private String network_status;
        private Object reason;
        private String risk_level;
        private String seller_message;
        private String type;

        public String getNetwork_status() {
            return network_status;
        }

        public void setNetwork_status(String network_status) {
            this.network_status = network_status;
        }

        public Object getReason() {
            return reason;
        }

        public void setReason(Object reason) {
            this.reason = reason;
        }

        public String getRisk_level() {
            return risk_level;
        }

        public void setRisk_level(String risk_level) {
            this.risk_level = risk_level;
        }

        public String getSeller_message() {
            return seller_message;
        }

        public void setSeller_message(String seller_message) {
            this.seller_message = seller_message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class PaymentMethodDetailsBean {
        /**
         * card : {"brand":"Visa","checks":{"address_line1_check":null,"address_postal_code_check":null,"cvc_check":null},"country":"US","exp_month":12,"exp_year":2017,"fingerprint":"Xt5EWLLDS7FJjR1c","funding":"credit","last4":"4242","three_d_secure":null,"wallet":null}
         * type : card
         */

        private CardBean card;
        private String type;

        public CardBean getCard() {
            return card;
        }

        public void setCard(CardBean card) {
            this.card = card;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static class CardBean {
            /**
             * brand : Visa
             * checks : {"address_line1_check":null,"address_postal_code_check":null,"cvc_check":null}
             * country : US
             * exp_month : 12
             * exp_year : 2017
             * fingerprint : Xt5EWLLDS7FJjR1c
             * funding : credit
             * last4 : 4242
             * three_d_secure : null
             * wallet : null
             */

            private String brand;
            private ChecksBean checks;
            private String country;
            private int exp_month;
            private int exp_year;
            private String fingerprint;
            private String funding;
            private String last4;
            private Object three_d_secure;
            private Object wallet;

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public ChecksBean getChecks() {
                return checks;
            }

            public void setChecks(ChecksBean checks) {
                this.checks = checks;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
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

            public Object getThree_d_secure() {
                return three_d_secure;
            }

            public void setThree_d_secure(Object three_d_secure) {
                this.three_d_secure = three_d_secure;
            }

            public Object getWallet() {
                return wallet;
            }

            public void setWallet(Object wallet) {
                this.wallet = wallet;
            }

            public static class ChecksBean {
                /**
                 * address_line1_check : null
                 * address_postal_code_check : null
                 * cvc_check : null
                 */

                private Object address_line1_check;
                private Object address_postal_code_check;
                private Object cvc_check;

                public Object getAddress_line1_check() {
                    return address_line1_check;
                }

                public void setAddress_line1_check(Object address_line1_check) {
                    this.address_line1_check = address_line1_check;
                }

                public Object getAddress_postal_code_check() {
                    return address_postal_code_check;
                }

                public void setAddress_postal_code_check(Object address_postal_code_check) {
                    this.address_postal_code_check = address_postal_code_check;
                }

                public Object getCvc_check() {
                    return cvc_check;
                }

                public void setCvc_check(Object cvc_check) {
                    this.cvc_check = cvc_check;
                }
            }
        }
    }

    public static class RefundsBean {
        /**
         * object : list
         * data : []
         * has_more : false
         * total_count : 0
         * url : /v1/charges/ch_19yUcN2eZvKYlo2CHeoY9G8U/refunds
         */

        private String object;
        private boolean has_more;
        private int total_count;
        private String url;
        private List<?> data;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public boolean isHas_more() {
            return has_more;
        }

        public void setHas_more(boolean has_more) {
            this.has_more = has_more;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<?> getData() {
            return data;
        }

        public void setData(List<?> data) {
            this.data = data;
        }
    }

    public static class SourceBean {
        /**
         * id : card_18CGkW2eZvKYlo2CUOSz0g1Q
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
         * customer : cus_8TDBWGtDPzEQfx
         * cvc_check : null
         * dynamic_last4 : null
         * exp_month : 12
         * exp_year : 2017
         * fingerprint : Xt5EWLLDS7FJjR1c
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
        private MetadataBeanX metadata;
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

        public MetadataBeanX getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataBeanX metadata) {
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

        public static class MetadataBeanX {
        }
    }
}
