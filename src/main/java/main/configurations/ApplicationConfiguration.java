package main.configurations;

import main.dao.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:database/data");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDAO userDAO() {
        return new UserDAO();
    }

    @Bean
    public ClientDAO clientDAO() {
        return new ClientDAO();
    }

    @Bean
    public CreditDAO creditDAO() {
        return new CreditDAO();
    }

    @Bean
    public PaymentDAO paymentDAO() {
        return new PaymentDAO();
    }

    @Bean
    public BankDAO bankDAO() {
        return new BankDAO();
    }

    @Bean
    public LoanOfferDAO loanOfferDAO() {
        return new LoanOfferDAO();
    }
}
