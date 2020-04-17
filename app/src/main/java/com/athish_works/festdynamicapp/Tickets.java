package com.athish_works.festdynamicapp;

public class Tickets {
    String name, contNo, gmail, event, txnNo, college;

    public Tickets() {
    }

    public Tickets(String name, String contNo, String college, String gmail, String event, String txnNo) {
        this.name = name;
        this.contNo = contNo;
        this.gmail = gmail;
        this.event = event;
        this.txnNo = txnNo;
        this.college = college;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTxnNo() {
        return txnNo;
    }

    public void setTxnNo(String txnNo) {
        this.txnNo = txnNo;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
