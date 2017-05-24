package com.quartzodev.api;

import java.util.Date;

/**
 * Created by victoraldir on 20/05/2017.
 */

public class Lend {
    public String receiverName;
    public String receiverEmail;
    public Date lendDate;

    public Lend() {

    }

    public Lend(String receiverName, String receiverEmail, Date lendDate) {
        this.receiverName = receiverName;
        this.receiverEmail = receiverEmail;
        this.lendDate = lendDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Date getLendDate() {
        return lendDate;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }
}
