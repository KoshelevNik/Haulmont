package main.services;

import main.dao.PaymentDAO;
import main.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    public static Payment[] payments;

    @Autowired
    private PaymentDAO paymentDAO;

    public void create(Payment t) {
        paymentDAO.create(t);
    }

    public Optional<Payment> read(UUID uuid) {
        return paymentDAO.read(uuid);
    }

    public void update(Payment t) {
        paymentDAO.update(t);
    }

    public void delete(Payment t) {
        paymentDAO.delete(t);
    }

    public List<Payment> findAll() {
        return paymentDAO.findAll();
    }

    public boolean idExistInDatabase(UUID uuid) {
        return read(uuid).isPresent();
    }

    public void createAllFromArray(Payment[] payments) {
        for (Payment p : payments)
            create(p);
    }
}
