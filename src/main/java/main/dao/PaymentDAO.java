package main.dao;

import main.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PaymentDAO implements DAO<Payment, UUID> {

    @Autowired
    private Connection connection;

    @Override
    public void create(Payment t) {
        try {
            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO payment VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, dt.format(t.getPayment_date().getTime()));
            preparedStatement.setInt(2, t.getPayment_amount());
            preparedStatement.setInt(3, t.getCredit_body());
            preparedStatement.setInt(4, t.getPercent());
            preparedStatement.setString(5, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Payment> read(UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM payment WHERE id=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String[] date = resultSet.getString("payment_date").split("-");
            Payment payment = new Payment();
            payment.setId(uuid);
            payment.setPayment_date(new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2])));
            payment.setPayment_amount(resultSet.getInt("payment_amount"));
            payment.setCredit_body(resultSet.getInt("credit_body"));
            payment.setPercent(resultSet.getInt("percent"));
            return Optional.of(payment);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void update(Payment t) {
        try {
            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE payment SET payment_date=?, payment_amount=?, credit_body=?, percent=? WHERE id=?");
            preparedStatement.setString(1, dt.format(t.getPayment_date().getTime()));
            preparedStatement.setInt(2, t.getPayment_amount());
            preparedStatement.setInt(3, t.getCredit_body());
            preparedStatement.setInt(4, t.getPercent());
            preparedStatement.setString(5, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Payment t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM payment WHERE id=?");
            preparedStatement.setString(1, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> paymentList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM payment");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (!resultSet.isLast()) {
                resultSet.next();
                String[] date = resultSet.getString("payment_date").split("-");
                Payment payment = new Payment();
                payment.setId(UUID.fromString(resultSet.getString("id")));
                payment.setPayment_date(new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2])));
                payment.setPayment_amount(resultSet.getInt("payment_amount"));
                payment.setCredit_body(resultSet.getInt("credit_body"));
                payment.setPercent(resultSet.getInt("percent"));
                paymentList.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentList;
    }
}
