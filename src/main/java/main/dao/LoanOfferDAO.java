package main.dao;

import main.entity.Bank;
import main.entity.LoanOffer;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LoanOfferDAO implements DAO<LoanOffer, LoanOffer.LoanOfferId> {

    @Autowired
    private Connection connection;

    @Override
    public void create(LoanOffer t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO loan_offer VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, t.getLoanOfferId().client_id().toString());
            preparedStatement.setString(2, t.getLoanOfferId().credit_id().toString());
            preparedStatement.setInt(3, t.getCredit_amount());
            preparedStatement.setArray(4, connection.createArrayOf("UUID", t.getPayments_graph()));
            preparedStatement.setBoolean(5, t.getClient_confirm());
            preparedStatement.setBoolean(6, t.getAdmin_confirm());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<LoanOffer> read(LoanOffer.LoanOfferId loanOfferId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM loan_offer WHERE client_id=? AND credit_id=?");
            preparedStatement.setString(1, loanOfferId.client_id().toString());
            preparedStatement.setString(2, loanOfferId.credit_id().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            LoanOffer loanOffer = new LoanOffer();
            loanOffer.setLoanOfferId(loanOfferId);
            loanOffer.setCredit_amount(resultSet.getInt("credit_amount"));
            ResultSet rs = resultSet.getArray("payments_graph").getResultSet();
            List<UUID> payments_graph = new ArrayList<>();
            while (!rs.isLast()) {
                rs.next();
                payments_graph.add(UUID.fromString(rs.getString(2)));
            }
            loanOffer.setPayments_graph(payments_graph.toArray(new UUID[0]));
            loanOffer.setClient_confirm(resultSet.getBoolean("client_confirm"));
            loanOffer.setAdmin_confirm(resultSet.getBoolean("admin_confirm"));
            return Optional.of(loanOffer);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void update(LoanOffer t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE loan_offer SET credit_amount=?, payments_graph=?, client_confirm=?, admin_confirm WHERE client_id=? AND credit_id=?");
            preparedStatement.setInt(1, t.getCredit_amount());
            preparedStatement.setArray(2, connection.createArrayOf("UUID", t.getPayments_graph()));
            preparedStatement.setBoolean(3, t.getClient_confirm());
            preparedStatement.setBoolean(4, t.getAdmin_confirm());
            preparedStatement.setString(5, t.getLoanOfferId().client_id().toString());
            preparedStatement.setString(6, t.getLoanOfferId().credit_id().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(LoanOffer t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM loan_offer WHERE client_id=? AND credit_id=?");
            preparedStatement.setString(1, t.getLoanOfferId().client_id().toString());
            preparedStatement.setString(2, t.getLoanOfferId().credit_id().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<LoanOffer> findAll() {
        List<LoanOffer> loanOfferList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM loan_offer");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (!resultSet.isLast()) {
                resultSet.next();
                LoanOffer loanOffer = new LoanOffer();
                loanOffer.setLoanOfferId(
                        new LoanOffer.LoanOfferId(
                                UUID.fromString(resultSet.getString("client_id")),
                                UUID.fromString(resultSet.getString("credit_id"))
                        )
                );
                loanOffer.setCredit_amount(resultSet.getInt("credit_amount"));
                ResultSet rs = resultSet.getArray("payments_graph").getResultSet();
                List<UUID> payments_graph = new ArrayList<>();
                while (!rs.isLast()) {
                    rs.next();
                    payments_graph.add(UUID.fromString(rs.getString(2)));
                }
                loanOffer.setPayments_graph(payments_graph.toArray(new UUID[0]));
                loanOffer.setClient_confirm(resultSet.getBoolean("client_confirm"));
                loanOffer.setAdmin_confirm(resultSet.getBoolean("admin_confirm"));
                loanOfferList.add(loanOffer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loanOfferList;
    }
}
