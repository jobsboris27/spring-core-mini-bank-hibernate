package sorokin.java.course.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import sorokin.java.course.account.Account;
import sorokin.java.course.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository extends BaseRepository {
    public AccountRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Account save(Account account) {
        return executeInTransactionOrJoin(() -> {
            sessionFactory.getCurrentSession().merge(account);
            return account;
        });
    }

    public void delete(Account account) {
        executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            Account managedAccount = session.contains(account) ? account : (Account) session.merge(account);

            session.remove(managedAccount);
            return null;
        });
    }

    public Optional<Account> findById(Long id) {
        return executeInTransactionOrJoin(() ->
                Optional.ofNullable(sessionFactory.getCurrentSession().get(Account.class, id))
        );
    }

    public List<Account> findByUserId(Long userId) {
        return executeInTransactionOrJoin(() -> {
            return sessionFactory.getCurrentSession()
                    .createQuery("from Account a where a.user.id = :userId", Account.class)
                    .setParameter("userId", userId)
                    .list();
        });
    }

    public void transfer(int fromAccountId, int toAccountId, int amount, BigDecimal commissionRate) {
        executeInTransactionOrJoin(() -> {
            Long fromId = (long) fromAccountId;
            Long toId = (long) toAccountId;

            Account accountFrom = findById(fromId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(fromAccountId)));
            Account accountTo = findById(toId)
                    .orElseThrow(() -> new IllegalArgumentException("No such account: id=%s".formatted(toAccountId)));

            BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);

            if (amountBigDecimal.compareTo(accountFrom.getMoneyAmount()) > 0) {
                throw new IllegalArgumentException(
                        "Insufficient funds on account id=%s, moneyAmount=%s, attempted transfer=%s"
                                .formatted(accountFrom.getId(), accountFrom.getMoneyAmount(), amountBigDecimal)
                );
            }

            accountFrom.setMoneyAmount(accountFrom.getMoneyAmount().subtract(amountBigDecimal));

            BigDecimal amountToTransfer = amountBigDecimal;

            if (!accountFrom.getUser().getId().equals(accountTo.getUser().getId())) {
                BigDecimal multiplier = BigDecimal.ONE.subtract(commissionRate);
                amountToTransfer = amountBigDecimal.multiply(multiplier);
            }

            accountTo.setMoneyAmount(accountTo.getMoneyAmount().add(amountToTransfer));
            return null;
        });
    }

    public void closeAccountAndTransferFunds(Long accountId) {
        executeInTransactionOrJoin(() -> {
            Session session = sessionFactory.getCurrentSession();
            String query = "select a from Account a join fetch a.user u join fetch u.accountList where a.id = :id";
            Account accountToClose = Optional.ofNullable(
                            session.createQuery(query, Account.class)
                                    .setParameter("id", accountId)
                                    .getSingleResultOrNull())
                    .orElseThrow();

            List<Account> userAccounts = accountToClose.getUser().getAccountList();

            if (userAccounts.size() == 1) {
                throw new IllegalStateException("Can't close the only one account");
            }

            Account accountToTransferMoney = userAccounts.stream()
                    .filter(it -> !it.getId().equals(accountToClose.getId()))
                    .findFirst()
                    .orElseThrow();

            BigDecimal newAmount = accountToTransferMoney.getMoneyAmount().add(accountToClose.getMoneyAmount());
            accountToTransferMoney.setMoneyAmount(newAmount);

            accountToClose.getUser().getAccountList().remove(accountToClose);
            session.remove(accountToClose);

            return null;
        });
    }

    public void depositFunds(Long accountId, BigDecimal amount) {
        executeInTransactionOrJoin(() -> {
            int updatedRows = sessionFactory.getCurrentSession()
                    .createMutationQuery("update Account a set a.moneyAmount = a.moneyAmount + :amount where a.id = :id")
                    .setParameter("amount", amount)
                    .setParameter("id", accountId)
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalArgumentException("No such account: id=%s".formatted(accountId));
            }
            return null;
        });
    }

    public void withdrawFunds(Long accountId, BigDecimal amount) {
        executeInTransactionOrJoin(() -> {
            int updatedRows = sessionFactory.getCurrentSession()
                    .createMutationQuery("update Account a set a.moneyAmount = a.moneyAmount - :amount where a.id = :id and a.moneyAmount >= :amount")
                    .setParameter("amount", amount)
                    .setParameter("id", accountId)
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalArgumentException(
                        "Transaction failed: account id=%s not found or insufficient funds".formatted(accountId)
                );
            }
            return null;
        });
    }

}