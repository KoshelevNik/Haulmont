package main.controller;

import main.entity.*;
import main.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private BankService bankService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private LoanOfferService loanOfferService;

    @Autowired
    private ClientService clientService;

    @GetMapping("/")
    public String indexGET(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("user", user);

        if (user.getRole().equals("user")) {
            List<Credit> creditList = creditService.findAll();
            List<LoanOffer> loanOfferList = loanOfferService.findAll();
            for (LoanOffer loanOffer : loanOfferList) {
                if (loanOffer.getLoanOfferId().client_id().equals(user.getId())) {
                    creditList.remove(creditService.read(loanOffer.getLoanOfferId().credit_id()).get());
                }
            }
            model.addAttribute("creditList", creditList);
        } else {
            autoDelete();
            List<LoanOffer> allLoanOffers = loanOfferService.findAll();
            List<NotConfirmedLoanOffer> notConfirmedLoanOffers = new ArrayList<>();
            for (LoanOffer loanOffer : allLoanOffers) {
                if (loanOffer.getAdmin_confirm() == null) {
                    DateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
                    notConfirmedLoanOffers.add(new NotConfirmedLoanOffer(
                            creditService.read(loanOffer.getLoanOfferId().credit_id()).get().getName(),
                            loanOffer.getLoanOfferId().client_id(),
                            loanOffer.getCredit_amount(),
                            dt.format(paymentService.read(loanOffer.getPayments_graph()[0]).get().getPayment_date().getTime()),
                            loanOffer.getInterest_rate(),
                            loanOffer.getLoanOfferId().credit_id(),
                            clientService.read(loanOffer.getLoanOfferId().client_id()).get().getName()
                    ));
                }
            }
            model.addAttribute("notConfirmedLoanOffers", notConfirmedLoanOffers);
        }

        return "index";
    }

    @PostMapping("/")
    public String indexPOST(@RequestParam HashMap<String, String> mapParam, Principal principal, Model model) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (user.getRole().equals("user")) {
            if (!mapParam.get("creditName").isEmpty() && mapParam.containsKey("search")) {
                model.addAttribute("user", user);

                List<Credit> creditList = creditService.findAll();
                List<LoanOffer> loanOfferList = loanOfferService.findAll();
                for (LoanOffer loanOffer : loanOfferList) {
                    if (loanOffer.getLoanOfferId().client_id().equals(user.getId())) {
                        creditList.remove(creditService.read(loanOffer.getLoanOfferId().credit_id()).get());
                    }
                }

                creditList.sort(new Comparator<>() {
                    @Override
                    public int compare(Credit o1, Credit o2) {
                        return countOfCoincidences(o2.getName(), mapParam.get("creditName")) -
                                countOfCoincidences(o1.getName(), mapParam.get("creditName"));
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
                return "index";
            } else {
                mapParam.remove("creditName");
                String key = mapParam.entrySet().toArray()[0].toString();
                if (key.startsWith("id=")) {
                    return "redirect:loanProcessing?id=" + key.split("=")[1];
                } else {
                    return "index";
                }
            }
        } else {
            String key = mapParam.keySet().toArray()[0].toString();
            if (key.startsWith("client")) {
                return "redirect:profile?clientId=" + key.split("=")[1];
            } else if (key.startsWith("confirm")) {
                LoanOffer loanOffer = loanOfferService.read(new LoanOffer.LoanOfferId(
                        UUID.fromString(key.split("=")[2]),
                        UUID.fromString(key.split("=")[1])
                )).get();
                loanOffer.setAdmin_confirm(true);
                loanOfferService.update(loanOffer);
                if (loanOffer.getAdmin_confirm() && loanOffer.getClient_confirm()) {
                    Bank bank = new Bank();
                    bank.setBankId(new Bank.BankId(
                            loanOffer.getLoanOfferId().client_id(),
                            loanOffer.getLoanOfferId().credit_id()
                    ));
                    bankService.create(bank);
                }
                return "redirect:";
            } else if (key.startsWith("cancel")) {
                LoanOffer loanOffer = loanOfferService.read(new LoanOffer.LoanOfferId(
                        UUID.fromString(key.split("=")[2]),
                        UUID.fromString(key.split("=")[1])
                )).get();
                loanOffer.setAdmin_confirm(false);
                loanOfferService.update(loanOffer);
                return "redirect:";
            } else {
                UUID clientId = UUID.fromString(key.split("=")[0]);
                UUID creditId = UUID.fromString(key.split("=")[1]);
                LoanOffer loanOffer = loanOfferService.read(new LoanOffer.LoanOfferId(clientId, creditId)).get();
                Payment[] paymentsGraph = new Payment[loanOffer.getPayments_graph().length];
                UUID[] uuids = loanOffer.getPayments_graph();
                for (int i = 0; i < uuids.length; i++) {
                    paymentsGraph[i] = paymentService.read(uuids[i]).get();
                }
                PaymentService.payments = paymentsGraph;
                return "redirect:paymentsGraph";
            }
        }
    }

    @GetMapping("/loanProcessing")
    public String loanProcessingGET(Model model, Principal principal, @RequestParam Optional<String> id) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (id.isPresent() && !id.get().isEmpty() && user.getRole().equals("user") &&
                loanOfferService.read(new LoanOffer.LoanOfferId(user.getId(), UUID.fromString(id.get()))).isEmpty()
        ) {
            model.addAttribute("showCreditNameSelect", false);
            model.addAttribute("creditId", id.get());
        } else if (user.getRole().equals("admin") && id.isPresent() && !id.get().isEmpty()) {
            model.addAttribute("showCreditNameSelect", true);
            UUID clientId = UUID.fromString(id.get());
            List<LoanOffer> allByClientId = loanOfferService.findAllByClientId(clientId);
            List<Credit> allCredits = creditService.findAll();
            for (LoanOffer loanOffer : allByClientId) {
                allCredits.remove(creditService.read(loanOffer.getLoanOfferId().credit_id()).get());
            }
            model.addAttribute("clientId", clientId);
            model.addAttribute("allCredits", allCredits);
        } else
            return "redirect:";

        model.addAttribute("creditAmountError", "");
        model.addAttribute("creditDateError", "");
        model.addAttribute("timeError", "");
        model.addAttribute("interestRateError", "");

        model.addAttribute("creditAmountValue", "");
        model.addAttribute("creditDateValue", "");
        model.addAttribute("timeValue", "");
        model.addAttribute("interestRateValue", "");

        return "loanProcessing";
    }

    @PostMapping("/loanProcessing")
    public String loanProcessingPOST(@RequestParam HashMap<String, String> mapParam, Principal principal, Model model) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        UUID creditId = UUID.fromString(mapParam.get("creditId"));
        if (
                !(!mapParam.containsValue("") &&
                        !mapParam.get("creditAmount").isEmpty() &&
                        Integer.parseInt(mapParam.get("creditAmount")) > 0 &&
                        !mapParam.get("creditDate").isEmpty() &&
                        !before(mapParam.get("creditDate")) &&
                        !mapParam.get("time").isEmpty() &&
                        Integer.parseInt(mapParam.get("time")) > 0 &&
                        !mapParam.get("interestRate").isEmpty() &&
                        Float.parseFloat(mapParam.get("interestRate")) > 0 &&
                        Integer.parseInt(mapParam.get("creditAmount")) <= creditService.read(creditId).get().getLimit() &&
                        Float.parseFloat(mapParam.get("interestRate")) <= creditService.read(creditId).get().getInterest_rate())
        ) {

            if (mapParam.get("creditAmount").isEmpty()) {
                model.addAttribute("creditAmountError", "Не указана сумма кредита");
                model.addAttribute("creditAmountValue", "");
            } else if (Integer.parseInt(mapParam.get("creditAmount")) <= 0) {
                model.addAttribute("creditAmountError", "Некорректно указано значение");
                model.addAttribute("creditAmountValue", "");
            } else if (Integer.parseInt(mapParam.get("creditAmount")) > creditService.read(creditId).get().getLimit()) {
                model.addAttribute("creditAmountError", "Значение больше лимита");
                model.addAttribute("creditAmountValue", "");
            } else {
                model.addAttribute("creditAmountError", "");
                model.addAttribute("creditAmountValue", mapParam.get("creditAmount"));
            }

            if (mapParam.get("creditDate").isEmpty()) {
                model.addAttribute("creditDateError", "Не указана дата");
                model.addAttribute("creditDateValue", "");
            } else {
                String[] dateString = mapParam.get("creditDate").split("-");
                Calendar creditDate = new GregorianCalendar(
                        Integer.parseInt(dateString[0]),
                        Integer.parseInt(dateString[1]) - 1,
                        Integer.parseInt(dateString[2])
                );
                DateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                if (creditDate.before(Calendar.getInstance())) {
                    model.addAttribute("creditDateError", "Некорректно указано значение");
                    model.addAttribute("creditDateValue", "");
                } else {
                    model.addAttribute("creditDateError", "");
                    model.addAttribute("creditDateValue", dt.format(creditDate.getTime()));
                }
            }

            if (mapParam.get("time").isEmpty()) {
                model.addAttribute("timeError", "Не указан срок");
                model.addAttribute("timeValue", "");
            } else if (Integer.parseInt(mapParam.get("time")) <= 0) {
                model.addAttribute("timeError", "Некорректно указано значение");
                model.addAttribute("timeValue", "");
            } else {
                model.addAttribute("timeError", "");
                model.addAttribute("timeValue", mapParam.get("time"));
            }

            if (mapParam.get("interestRate").isEmpty()) {
                model.addAttribute("interestRateError", "Не указана процентная ставка");
                model.addAttribute("interestRateValue", "");
            } else if (Float.parseFloat(mapParam.get("interestRate")) <= 0) {
                model.addAttribute("interestRateError", "Некорректно указано значение");
                model.addAttribute("interestRateValue", "");
            } else if (Float.parseFloat(mapParam.get("interestRate")) > creditService.read(creditId).get().getInterest_rate()) {
                model.addAttribute("interestRateError", "Значение больше лимита");
                model.addAttribute("interestRateValue", "");
            } else {
                model.addAttribute("interestRateError", "");
                model.addAttribute("interestRateValue", mapParam.get("interestRate"));
            }

            model.addAttribute("showCreditNameSelect", false);
            model.addAttribute("creditId", mapParam.get("creditId"));
            return "loanProcessing";
        } else {
            int creditAmount = Integer.parseInt(mapParam.get("creditAmount"));
            String[] dateString = mapParam.get("creditDate").split("-");
            Calendar creditDate = new GregorianCalendar(
                    Integer.parseInt(dateString[0]),
                    Integer.parseInt(dateString[1]) - 1,
                    Integer.parseInt(dateString[2])
            );
            int timeInMonth = Integer.parseInt(mapParam.get("time")) * 12;
            float interestRate = Float.parseFloat(mapParam.get("interestRate"));
            String creditType = mapParam.get("creditType");

            PaymentsGraph paymentsGraph;

            if (mapParam.containsKey("registration")) {

                LoanOffer loanOffer = new LoanOffer();
                loanOffer.setLoanOfferId(new LoanOffer.LoanOfferId((user.getRole().equals("user")) ? user.getId() : UUID.fromString(mapParam.get("clientId")), creditId));
                loanOffer.setAdmin_confirm((user.getRole().equals("user")) ? null : true);
                loanOffer.setClient_confirm((user.getRole().equals("user")) ? true : null);
                loanOffer.setCredit_amount(creditAmount);
                loanOffer.setInterest_rate(interestRate);

                paymentsGraph =
                        (creditType.equals("Аннуитетный"))
                                ? annuityLoan(creditAmount, creditDate, timeInMonth, interestRate)
                                : differentiatedLoan(creditAmount, creditDate, timeInMonth, interestRate);

                paymentService.createAllFromArray(paymentsGraph.payments());
                loanOffer.setPayments_graph(paymentsGraph.paymentsUUID());
                loanOfferService.create(loanOffer);

                return "redirect:";
            } else {

                if (creditType.equals("Аннуитетный")) {
                    paymentsGraph = annuityLoan(
                            creditAmount,
                            creditDate,
                            timeInMonth,
                            interestRate
                    );
                } else {
                    paymentsGraph = differentiatedLoan(
                            creditAmount,
                            creditDate,
                            timeInMonth,
                            interestRate
                    );
                }
                PaymentService.payments = paymentsGraph.payments();
                return "redirect:paymentsGraph";
            }
        }
    }

    @GetMapping("/paymentsGraph")
    public String paymentsGraphGET(Model model) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        int amount = 0;
        int creditAmount = 0;
        for (int i = 0; i < PaymentService.payments.length - 1; i++) {
            amount += PaymentService.payments[i].getPayment_amount();
            creditAmount += PaymentService.payments[i].getCredit_body();
        }
        amount += PaymentService.payments[PaymentService.payments.length - 1].getPayment_amount();
        creditAmount += PaymentService.payments[PaymentService.payments.length - 1].getPayment_amount();
        model.addAttribute("amount", amount);
        model.addAttribute("percent", amount - creditAmount);
        model.addAttribute("payments", PaymentService.payments);
        model.addAttribute("dateFormat", dateFormat);
        return "paymentsGraph";
    }

    @GetMapping("/clients")
    public String clientsGET(Model model) {
        model.addAttribute("clientList", clientService.findAll());
        return "clients";
    }

    @PostMapping("/clients")
    public String clientsPOST(Model model, @RequestParam String name) {
        List<Client> clientList = clientService.findAll();
        clientList.sort(new Comparator<>() {
            @Override
            public int compare(Client o1, Client o2) {
                return countOfCoincidences(o2.getName(), name) -
                        countOfCoincidences(o1.getName(), name);
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
        model.addAttribute("clientList", clientList);
        return "clients";
    }

    private PaymentsGraph annuityLoan(
            int creditAmount,
            Calendar creditDate,
            int timeInMonth,
            float interestRate
    ) {
        Payment[] payments = new Payment[timeInMonth + 1];
        UUID[] paymentsUUID = new UUID[timeInMonth + 1];

        float interestRateMonth = interestRate / 100.0f / 12.0f;
        int paymentAmount = (int)
                (creditAmount * (interestRateMonth / (1 - Math.pow(1 + interestRateMonth, -timeInMonth))));

        Payment paymentFirst = new Payment();
        paymentFirst.setPayment_date(creditDate);
        paymentFirst.setPayment_amount(0);
        paymentFirst.setCredit_body(0);
        paymentFirst.setPercent(0);
        paymentFirst.setRemainder(creditAmount);
        paymentFirst.setId(UUID.randomUUID());
        while (paymentService.idExistInDatabase(paymentFirst.getId()))
            paymentFirst.setId(UUID.randomUUID());

        payments[0] = paymentFirst;
        paymentsUUID[0] = paymentFirst.getId();

        for (int i = 1; i < timeInMonth; i++) {
            Calendar creditDateNewInstance = (Calendar) creditDate.clone();
            creditDateNewInstance.add(Calendar.MONTH, i);
            int percent = (int) (creditAmount * interestRateMonth);
            int creditBody = paymentAmount - percent;

            Payment p = new Payment();
            p.setPayment_date(creditDateNewInstance);
            p.setPayment_amount(paymentAmount);
            p.setCredit_body(creditBody);
            p.setPercent(percent);
            p.setRemainder(creditAmount - creditBody);
            p.setId(UUID.randomUUID());
            while (paymentService.idExistInDatabase(p.getId()) && Arrays.asList(paymentsUUID).contains(p.getId()))
                p.setId(UUID.randomUUID());

            creditAmount -= creditBody;
            payments[i] = p;
            paymentsUUID[i] = p.getId();
        }

        int percent = (int) (creditAmount * interestRateMonth);
        int creditBody = creditAmount - percent;
        Calendar creditDateNewInstance = (Calendar) creditDate.clone();
        creditDateNewInstance.add(Calendar.MONTH, timeInMonth);

        Payment paymentLast = new Payment();
        paymentLast.setPayment_date(creditDateNewInstance);
        paymentLast.setPayment_amount(creditAmount);
        paymentLast.setCredit_body(creditBody);
        paymentLast.setPercent(percent);
        paymentLast.setRemainder(0);
        paymentLast.setId(UUID.randomUUID());
        while (paymentService.idExistInDatabase(paymentLast.getId()) && Arrays.asList(paymentsUUID).contains(paymentLast.getId()))
            paymentLast.setId(UUID.randomUUID());

        payments[timeInMonth] = paymentLast;
        paymentsUUID[timeInMonth] = paymentLast.getId();

        return new PaymentsGraph(payments, paymentsUUID);
    }

    private PaymentsGraph differentiatedLoan(
            int creditAmount,
            Calendar creditDate,
            int timeInMonth,
            float interestRate
    ) {
        Payment[] payments = new Payment[timeInMonth + 1];
        UUID[] paymentsUUID = new UUID[timeInMonth + 1];

        int creditBody = creditAmount / timeInMonth;

        Payment paymentFirst = new Payment();
        paymentFirst.setPayment_date(creditDate);
        paymentFirst.setPayment_amount(0);
        paymentFirst.setCredit_body(0);
        paymentFirst.setPercent(0);
        paymentFirst.setRemainder(creditAmount);
        paymentFirst.setId(UUID.randomUUID());
        while (paymentService.idExistInDatabase(paymentFirst.getId()))
            paymentFirst.setId(UUID.randomUUID());

        payments[0] = paymentFirst;
        paymentsUUID[0] = paymentFirst.getId();

        for (int i = 1; i < timeInMonth; i++) {
            Calendar creditDateNewInstance = (Calendar) creditDate.clone();
            creditDateNewInstance.add(Calendar.MONTH, i);
            int percent = (int) (creditAmount * interestRate / 100 / 12);
            int paymentAmount = creditBody + percent;

            Payment p = new Payment();
            p.setPayment_date(creditDateNewInstance);
            p.setPayment_amount(paymentAmount);
            p.setCredit_body(creditBody);
            p.setPercent(percent);
            p.setRemainder(creditAmount - creditBody);
            p.setId(UUID.randomUUID());
            while (paymentService.idExistInDatabase(p.getId()) && Arrays.asList(paymentsUUID).contains(p.getId()))
                p.setId(UUID.randomUUID());

            creditAmount -= creditBody;
            payments[i] = p;
            paymentsUUID[i] = p.getId();
        }

        int percent = (int) (creditAmount * interestRate / 100 / 12);
        creditBody = creditAmount - percent;
        Calendar creditDateNewInstance = (Calendar) creditDate.clone();
        creditDateNewInstance.add(Calendar.MONTH, timeInMonth);

        Payment paymentLast = new Payment();
        paymentLast.setPayment_date(creditDateNewInstance);
        paymentLast.setPayment_amount(creditAmount);
        paymentLast.setCredit_body(creditBody);
        paymentLast.setPercent(percent);
        paymentLast.setRemainder(0);
        paymentLast.setId(UUID.randomUUID());
        while (paymentService.idExistInDatabase(paymentLast.getId()) && Arrays.asList(paymentsUUID).contains(paymentLast.getId()))
            paymentLast.setId(UUID.randomUUID());

        payments[timeInMonth] = paymentLast;
        paymentsUUID[timeInMonth] = paymentLast.getId();

        return new PaymentsGraph(payments, paymentsUUID);
    }

    private record PaymentsGraph(Payment[] payments, UUID[] paymentsUUID) {
    }

    private record NotConfirmedLoanOffer(String name, UUID clientId, Integer creditAmount, String issueDate,
                                         Float interestRate, UUID creditId, String clientName) {
    }

    private boolean before(String date) {
        String[] dateString = date.split("-");
        Calendar creditDate = new GregorianCalendar(
                Integer.parseInt(dateString[0]),
                Integer.parseInt(dateString[1]) - 1,
                Integer.parseInt(dateString[2])
        );
        return creditDate.before(Calendar.getInstance());
    }

    private void autoDelete() {
        List<LoanOffer> loanOfferList = loanOfferService.findAll();
        for (LoanOffer loanOffer : loanOfferList) {
            if (
                    paymentService.read(loanOffer.getPayments_graph()[0]).get().getPayment_date().before(Calendar.getInstance()) &&
                            (
                                    loanOffer.getAdmin_confirm() == null ||
                                            !loanOffer.getAdmin_confirm() ||
                                            loanOffer.getClient_confirm() == null ||
                                            !loanOffer.getClient_confirm()
                            )
            ) {
                loanOfferService.delete(loanOffer);
                for (UUID uuid : loanOffer.getPayments_graph()) {
                    paymentService.delete(paymentService.read(uuid).get());
                }
            } else if (paymentService.read(loanOffer.getPayments_graph()[loanOffer.getPayments_graph().length - 1]).get().getPayment_date().before(Calendar.getInstance())) {
                loanOfferService.delete(loanOffer);
                for (UUID uuid : loanOffer.getPayments_graph()) {
                    paymentService.delete(paymentService.read(uuid).get());
                }
                bankService.delete(bankService.read(new Bank.BankId(loanOffer.getLoanOfferId().client_id(), loanOffer.getLoanOfferId().credit_id())).get());
            }
        }
    }
}
