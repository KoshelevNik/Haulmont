package main.services;

import main.dao.BankDAO;
import main.entity.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    @Autowired
    private BankDAO bankDAO;

    public void create(Bank t) {
        bankDAO.create(t);
    }

    public Optional<Bank> read(Bank.BankId bankId) {
        return bankDAO.read(bankId);
    }

    public void delete(Bank t) {
        bankDAO.delete(t);
    }

    public List<Bank> findAll() {
        return bankDAO.findAll();
    }
}
