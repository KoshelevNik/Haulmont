package main.services;

import main.dao.LoanOfferDAO;
import main.entity.LoanOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanOfferService {

    @Autowired
    private LoanOfferDAO loanOfferDAO;

    public void create(LoanOffer t) {
        loanOfferDAO.create(t);
    }

    public Optional<LoanOffer> read(LoanOffer.LoanOfferId loanOfferId) {
        return loanOfferDAO.read(loanOfferId);
    }

    public void update(LoanOffer t) {
        loanOfferDAO.update(t);
    }

    public void delete(LoanOffer t) {
        loanOfferDAO.delete(t);
    }

    public List<LoanOffer> findAll() {
        return loanOfferDAO.findAll();
    }
}
