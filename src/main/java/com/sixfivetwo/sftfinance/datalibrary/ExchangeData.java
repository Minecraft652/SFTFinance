package com.sixfivetwo.sftfinance.datalibrary;

import java.util.Map;

public class ExchangeData {
    public String tokentype;
    public String price;
    public String executecommand;
    public String executorisconsole;

    public ExchangeData(Map<Integer, String> FileMap) {
        tokentype = FileMap.get(1);
        price = FileMap.get(2);
        executecommand = FileMap.get(3);
        executorisconsole = FileMap.get(4);
    }

    public String getTokentype() {
        return tokentype;
    }

    public String getPrice() {
        return price;
    }

    public String getExecutecommand() {
        return executecommand;
    }

    public String getExecutorisconsole() {
        return executorisconsole;
    }

    public void setTokentype(String tokentype) {
        this.tokentype = tokentype;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setExecutecommand(String executecommand) {
        this.executecommand = executecommand;
    }

    public void setExecutorisconsole(String executorisconsole) {
        this.executorisconsole = executorisconsole;
    }
}
