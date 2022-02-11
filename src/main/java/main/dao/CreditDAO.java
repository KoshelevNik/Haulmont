package main.dao;

import main.entity.Credit;
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
public class CreditDAO implements DAO<Credit, UUID> {

    @Autowired
    private Connection connection;

    @Override
    public void create(Credit t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO credit VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, t.getId().toString());
            preparedStatement.setInt(2, t.getLimit());
            preparedStatement.setFloat(3, t.getInterest_rate());
            preparedStatement.setString(4, t.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Credit> read(UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM credit WHERE id=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Credit credit = new Credit();
            credit.setId(uuid);
            credit.setInterest_rate(resultSet.getFloat("interest_rate"));
            credit.setLimit(resultSet.getInt("limit"));
            credit.setName(resultSet.getString("name"));
            return Optional.of(credit);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Credit t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE credit SET limit=?, interest_rate=?, name=? WHERE id=?");
            preparedStatement.setInt(1, t.getLimit());
            preparedStatement.setFloat(2, t.getInterest_rate());
            preparedStatement.setString(3, t.getName());
            preparedStatement.setString(4, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Credit t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM credit WHERE id=?");
            preparedStatement.setString(1, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Credit> findAll() {
        List<Credit> creditList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM credit");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (!resultSet.isLast()) {
                resultSet.next();
                Credit credit = new Credit();
                credit.setId(UUID.fromString(resultSet.getString("id")));
                credit.setLimit(resultSet.getInt("limit"));
                credit.setInterest_rate(resultSet.getFloat("interest_rate"));
                credit.setName(resultSet.getString("name"));
                creditList.add(credit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return creditList;
    }
}
