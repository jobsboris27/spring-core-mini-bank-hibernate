package sorokin.java.course.account;

import org.springframework.stereotype.Component;
import sorokin.java.course.account.Account;
import sorokin.java.course.repository.AccountRepository;
import sorokin.java.course.user.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountProperties accountProperties;

    public AccountService(AccountProperties accountProperties, AccountRepository accountRepository) {
        this.accountProperties = accountProperties;
        this.accountRepository = accountRepository;
    }

    public Account createAccount(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        Account newAccount = new Account(user, accountProperties.getDefaultAmount());
        accountRepository.save(newAccount);

        return newAccount;
    }

    public Optional<Account> findAccountById(Integer id) {
        validatePositiveId(id, "account id");
        return accountRepository.findById(id.longValue());
    }

    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public void withdraw(Integer fromAccountId, Integer amount) {
        validatePositiveId(fromAccountId, "account id");
        validatePositiveAmount(amount);

        BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);
        accountRepository.withdrawFunds(fromAccountId.longValue(), amountBigDecimal);
    }

    public void deposit(Integer toAccountId, Integer amount) {
        validatePositiveId(toAccountId, "account id");
        validatePositiveAmount(amount);

        BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);
        accountRepository.depositFunds(toAccountId.longValue(), amountBigDecimal);
    }

    public void closeAccount(Integer accountId) {
        validatePositiveId(accountId, "account id");
        accountRepository.closeAccountAndTransferFunds(accountId.longValue());
    }

    public void transfer(int fromAccountId, int toAccountId, int amount) {
        validatePositiveId(fromAccountId, "source account id");
        validatePositiveId(toAccountId, "target account id");
        validatePositiveAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("source and target account id must be different");
        }

        accountRepository.transfer(fromAccountId, toAccountId, amount, accountProperties.getTransferCommission());
    }

    private void validatePositiveId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be > 0");
        }
    }

    private void validatePositiveAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}
