package quize.application.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        String databaseUrl = env.getProperty("DATABASE_URL");
        
        // If DATABASE_URL exists (Render), parse it
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            System.out.println("========================================");
            System.out.println("=== Detected Render DATABASE_URL ===");
            System.out.println("========================================");
            
            // Remove "postgresql://" prefix
            String urlWithoutPrefix = databaseUrl.substring(13);
            
            // Split by '@' to separate credentials from host
            int atIndex = urlWithoutPrefix.indexOf('@');
            if (atIndex > 0) {
                String credentials = urlWithoutPrefix.substring(0, atIndex);
                String hostAndDb = urlWithoutPrefix.substring(atIndex + 1);
                
                // Split credentials by ':'
                int colonIndex = credentials.indexOf(':');
                String username = credentials.substring(0, colonIndex);
                String password = credentials.substring(colonIndex + 1);
                
                // Build JDBC URL
                String jdbcUrl = "jdbc:postgresql://" + hostAndDb + "?sslmode=require";
                
                System.out.println("=== JDBC URL: " + jdbcUrl + " ===");
                System.out.println("=== Username: " + username + " ===");
                System.out.println("========================================");
                
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setDriverClassName("org.postgresql.Driver");
                
                return dataSource;
            }
        }
        
        // Fallback to default configuration (local MySQL)
        System.out.println("=== Using default database configuration (local MySQL) ===");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username", "root"));
        dataSource.setPassword(env.getProperty("spring.datasource.password", "root"));
        
        return dataSource;
    }
}
