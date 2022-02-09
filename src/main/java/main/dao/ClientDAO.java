package main.dao;

import main.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientDAO implements DAO<Client, UUID> {

    @Autowired
    private Connection connection;

    @Override
    public void create(Client t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO client VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, t.getClient_id().toString());
            preparedStatement.setString(2, t.getName());
            preparedStatement.setString(3, t.getPhone());
            preparedStatement.setInt(4, t.getPassport_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Client> read(UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM client WHERE client_id=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Client client = new Client();
            client.setClient_id(uuid);
            client.setName(resultSet.getString("name"));
            client.setPhone(resultSet.getString("phone"));
            client.setPassport_id(resultSet.getInt("passport_id"));
            return Optional.of(client);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void update(Client t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE client SET name=?, phone=?, passport_id=? WHERE client_id=?");
            preparedStatement.setString(1, t.getName());
            preparedStatement.setString(2, t.getPhone());
            preparedStatement.setInt(3, t.getPassport_id());
            preparedStatement.setString(4, t.getClient_id().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Client t) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM client WHERE client_id=?");
            preparedStatement.setString(1, t.getClient_id().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Client> findAll() {
        List<Client> clientList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM client");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (!resultSet.isLast()) {
                resultSet.next();
                Client client = new Client();
                client.setClient_id(UUID.fromString(resultSet.getString("client_id")));
                client.setName(resultSet.getString("name"));
                client.setPhone(resultSet.getString("phone"));
                client.setPassport_id(resultSet.getInt("passport_id"));
                clientList.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientList;
    }
}
