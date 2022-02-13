package main.services;

import main.dao.LoanOfferDAO;
import main.entity.LoanOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<LoanOffer> findAllByClientId(UUID clientId) {
        List<LoanOffer> allLoanOffer = findAll();
        List<LoanOffer> allById = new ArrayList<>();
        for (LoanOffer loanOffer : allLoanOffer) {
            if (loanOffer.getLoanOfferId().client_id().equals(clientId)) {
                allById.add(loanOffer);
            }
        }
        return allById;
    }

    public List<LoanOffer> findAllByCreditId(UUID creditId) {
        List<LoanOffer> allLoanOffer = findAll();
        List<LoanOffer> allById = new ArrayList<>();
        for (LoanOffer loanOffer : allLoanOffer) {
            if (loanOffer.getLoanOfferId().credit_id().equals(creditId)) {
                allById.add(loanOffer);
            }
        }
        return allById;
    }
}
