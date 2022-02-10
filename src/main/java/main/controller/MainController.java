package main.controller;

import main.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        return "index";
    }
}
