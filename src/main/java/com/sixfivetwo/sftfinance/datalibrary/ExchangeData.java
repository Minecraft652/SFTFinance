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
}
