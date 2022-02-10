package main.services;

import main.dao.CreditDAO;
import main.entity.Credit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditService {

    @Autowired
    private CreditDAO creditDAO;

    public void create(Credit t) {
        creditDAO.create(t);
    }

    public Optional<Credit> read(UUID uuid) {
        return creditDAO.read(uuid);
    }

    public void update(Credit t) {
        creditDAO.update(t);
    }

    public void delete(Credit t) {
        creditDAO.delete(t);
    }

    public List<Credit> findAll() {
        return creditDAO.findAll();
    }
}
