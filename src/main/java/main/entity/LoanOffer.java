package main.entity;

import java.util.UUID;

public class LoanOffer {

    private LoanOfferId loanOfferId;
    private Integer credit_amount;
    private UUID[] payments_graph;
    private Boolean client_confirm, admin_confirm;

    public LoanOfferId getLoanOfferId() {
        return loanOfferId;
    }

    public void setLoanOfferId(LoanOfferId loanOfferId) {
        this.loanOfferId = loanOfferId;
    }

    public Integer getCredit_amount() {
        return credit_amount;
    }

    public void setCredit_amount(Integer credit_amount) {
        this.credit_amount = credit_amount;
    }

    public UUID[] getPayments_graph() {
        return payments_graph;
    }

    public void setPayments_graph(UUID[] payments_graph) {
        this.payments_graph = payments_graph;
    }

    public Boolean getClient_confirm() {
        return client_confirm;
    }

    public void setClient_confirm(Boolean client_confirm) {
        this.client_confirm = client_confirm;
    }

    public Boolean getAdmin_confirm() {
        return admin_confirm;
    }

    public void setAdmin_confirm(Boolean admin_confirm) {
        this.admin_confirm = admin_confirm;
    }

    public record LoanOfferId(UUID client_id, UUID credit_id) {}
}
