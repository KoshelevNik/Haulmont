package main.dao;

import main.entity.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BankDAO implements DAO<Bank, Bank.BankId> {

    @Autowired
    private Connection connection;

    @Override
    public void create(Bank t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bank VALUES (?, ?)");
            preparedStatement.setString(1, t.getBankId().client_id().toString());
            preparedStatement.setString(2, t.getBankId().credit_id().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Bank> read(Bank.BankId bankId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank WHERE client_id=? AND credit_id=?");
            preparedStatement.setString(1, bankId.client_id().toString());
            preparedStatement.setString(2, bankId.credit_id().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Bank bank = new Bank();
            bank.setBankId(bankId);
            return Optional.of(bank);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Bank t) {

    }

    @Override
    public void delete(Bank t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM bank WHERE client_id=? AND credit_id=?");
            preparedStatement.setString(1, t.getBankId().client_id().toString());
            preparedStatement.setString(2, t.getBankId().credit_id().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Bank> findAll() {
        List<Bank> bankList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (!resultSet.isLast()) {
                resultSet.next();
                Bank bank = new Bank();
                bank.setBankId(new Bank.BankId(
                        UUID.fromString(resultSet.getString("client_id")),
                        UUID.fromString(resultSet.getString("credit_id"))
                ));
                bankList.add(bank);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankList;
    }
}
