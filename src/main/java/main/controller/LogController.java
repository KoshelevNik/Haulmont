package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LogController {

    @GetMapping("/login")
    public String loginGET(@RequestParam Optional<String> error, Model model) {
        model.addAttribute("error", (error.isPresent()) ? "Введены неверные данные": "");
        return "login";
    }
}
