package com.ecommerce.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class InventoryServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    // Quick sanity check at startup to ensure Liquibase ran and table exists
    @Bean
    public CommandLineRunner sanityCheck(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product_batch", Integer.class);
                log.info("product_batch table exists, rows={}", count);
            } catch (Exception ex) {
                log.error("product_batch table not available or error querying it", ex);
            }
        };
    }
}
