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

        System.out.println("=== Database URL received: " + (url != null ? url.substring(0, Math.min(50, url.length())) + "..." : "null") + " ===");

        // Fix Render's database URL format (adds jdbc: prefix if missing)
        if (url != null && !url.startsWith("jdbc:") && url.startsWith("postgresql:")) {
            url = "jdbc:" + url;
            System.out.println("=== Fixed database URL to include jdbc: prefix ===");
        }

        // Parse and reconstruct PostgreSQL URL to ensure correct format
        if (url != null && url.startsWith("jdbc:postgresql://")) {
            // Extract the database part after jdbc:postgresql://
            String dbPart = url.substring("jdbc:postgresql://".length());
            
            // Split by '/' to separate credentials from database name
            int firstSlash = dbPart.indexOf('/');
            if (firstSlash > 0) {
                String credentialsAndHost = dbPart.substring(0, firstSlash);
                String database = dbPart.substring(firstSlash + 1);
                
                // Split by '@' to separate user:pass from host
                int atIndex = credentialsAndHost.indexOf('@');
                if (atIndex > 0) {
                    String userPass = credentialsAndHost.substring(0, atIndex);
                    String host = credentialsAndHost.substring(atIndex + 1);
                    
                    // Split user:pass by ':'
                    int colonIndex = userPass.indexOf(':');
                    if (colonIndex > 0) {
                        String dbUser = userPass.substring(0, colonIndex);
                        String dbPass = userPass.substring(colonIndex + 1);
                        
                        // Reconstruct URL properly
                        url = "jdbc:postgresql://" + host + "/" + database + "?sslmode=require";
                        username = dbUser;
                        password = dbPass;
                        
                        System.out.println("=== Reconstructed PostgreSQL URL ===");
                        System.out.println("=== Host: " + host + " ===");
                        System.out.println("=== Database: " + database + " ===");
                    }
                }
            }
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

        System.out.println("=== Final DB URL (first 60 chars): " + (url != null ? url.substring(0, Math.min(60, url.length())) : "null") + " ===");
        return dataSourceBuilder.build();
    }
}
