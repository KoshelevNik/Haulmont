package main.services;

import main.dao.ClientDAO;
import main.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientDAO clientDAO;

    public void create(Client t) {
        clientDAO.create(t);
    }

    public Optional<Client> read(UUID uuid) {
        return clientDAO.read(uuid);
    }

    public void update(Client t) {
        clientDAO.update(t);
    }

    public void delete(Client t) {
        clientDAO.delete(t);
    }

    public List<Client> findAll() {
        return clientDAO.findAll();
    }
}
