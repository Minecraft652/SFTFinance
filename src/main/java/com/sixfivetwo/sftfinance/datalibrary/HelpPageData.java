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

    public HelpPageData() {}

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

    public String getFront() {
        return front;
    }

    public String getComment1() {
        return comment1;
    }

    public String getComment2() {
        return comment2;
    }

    public String getComment3() {
        return comment3;
    }

    public String getComment4() {
        return comment4;
    }

    public String getComment5() {
        return comment5;
    }

    public String getComment6() {
        return comment6;
    }

    public String getComment7() {
        return comment7;
    }

    public String getComment8() {
        return comment8;
    }

    public String getComment9() {
        return comment9;
    }

    public String getComment10() {
        return comment10;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public void setComment3(String comment3) {
        this.comment3 = comment3;
    }

    public void setComment4(String comment4) {
        this.comment4 = comment4;
    }

    public void setComment5(String comment5) {
        this.comment5 = comment5;
    }

    public void setComment6(String comment6) {
        this.comment6 = comment6;
    }

    public void setComment7(String comment7) {
        this.comment7 = comment7;
    }

    public void setComment8(String comment8) {
        this.comment8 = comment8;
    }

    public void setComment9(String comment9) {
        this.comment9 = comment9;
    }

    public void setComment10(String comment10) {
        this.comment10 = comment10;
    }
}
