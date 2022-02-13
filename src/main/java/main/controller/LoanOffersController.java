package main.controller;

import main.entity.Bank;
import main.entity.LoanOffer;
import main.entity.Payment;
import main.entity.User;
import main.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
public class LoanOffersController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private LoanOfferService loanOfferService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @GetMapping("/loanOffers")
    public String loanOffersGET(Model model, Principal principal, @RequestParam Optional<String> clientIdOptional) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("role", user.getRole());
        if (user.getRole().equals("admin") && clientIdOptional.isPresent() && !clientIdOptional.get().isEmpty()) {
            user = userService.read(UUID.fromString(clientIdOptional.get())).get();
            model.addAttribute("clientId", clientIdOptional.get());
        } else if (user.getRole().equals("admin") && (clientIdOptional.isEmpty() || clientIdOptional.get().isEmpty())) {
            return "redirect:";
        }
        List<LoanOffer> clientLoanOffers = loanOfferService.findAllByClientId(user.getId());
        List<MyLoanOffer> myLoanOffers = new ArrayList<>();
        for (LoanOffer loanOffer : clientLoanOffers) {
            String clientConfirm = (loanOffer.getClient_confirm() == null) ? "null" : String.valueOf(loanOffer.getClient_confirm());
            String adminConfirm = (loanOffer.getAdmin_confirm() == null) ? "null" : String.valueOf(loanOffer.getAdmin_confirm());
            myLoanOffers.add(new MyLoanOffer(
                    creditService.read(loanOffer.getLoanOfferId().credit_id()).get().getName(),
                    clientConfirm,
                    adminConfirm,
                    loanOffer.getLoanOfferId().credit_id()
            ));
        }
        model.addAttribute("myLoanOffers", myLoanOffers);
        return "loanOffers";
    }

    @PostMapping("/loanOffers")
    public String loanOffersPOST(@RequestParam Map<String, String> allParam, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        String key = allParam.keySet().toArray()[0].toString();
        UUID creditId = UUID.fromString(key.split("=")[1]);
        LoanOffer loanOffer = loanOfferService.read(new LoanOffer.LoanOfferId(
                (user.getRole().equals("user")) ? user.getId() : UUID.fromString(key.split("=")[2]),
                creditId
        )).get();
        if (key.startsWith("confirm")) {
            if (user.getRole().equals("user")) {
                loanOffer.setClient_confirm(true);
            } else {
                loanOffer.setAdmin_confirm(true);
            }
            loanOfferService.update(loanOffer);
            return "redirect:loanOffers" + (user.getRole().equals("admin") ? "?clientIdOptional="+key.split("=")[2] : "");
        } else if (key.startsWith("cancel")) {
            if (user.getRole().equals("user")) {
                loanOffer.setClient_confirm(false);
            } else {
                loanOffer.setAdmin_confirm(false);
            }
            loanOfferService.update(loanOffer);
            return "redirect:loanOffers" + (user.getRole().equals("admin") ? "?clientIdOptional="+key.split("=")[2] : "");
        } else {
            Payment[] paymentsGraph = new Payment[loanOffer.getPayments_graph().length];
            for (int i = 0; i < loanOffer.getPayments_graph().length; i++)
                paymentsGraph[i] = paymentService.read(loanOffer.getPayments_graph()[i]).get();
            PaymentService.payments = paymentsGraph;
            return "redirect:paymentsGraph";
        }
    }

    private record MyLoanOffer(String name, String clientConfirm, String adminConfirm, UUID creditId) {
    }
}
