package main.controller;

import main.entity.LoanOffer;
import main.entity.Payment;
import main.entity.User;
import main.services.CreditService;
import main.services.LoanOfferService;
import main.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class LoanOffersController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private LoanOfferService loanOfferService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/loanOffers")
    public String loanOffersGET(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        List<LoanOffer> clientLoanOffers = loanOfferService.findAllByClientId(user.getId());
        List<MyLoanOffer> myLoanOffers = new ArrayList<>();
        for (LoanOffer loanOffer : clientLoanOffers) {
            myLoanOffers.add(new MyLoanOffer(
                    creditService.read(loanOffer.getLoanOfferId().credit_id()).get().getName(),
                    loanOffer.getClient_confirm(),
                    loanOffer.getAdmin_confirm(),
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
        System.out.println(key);
        UUID creditId = UUID.fromString(key.split("=")[1]);
        LoanOffer loanOffer = loanOfferService.read(new LoanOffer.LoanOfferId(user.getId(), creditId)).get();
        if (key.startsWith("confirm")) {
            loanOffer.setClient_confirm(true);
            loanOfferService.update(loanOffer);
            return "redirect:loanOffers";
        } else if (key.startsWith("cancel")) {
            loanOffer.setClient_confirm(false);
            loanOfferService.update(loanOffer);
            return "redirect:loanOffers";
        } else {
            Payment[] paymentsGraph = new Payment[loanOffer.getPayments_graph().length];
            for (int i = 0; i < loanOffer.getPayments_graph().length; i++)
                paymentsGraph[i] = paymentService.read(loanOffer.getPayments_graph()[i]).get();
            PaymentService.payments = paymentsGraph;
            return "redirect:paymentsGraph";
        }
    }

    private record MyLoanOffer(String name, Boolean clientConfirm, Boolean adminConfirm, UUID creditId) {}
}
