package ced.usermanagement.controller;

import ced.usermanagement.model.User;
import ced.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api/user")
public class UserController {

    private final PasswordEncoder encoder;
    private final UserRepository repository;

    @Autowired
    public UserController(PasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return ResponseEntity.ok(repository.save(user));
    }

    @GetMapping("/checkPassword")
    public ResponseEntity<Boolean> checkPassword(@RequestParam String login,
                                                 @RequestParam String password){
        Optional<User> user = repository.findByLogin(login);
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        if(encoder.matches(password, user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }
}
