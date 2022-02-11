package main.controller;

import main.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class MainController {

    @GetMapping("/")
    public String indexGET(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("user", user);
        return "index";
    }

    @PostMapping("/")
    public String indexPOST() {
        return "index";
    }

    @GetMapping("/loanProcessing")
    public String loanProcessingGET(Model model, Principal principal) {
        return "loanProcessing";
    }

    @PostMapping("/loanProcessing")
    public String loanProcessingPOST() {
        return "loanProcessing";
    }

    @GetMapping("/paymentsGraph")
    public String paymentsGraphGET(Model model, Principal principal) {
        return "paymentsGraph";
    }

    @PostMapping("/paymentsGraph")
    public String paymentsGraphPOST() {
        return "paymentsGraph";
    }

    @GetMapping("/clients")
    public String clientsGET(Model model, Principal principal) {
        return "clients";
    }

    @PostMapping("/clients")
    public String clientsPOST() {
        return "clients";
    }
}
