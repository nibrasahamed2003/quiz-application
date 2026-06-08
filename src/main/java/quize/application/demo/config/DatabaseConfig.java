package quize.application.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final Environment environment;

    public DatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        String url = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");

        // Fix Render's database URL format (adds jdbc: prefix if missing)
        if (url != null && !url.startsWith("jdbc:") && url.startsWith("postgresql:")) {
            url = "jdbc:" + url;
            System.out.println("=== Fixed database URL to include jdbc: prefix ===");
        }

        // Add SSL mode for PostgreSQL if not present
        if (url != null && url.startsWith("jdbc:postgresql") && !url.contains("sslmode")) {
            url = url + "?sslmode=require";
            System.out.println("=== Added SSL mode to PostgreSQL URL ===");
        }

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);

        // Auto-detect driver based on URL
        if (url != null && url.startsWith("jdbc:postgresql")) {
            dataSourceBuilder.driverClassName("org.postgresql.Driver");
            System.out.println("=== Using PostgreSQL Driver ===");
        } else if (url != null && url.startsWith("jdbc:mysql")) {
            dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
            System.out.println("=== Using MySQL Driver ===");
        }

        return dataSourceBuilder.build();
    }
}
