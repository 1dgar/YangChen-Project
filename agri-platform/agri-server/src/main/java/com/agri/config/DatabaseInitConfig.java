package com.agri.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseInitConfig {

    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            ClassPathResource schemaResource = new ClassPathResource("schema.sql");
            executeSqlFile(jdbcTemplate, schemaResource);
            
            ClassPathResource dataResource = new ClassPathResource("data.sql");
            executeSqlFile(jdbcTemplate, dataResource);
        };
    }
    
    private void executeSqlFile(JdbcTemplate jdbcTemplate, ClassPathResource resource) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line).append("\n");
                
                if (trimmedLine.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            jdbcTemplate.execute(sql);
                        } catch (Exception e) {
                            System.err.println("Error executing SQL: " + e.getMessage());
                        }
                    }
                    sqlBuilder = new StringBuilder();
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading SQL file: " + e.getMessage());
        }
    }
}
