package sorokin.java.course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProperties {

    @Value("${db.driver}") private String driver;
    @Value("${db.url}") private String url;
    @Value("${db.username}") private String username;
    @Value("${db.password}") private String password;
    @Value("${db.dialect}") private String dialect;

    @Value("${hibernate.hbm2ddl.auto}") private String hbm2ddlAuto;
    @Value("${hibernate.show_sql}") private String showSql;
    @Value("${hibernate.format_sql}") private String formatSql;

    public String getDriver() { return driver; }
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDialect() { return dialect; }
    public String getHbm2ddlAuto() { return hbm2ddlAuto; }
    public String getShowSql() { return showSql; }
    public String getFormatSql() { return formatSql; }
}
