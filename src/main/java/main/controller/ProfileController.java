package main.controller;

import main.entity.*;
import main.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Controller
public class ProfileController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private LoanOfferService loanOfferService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/profile")
    public String profileGET(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Client client = clientService.read(user.getId()).get();
        model.addAttribute("user", user);
        model.addAttribute("client", client);
        model.addAttribute("isAdmin", user.getRole().equals("admin"));
        return "profile";
    }

    @GetMapping("/myCredits")
    public String myCreditsGET(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        List<Bank> bankList = bankService.findAll();
        List<MyCredit> credits = new ArrayList<>();
        for (Bank bank : bankList) {
            UUID clientId = bank.getBankId().client_id();
            if (clientId.equals(user.getId())) {
                UUID creditId = bank.getBankId().credit_id();
                List<LoanOffer> allById = loanOfferService.findAllById(new LoanOffer.LoanOfferId(clientId, creditId));
                LoanOffer loanOffer = new LoanOffer();
                boolean confirmed = false;
                for (LoanOffer lo : allById) {
                    if (lo.getAdmin_confirm() != null && lo.getClient_confirm() != null && lo.getAdmin_confirm() && lo.getClient_confirm()) {
                        loanOffer = lo;
                        confirmed = true;
                        break;
                    }
                }
                if (confirmed) {
                    Calendar date = paymentService.read(loanOffer.getPayments_graph()[0]).get().getPayment_date();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    MyCredit myCredit = new MyCredit(
                            creditService.read(creditId).get().getName(),
                            loanOffer.getCredit_amount(),
                            dateFormat.format(date.getTime()),
                            loanOffer.getInterest_rate(),
                            creditId
                    );
                    credits.add(myCredit);
                }
            }
        }
        model.addAttribute("credits", credits);
        return "myCredits";
    }

    @PostMapping("/myCredits")
    public String myCreditsPOST(@RequestParam Map<String, String> allParam, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String creditId = allParam.keySet().toArray()[0].toString().split("=")[1];
        LoanOffer loanOffer = loanOfferService.read(new LoanOffer.LoanOfferId(
                user.getId(),
                UUID.fromString(creditId)
        )).get();
        Payment[] paymentsGraph = new Payment[loanOffer.getPayments_graph().length];
        for (int i = 0; i < loanOffer.getPayments_graph().length; i++)
            paymentsGraph[i] = paymentService.read(loanOffer.getPayments_graph()[i]).get();
        PaymentService.payments = paymentsGraph;
        return "redirect:paymentsGraph";
    }

    @GetMapping("/changePassword")
    public String changePasswordGET(Model model) {
        model.addAttribute("oldValue", "");
        model.addAttribute("newValue", "");
        model.addAttribute("confirmValue", "");

        model.addAttribute("oldError", "");
        model.addAttribute("newError", "");
        model.addAttribute("confirmError", "");
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePasswordPOST(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirm,
            Principal principal,
            Model model
    ) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (
                !oldPassword.isEmpty() &&
                        !newPassword.isEmpty() &&
                        !confirm.isEmpty() &&
                        bCryptPasswordEncoder.matches(oldPassword, user.getPassword()) &&
                        newPassword.equals(confirm)
        ) {
            user.setPassword_hash(bCryptPasswordEncoder.encode(newPassword));
            userService.update(user);
            return "redirect:profile";
        } else {
            if (oldPassword.isEmpty()) {
                model.addAttribute("oldError", "Не указан старый пароль");
                model.addAttribute("oldValue", "");
            } else if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
                model.addAttribute("oldError", "Неверно указан старый пароль");
                model.addAttribute("oldValue", "");
            } else {
                model.addAttribute("oldError", "");
                model.addAttribute("oldValue", oldPassword);
            }

            if (newPassword.isEmpty()) {
                model.addAttribute("newError", "Не указан новый пароль");
                model.addAttribute("newValue", "");
            } else {
                model.addAttribute("newError", "");
                model.addAttribute("newValue", newPassword);
            }

            if (confirm.isEmpty()) {
                model.addAttribute("confirmError", "Не подтвержден новый пароль");
                model.addAttribute("confirmValue", "");
            } else if (!newPassword.isEmpty() && !newPassword.equals(confirm)) {
                model.addAttribute("confirmError", "Пароли не совпадают");
                model.addAttribute("confirmValue", "");
            } else {
                model.addAttribute("confirmError", "");
                model.addAttribute("confirmValue", confirm);
            }
        }
        return "changePassword";
    }

    @GetMapping("/changeClientData")
    public String changeClientDataGET(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Client client = clientService.read(user.getId()).get();

        model.addAttribute("nameError", "");
        model.addAttribute("phoneError", "");
        model.addAttribute("passportIdError", "");

        model.addAttribute("nameValue", client.getName());
        model.addAttribute("phoneValue", client.getPhone());
        model.addAttribute("passportIdValue", client.getPassport_id());

        return "changeClientData";
    }

    @PostMapping("/changeClientData")
    public String changeClientDataPOST(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String passportId,
            Model model,
            Principal principal
    ) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (
                !name.isEmpty() &&
                        !phone.isEmpty() &&
                        Pattern.matches("\\+7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}", phone) &&
                        !passportId.isEmpty() &&
                        Integer.parseInt(passportId) > 0
        ) {
            Client client = clientService.read(user.getId()).get();
            client.setPhone(phone);
            client.setPassport_id(Integer.valueOf(passportId));
            client.setName(name);
            clientService.update(client);
            return "redirect:profile";
        } else {
            model.addAttribute("nameError", (name.isEmpty()) ? "Вы не указали имя" : "");
            model.addAttribute("nameValue", (name.isEmpty()) ? "" : name);

            if (phone.isEmpty()) {
                model.addAttribute("phoneError", "Вы не указали телефон");
                model.addAttribute("phoneValue", "");
            } else if (!Pattern.matches("\\+7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}", phone)) {
                model.addAttribute("phoneError", "Не соответствует шаблону +7-xxx-xxx-xx-xx");
                model.addAttribute("phoneValue", "");
            } else {
                model.addAttribute("phoneError", "");
                model.addAttribute("phoneValue", phone);
            }

            if (passportId.isEmpty()) {
                model.addAttribute("passportIdError", "Вы не указали номер паспорта");
                model.addAttribute("passportIdValue", "");
            } else if (Integer.parseInt(passportId) <= 0) {
                model.addAttribute("passportIdError", "Некорректный ввод");
                model.addAttribute("passportIdValue", "");
            } else {
                model.addAttribute("passportIdError", "");
                model.addAttribute("passportIdValue", passportId);
            }

            return "changeClientData";
        }
    }

    private record MyCredit(String name, Integer creditAmount, String dateOfIssue, Float interestRate, UUID creditId) {
    }
}
