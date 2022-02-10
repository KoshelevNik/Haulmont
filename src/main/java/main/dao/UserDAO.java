package main.dao;

import main.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserDAO implements DAO<User, UUID> {

    @Autowired
    private Connection connection;

    @Override
    public void create(User t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, t.getId().toString());
            preparedStatement.setString(2, t.getMail());
            preparedStatement.setString(3, t.getPassword());
            preparedStatement.setString(4, t.getRole());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> read(UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE id=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            User user = new User();
            user.setId(uuid);
            user.setMail(resultSet.getString("mail"));
            user.setPassword_hash(resultSet.getString("password_hash"));
            user.setRole(resultSet.getString("role"));
            return Optional.of(user);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(User t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user SET mail=?, password_hash=?, role=? WHERE id=?");
            preparedStatement.setString(1, t.getMail());
            preparedStatement.setString(2, t.getPassword());
            preparedStatement.setString(3, t.getRole());
            preparedStatement.setString(4, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE id=?");
            preparedStatement.setString(1, t.getId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (!resultSet.isLast()) {
                resultSet.next();
                User user = new User();
                user.setId(UUID.fromString(resultSet.getString("id")));
                user.setMail(resultSet.getString("mail"));
                user.setPassword_hash(resultSet.getString("password_hash"));
                user.setRole(resultSet.getString("role"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
