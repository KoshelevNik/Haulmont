package main.entity;

import java.util.UUID;

public class Credit {

    private UUID id;
    private Integer limit;
    private Float interest_rate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Float getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(Float interest_rate) {
        this.interest_rate = interest_rate;
    }
}
