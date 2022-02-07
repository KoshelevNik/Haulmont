package main.configurations;

import main.dao.LolDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:database/AZAZA");
    }

    @Bean
    public LolDAO lolDAO() {
        return new LolDAO();
    }
}
