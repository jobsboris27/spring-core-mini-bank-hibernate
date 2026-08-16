package sorokin.java.course.user;

import org.springframework.stereotype.Component;
import sorokin.java.course.account.AccountService;
import sorokin.java.course.repository.UserRepository;
import sorokin.java.course.user.User;

import java.util.*;

@Component
public class UserService {

    private final AccountService accountService;
    private final UserRepository userRepository;

    public UserService(AccountService accountService, UserRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    public User createUser(String login) {
        String normalizedLogin = validateLogin(login);
        if (userRepository.existsByLogin(normalizedLogin)) {
            throw new IllegalArgumentException("User already exists with login=%s".formatted(normalizedLogin));
        }

        var user = new User();
        user.setLogin(normalizedLogin);

        userRepository.save(user);

//        var defaultAccount = accountService.createAccount(user);
//        user.getAccountList().add(defaultAccount);

//        userMap.put(idCounter, user);
//        takenLogins.add(normalizedLogin);
        return user;
    }

    public User findUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("user id must be > 0");
        }
        var user = userRepository.findById(id.longValue());
        if (user == null) {
            throw new IllegalArgumentException("No such user with id=%s".formatted(id));
        }
        return user;
    }

    public List<User> findAllWithAccounts() {
        return userRepository.findAllWithAccounts();
    }

    private String validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login must not be blank");
        }
        return login.trim();
    }
}
