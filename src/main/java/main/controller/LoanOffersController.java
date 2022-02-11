package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class LoanOffersController {

    @GetMapping("/loanOffers")
    public String loanOffersGET(Model model, Principal principal) {
        return "loanOffers";
    }

    @PostMapping("/loanOffers")
    public String loanOffersPOST() {
        return "loanOffers";
    }
}
