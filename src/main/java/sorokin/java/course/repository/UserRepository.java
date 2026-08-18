package sorokin.java.course.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import sorokin.java.course.user.User;

import java.util.List;

@Repository
public class UserRepository extends BaseRepository {
    public UserRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public boolean existsByLogin(String login) {
        return executeInTransactionOrJoin(() -> {
            String hql = "select count(u) > 0 from User u where u.login = :login";
            return sessionFactory.getCurrentSession()
                    .createQuery(hql, Boolean.class)
                    .setParameter("login", login)
                    .getSingleResult();
        });
    }

    public User save(User user) {
        return executeInTransactionOrJoin(() -> {
            sessionFactory.getCurrentSession().persist(user);
            return user;
        });
    }


    public User findById(Long id) {
        return executeInTransactionOrJoin(() ->
                sessionFactory.getCurrentSession().get(User.class, id)
        );
    }

    public List<User> findAllWithAccounts() {
        return executeInTransactionOrJoin(() ->
                sessionFactory.getCurrentSession()
                        .createQuery("SELECT u FROM User u LEFT JOIN FETCH u.accountList", User.class)
                        .list()
        );
    }
}