package main.dao;

import main.model.Lol;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LolDAO implements DAO<Lol, UUID> {

    @Autowired
    private Connection connection;

    @Override
    public void create(Lol t) {

    }

    @Override
    public Lol read(UUID uuid) {
        Lol lol = new Lol();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM lol WHERE id='" + uuid.toString()+ "'");
            resultSet.next();
            lol.setId(UUID.fromString(resultSet.getString("id")));
            lol.setName(resultSet.getString("name"));
            resultSet.close();
            statement.close();
        } catch (SQLException ignored) {}
        return lol;
    }

    @Override
    public void update(Lol t) {

    }

    @Override
    public void delete(Lol t) {

    }

    @Override
    public List<Lol> findAll() {
        return null;
    }
}
