package main.entity;

import java.util.UUID;

public class Bank {
    private BankId bankId;

    public BankId getBankId() {
        return bankId;
    }

    public void setBankId(BankId bankId) {
        this.bankId = bankId;
    }

    public record BankId(UUID client_id, UUID credit_id) {}
}
