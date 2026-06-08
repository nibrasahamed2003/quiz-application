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
        // Try DATABASE_URL first (Render standard)
        String databaseUrl = env.getProperty("DATABASE_URL");
        
        // If DATABASE_URL exists (Render), parse it
        if (databaseUrl != null && (databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("jdbc:postgresql://"))) {
            System.out.println("========================================");
            System.out.println("=== Detected DATABASE_URL ===");
            System.out.println("=== Raw URL: " + databaseUrl.substring(0, Math.min(30, databaseUrl.length())) + "... ===");
            System.out.println("========================================");
            
            // Ensure it has jdbc: prefix
            if (!databaseUrl.startsWith("jdbc:")) {
                databaseUrl = "jdbc:" + databaseUrl;
            }
            
            // Parse the URL to extract credentials
            String urlWithoutPrefix = databaseUrl.substring("jdbc:postgresql://".length());
            
            // Split by '@' to separate credentials from host
            int atIndex = urlWithoutPrefix.indexOf('@');
            if (atIndex > 0) {
                String credentials = urlWithoutPrefix.substring(0, atIndex);
                String hostAndDb = urlWithoutPrefix.substring(atIndex + 1);
                
                // Split credentials by ':'
                int colonIndex = credentials.indexOf(':');
                String username = credentials.substring(0, colonIndex);
                String password = credentials.substring(colonIndex + 1);
                
                // Build clean JDBC URL
                String jdbcUrl = "jdbc:postgresql://" + hostAndDb;
                if (!jdbcUrl.contains("?")) {
                    jdbcUrl += "?sslmode=require";
                }
                
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
        
        // Fallback: Try SPRING_DATASOURCE_URL (alternative Render config)
        String springUrl = env.getProperty("SPRING_DATASOURCE_URL");
        if (springUrl != null) {
            System.out.println("=== Using SPRING_DATASOURCE_URL ===");
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(springUrl);
            dataSource.setUsername(env.getProperty("SPRING_DATASOURCE_USERNAME", "root"));
            dataSource.setPassword(env.getProperty("SPRING_DATASOURCE_PASSWORD", "root"));
            return dataSource;
        }
        
        // Final fallback: default configuration (local MySQL)
        System.out.println("=== Using default database configuration (local MySQL) ===");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username", "root"));
        dataSource.setPassword(env.getProperty("spring.datasource.password", "root"));
        
        return dataSource;
    }
}
