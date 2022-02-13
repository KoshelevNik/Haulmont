package main.entity;

import java.util.Objects;
import java.util.UUID;

public class Credit {

    private UUID id;
    private Integer limit;
    private Float interest_rate;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credit credit = (Credit) o;
        return Objects.equals(id, credit.id) && Objects.equals(limit, credit.limit) && Objects.equals(interest_rate, credit.interest_rate) && Objects.equals(name, credit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, limit, interest_rate, name);
    }
}
