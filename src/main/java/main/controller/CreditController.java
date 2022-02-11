package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CreditController {

    @GetMapping("/credits")
    public String creditsGET(Model model, Principal principal) {
        return "credits";
    }

    @PostMapping("/credits")
    public String creditsPOST() {
        return "credits";
    }

    @GetMapping("/createNewCredit")
    public String createNewCreditGET(Model model, Principal principal) {
        return "createNewCredit";
    }

    @PostMapping("/createNewCredit")
    public String createNewCreditPOST() {
        return "createNewCredit";
    }
}
