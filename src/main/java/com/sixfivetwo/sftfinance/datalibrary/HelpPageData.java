package com.sixfivetwo.sftfinance.datalibrary;

import java.util.Map;

public class HelpPageData {
    public String front;
    public String comment1;
    public String comment2;
    public String comment3;
    public String comment4;
    public String comment5;
    public String comment6;
    public String comment7;
    public String comment8;
    public String comment9;
    public String comment10;

    public HelpPageData(Map<Integer, String> FileMap) {
        front = FileMap.get(1);
        comment1 = FileMap.get(2);
        comment2 = FileMap.get(3);
        comment3 = FileMap.get(4);
        comment4 = FileMap.get(5);
        comment5 = FileMap.get(6);
        comment6 = FileMap.get(7);
        comment7 = FileMap.get(8);
        comment8 = FileMap.get(9);
        comment9 = FileMap.get(10);
        comment10 = FileMap.get(11);
    }
}
