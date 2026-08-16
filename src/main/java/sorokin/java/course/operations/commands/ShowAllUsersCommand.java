package sorokin.java.course.operations.commands;

import org.springframework.stereotype.Component;
import sorokin.java.course.account.Account;
import sorokin.java.course.operations.ConsoleOperationType;
import sorokin.java.course.operations.OperationCommand;
import sorokin.java.course.user.User;
import sorokin.java.course.user.UserService;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
public class ShowAllUsersCommand implements OperationCommand {

    private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        List<User> users = userService.findAllWithAccounts();

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        for (User user : users) {
            String accountsString = user.getAccountList().stream()
                    .map(Account::toString)
                    .collect(Collectors.joining(", ", "[", "]"));

            String result = user.toString().replace("}", ", accountList=%s}".formatted(accountsString));
            System.out.println(result);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
