package main.controller;

import main.dao.LolDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    private LolDAO lolDAO;

    @GetMapping("/")
    public String indexGET(Model model) {
        model.addAttribute("lol", lolDAO.read(UUID.fromString("f568a576-876f-11ec-a8a3-0242ac120002")).getName());
        return "index";
    }
}
