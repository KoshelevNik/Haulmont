package main.services;

import main.dao.UserDAO;
import main.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;

        for (User v : userDAO.findAll()) {
            if (Objects.equals(v.getMail(), username)) user = v;
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public void create(User t) {
        userDAO.create(t);
    }

    public Optional<User> read(UUID uuid) {
        return userDAO.read(uuid);
    }

    public void update(User t) {
        userDAO.update(t);
    }

    public void delete(User t) {
        userDAO.delete(t);
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public boolean idExistInDatabase(UUID uuid) {
        return read(uuid).isPresent();
    }

    public boolean mailExistInDatabase(String mail) {
        List<User> userList = findAll();
        for (User u : userList)
            if (u.getMail().equals(mail)) return true;
        return false;
    }
}
