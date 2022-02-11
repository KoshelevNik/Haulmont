package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profileGET(Model model, Principal principal) {
        return "profile";
    }

    @PostMapping("/profile")
    public String profilePOST() {
        return "profile";
    }

    @GetMapping("/myCredits")
    public String myCreditsGET(Model model, Principal principal) {
        return "myCredits";
    }

    @PostMapping("/myCredits")
    public String myCreditsPOST() {
        return "myCredits";
    }

    @GetMapping("/changePassword")
    public String changePasswordGET(Model model, Principal principal) {
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePasswordPOST() {
        return "changePassword";
    }

    @GetMapping("/changeClientData")
    public String changeClientDataGET(Model model, Principal principal) {
        return "changeClientData";
    }

    @PostMapping("/changeClientData")
    public String changeClientDataPOST() {
        return "changeClientData";
    }
}
