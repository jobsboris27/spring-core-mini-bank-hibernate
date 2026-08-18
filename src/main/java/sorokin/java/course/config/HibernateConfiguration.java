package sorokin.java.course.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import sorokin.java.course.account.Account;
import sorokin.java.course.user.User;

@Configuration
public class HibernateConfiguration {
    private final DatabaseProperties dbProps;

    public HibernateConfiguration(DatabaseProperties dbProps) {
        this.dbProps = dbProps;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class)
                .setProperty("hibernate.dialect", dbProps.getDialect())
                .setProperty("hibernate.connection.driver_class", dbProps.getDriver())
                .setProperty("hibernate.connection.url", dbProps.getUrl())
                .setProperty("hibernate.connection.username", dbProps.getUsername())
                .setProperty("hibernate.connection.password", dbProps.getPassword())
                .setProperty("hibernate.show_sql", dbProps.getShowSql())
                .setProperty("hibernate.format_sql", dbProps.getFormatSql())
                .setProperty("hibernate.current_session_context_class", "thread")
                .setProperty("hibernate.hbm2ddl.auto", dbProps.getHbm2ddlAuto());

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }
}
