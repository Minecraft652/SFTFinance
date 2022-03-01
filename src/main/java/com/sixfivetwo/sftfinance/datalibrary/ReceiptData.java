package com.sixfivetwo.sftfinance.datalibrary;

public class ReceiptData {
    public String type;
    public String fromAddress;
    public String toAddress;
    public String value;
    public String gasLimit;
    public String gasPrice;

    public ReceiptData(String type, String fromAddress, String toAddress, String value, String gasLimit, String gasPrice) {
        this.type = type;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.value = value;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }
}
