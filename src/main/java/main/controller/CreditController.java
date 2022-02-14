package main.controller;

import main.entity.Credit;
import main.services.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class CreditController {

    @Autowired
    private CreditService creditService;

    @GetMapping("/credits")
    public String creditsGET(Model model) {
        model.addAttribute("creditList", creditService.findAll());
        return "credits";
    }

    @PostMapping("/credits")
    public String creditsPOST(Model model, @RequestParam Map<String, String> param) {
        if (param.containsKey("search")) {
            List<Credit> creditList = creditService.findAll();
            creditList.sort(new Comparator<>() {
                @Override
                public int compare(Credit o1, Credit o2) {
                    return countOfCoincidences(o2.getName(), param.get("name")) -
                            countOfCoincidences(o1.getName(), param.get("name"));
                }

                private int countOfCoincidences(String s1, String s2) {
                    int maxCount = 0;
                    for (int i = 0; i < s1.length(); i++) {
                        int count = 0;
                        boolean start = false;
                        for (int j = 0; j < s2.length() && i + j < s1.length(); j++) {
                            boolean b = s1.toCharArray()[i + j] == s2.toCharArray()[j];
                            if (b && !start) {
                                start = true;
                                count++;
                            } else if (!b && start) {
                                break;
                            } else if (b) {
                                count++;
                            }
                        }
                        maxCount = Math.max(maxCount, count);
                    }
                    return maxCount;
                }
            });
            model.addAttribute("creditList", creditList);
            return "credits";
        } else {
            param.remove("name");
            String key = param.keySet().toArray()[0].toString();
            UUID creditId = UUID.fromString(key.split("=")[1]);
            if (key.startsWith("delete")) {
                creditService.delete(creditService.read(creditId).get());
                return "redirect:credits";
            } else {
                return "redirect:createNewCredit?id="+creditId;
            }
        }
    }

    @GetMapping("/createNewCredit")
    public String createNewCreditGET(Model model, @RequestParam Optional<String> id) {
        if (id.isPresent()) {
            Credit credit = creditService.read(UUID.fromString(id.get())).get();
            model.addAttribute("nameValue", credit.getName());
            model.addAttribute("limitValue", credit.getLimit());
            model.addAttribute("interestRateValue", credit.getInterest_rate());
            model.addAttribute("isUpdate", true);
            model.addAttribute("id", id);
        } else {
            model.addAttribute("nameValue", "");
            model.addAttribute("limitValue", "");
            model.addAttribute("interestRateValue", "");
            model.addAttribute("isUpdate", false);
        }
        model.addAttribute("nameError", "");
        model.addAttribute("limitError", "");
        model.addAttribute("interestRateError", "");
        return "createNewCredit";
    }

    @PostMapping("/createNewCredit")
    public String createNewCreditPOST(@RequestParam Map<String, String> params, Model model) {
        if (
                !params.get("name").isEmpty() &&
                        !params.get("limit").isEmpty() &&
                        !params.get("interestRate").isEmpty() &&
                        Integer.parseInt(params.get("limit")) > 0 &&
                        Float.parseFloat(params.get("interestRate")) > 0
        ) {
            Credit credit;
            if (params.containsKey("id")) {
                credit = creditService.read(UUID.fromString(params.get("id"))).get();
            } else {
                credit = new Credit();
                credit.setId(UUID.randomUUID());
                while (creditService.idExistInDatabase(credit.getId()))
                    credit.setId(UUID.randomUUID());
            }
            credit.setName(params.get("name"));
            credit.setLimit(Integer.parseInt(params.get("limit")));
            credit.setInterest_rate(Float.parseFloat(params.get("interestRate")));
            if (params.containsKey("id")) {
                creditService.update(credit);
            } else {
                creditService.create(credit);
            }
            return "redirect:credits";
        } else {
            if (params.get("name").isEmpty()) {
                model.addAttribute("nameValue", "");
                model.addAttribute("nameError", "Не указано название");
            } else {
                model.addAttribute("nameValue", params.get("name"));
                model.addAttribute("nameError", "");
            }

            if (params.get("limit").isEmpty()) {
                model.addAttribute("limitValue", "");
                model.addAttribute("limitError", "Не указан лимит");
            } else if (Integer.parseInt(params.get("limit")) <= 0) {
                model.addAttribute("limitValue", "");
                model.addAttribute("limitError", "Некорректное значение");
            } else {
                model.addAttribute("limitValue", params.get("limit"));
                model.addAttribute("limitError", "");
            }

            if (params.get("interestRate").isEmpty()) {
                model.addAttribute("interestRateValue", "");
                model.addAttribute("interestRateError", "Не указана процентная ставкка");
            } else if (Float.parseFloat(params.get("interestRate")) <= 0) {
                model.addAttribute("interestRateValue", "");
                model.addAttribute("interestRateError", "Некорректное значение");
            } else {
                model.addAttribute("interestRateValue", params.get("interestRate"));
                model.addAttribute("interestRateError", "");
            }

            if (params.containsKey("id")) {
                model.addAttribute("id", Optional.of(params.get("id")));
            }

            return "createNewCredit";
        }
    }
}
