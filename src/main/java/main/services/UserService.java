package main.services;

import main.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;

        /*for (User v : userRepository.findAll()) {
            if (Objects.equals(v.getMail(), username)) user = v;
        }*/

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}
