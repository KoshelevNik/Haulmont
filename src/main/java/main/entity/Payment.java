package main.entity;

import java.util.Calendar;
import java.util.UUID;

public class Payment {

    private Calendar payment_date;
    private Integer payment_amount, credit_body, percent, remainder;
    private UUID id;

    public Integer getRemainder() {
        return remainder;
    }

    public void setRemainder(Integer remainder) {
        this.remainder = remainder;
    }

    public Calendar getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(Calendar payment_date) {
        this.payment_date = payment_date;
    }

    public Integer getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(Integer payment_amount) {
        this.payment_amount = payment_amount;
    }

    public Integer getCredit_body() {
        return credit_body;
    }

    public void setCredit_body(Integer credit_body) {
        this.credit_body = credit_body;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
