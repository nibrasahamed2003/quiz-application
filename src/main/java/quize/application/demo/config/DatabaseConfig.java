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

        System.out.println("========================================");
        System.out.println("=== Database Config Starting ===");
        System.out.println("=== Raw URL (first 60 chars): " + (url != null ? url.substring(0, Math.min(60, url.length())) : "null") + " ===");
        System.out.println("=== Username: " + username + " ===");
        System.out.println("========================================");

        // Fix Render's database URL format (adds jdbc: prefix if missing)
        if (url != null && !url.startsWith("jdbc:") && url.startsWith("postgresql:")) {
            url = "jdbc:" + url;
            System.out.println("=== Added jdbc: prefix ===");
        }

        // Parse and reconstruct PostgreSQL URL to extract credentials
        if (url != null && url.startsWith("jdbc:postgresql://") && url.contains("@")) {
            System.out.println("=== Parsing PostgreSQL URL with embedded credentials ===");
            
            // Remove jdbc:postgresql:// prefix
            String afterPrefix = url.substring("jdbc:postgresql://".length());
            
            // Split by '@' to get "user:pass" and "host/database"
            String[] parts = afterPrefix.split("@", 2);
            if (parts.length == 2) {
                String credentials = parts[0];
                String hostAndDb = parts[1];
                
                // Extract username and password
                String[] credParts = credentials.split(":", 2);
                if (credParts.length == 2) {
                    username = credParts[0];
                    password = credParts[1];
                    
                    // Reconstruct URL without embedded credentials
                    url = "jdbc:postgresql://" + hostAndDb;
                    
                    // Add sslmode if not present
                    if (!url.contains("?")) {
                        url += "?sslmode=require";
                    } else if (!url.contains("sslmode")) {
                        url += "&sslmode=require";
                    }
                    
                    System.out.println("=== URL Parsed Successfully ===");
                    System.out.println("=== Host/DB: " + hostAndDb + " ===");
                    System.out.println("=== Username extracted: " + username + " ===");
                }
            }
        }

        System.out.println("========================================");
        System.out.println("=== Final Configuration ===");
        System.out.println("=== URL (first 60 chars): " + (url != null ? url.substring(0, Math.min(60, url.length())) : "null") + " ===");
        System.out.println("=== Username: " + username + " ===");
        System.out.println("========================================");

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
