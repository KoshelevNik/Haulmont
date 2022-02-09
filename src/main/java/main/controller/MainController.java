package main.controller;

import main.dao.*;
import main.entity.LoanOffer;
import main.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private CreditDAO creditDAO;

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private BankDAO bankDAO;

    @Autowired
    private LoanOfferDAO loanOfferDAO;

    @GetMapping("/")
    public String indexGET(Model model) {
        User user = new User();
        user.setRole("user");
        user.setMail("lol");
        user.setId(UUID.randomUUID());
        user.setPassword_hash("$2a$12$8/0HFu362zwPsEN5TS0HsutK.9WBNCXgY2YC4Lgd5o.DEGJuZ8iZO");
        userDAO.create(user);
        model.addAttribute("lol", userDAO.findAll());
        return "index";
    }
}
