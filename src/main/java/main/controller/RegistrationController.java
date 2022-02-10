package main.controller;

import main.entity.Client;
import main.entity.User;
import main.services.ClientService;
import main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
import java.util.regex.Pattern;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/registration")
    public String registrationGET(Model model) {
        model.addAttribute("usernameError", "");
        model.addAttribute("mailError", "");
        model.addAttribute("phoneError", "");
        model.addAttribute("passportIdError", "");
        model.addAttribute("passwordError", "");
        model.addAttribute("confirmError", "");

        model.addAttribute("usernameValue", "");
        model.addAttribute("mailValue", "");
        model.addAttribute("phoneValue", "");
        model.addAttribute("passportIdValue", "");
        model.addAttribute("passwordValue", "");
        model.addAttribute("confirmValue", "");
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPOST(
            @RequestParam String username,
            @RequestParam String mail,
            @RequestParam String phone,
            @RequestParam String passport_id,
            @RequestParam String password,
            @RequestParam String confirm,
            Model model
    ) {
        if (
                !username.isEmpty() &&
                !mail.isEmpty() &&
                !userService.mailExistInDatabase(mail) &&
                !phone.isEmpty() &&
                Pattern.matches("\\+7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}", phone) &&
                !passport_id.isEmpty() &&
                Integer.parseInt(passport_id) > 0 &&
                !password.isEmpty() &&
                !confirm.isEmpty() &&
                password.equals(confirm)
        ) {
            User user = new User();
            user.setRole("user");
            user.setMail(mail);
            user.setId(UUID.randomUUID());
            while (userService.idExistInDatabase(user.getId()))
                user.setId(UUID.randomUUID());
            user.setPassword_hash(bCryptPasswordEncoder.encode(password));
            userService.create(user);
            Client client = new Client();
            client.setClient_id(user.getId());
            client.setPhone(phone);
            client.setPassport_id(Integer.valueOf(passport_id));
            client.setName(username);
            clientService.create(client);
            return "redirect:login";
        } else {
            model.addAttribute("usernameError",
                    (username.isEmpty()) ? "Вы не указали имя" : ""
            );
            model.addAttribute("usernameValue",
                    (username.isEmpty()) ? "" : username
            );

            if (mail.isEmpty()) {
                model.addAttribute("mailError", "Вы не указали почту");
                model.addAttribute("mailValue", "");
            } else if (userService.mailExistInDatabase(mail)) {
                model.addAttribute("mailError", "Эта почта уже привязана к аккаунту");
                model.addAttribute("mailValue", "");
            } else {
                model.addAttribute("mailError", "");
                model.addAttribute("mailValue", mail);
            }

            if (phone.isEmpty()) {
                model.addAttribute("phoneError","Вы не указали телефон");
                model.addAttribute("phoneValue", "");
            } else if (!Pattern.matches("\\+7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}", phone)) {
                model.addAttribute("phoneError","Не соответствует шаблону +7-xxx-xxx-xx-xx");
                model.addAttribute("phoneValue", "");
            } else {
                model.addAttribute("phoneError","");
                model.addAttribute("phoneValue", phone);
            }

            if (passport_id.isEmpty()) {
                model.addAttribute("passportIdError", "Вы не указали номер паспорта");
                model.addAttribute("passportIdValue","");
            } else if (Integer.parseInt(passport_id) <= 0) {
                model.addAttribute("passportIdError", "Некорректный ввод");
                model.addAttribute("passportIdValue","");
            } else {
                model.addAttribute("passportIdError", "");
                model.addAttribute("passportIdValue",passport_id);
            }

            model.addAttribute("passwordError",
                    (password.isEmpty()) ? "Вы не создали пароль" : ""
            );
            model.addAttribute("passwordValue",
                    (password.isEmpty()) ? "" : password
            );

            if (confirm.isEmpty()) {
                model.addAttribute("confirmError", "Вы не подтвердили пароль");
                model.addAttribute("confirmValue", "");
            } else if (!password.equals(confirm)) {
                model.addAttribute("confirmError", "Пароли не совпадают");
                model.addAttribute("confirmValue", "");
            } else {
                model.addAttribute("confirmError", "");
                model.addAttribute("confirmValue", confirm);
            }

            return "registration";
        }
    }
}
