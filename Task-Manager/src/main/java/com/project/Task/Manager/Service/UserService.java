package com.project.Task.Manager.Service;

import com.project.Task.Manager.Entities.User;
import com.project.Task.Manager.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setOauthId(user.getOauthId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findOrCreateUserByOAuthId(String oauthId, Jwt jwt) {
        return userRepository.findByOauthId(oauthId)
                .or(() -> {
                    User newUser = new User();
                    newUser.setOauthId(oauthId);

                    Map<String, Object> claims = jwt.getClaims();
                    String email = (String) claims.get("email");
                    String name = (String) claims.get("name");

                    newUser.setEmail(email);
                    newUser.setUsername(name != null ? name : email);

                    User savedUser = userRepository.save(newUser);
                    savedUser.setOauthId(oauthId);

                    return Optional.of(userRepository.save(savedUser));
                });
    }
}