package kr.hhplus.ecommerce.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanUp {
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public void all() {
        var tables = entityManager.getMetamodel().getEntities().stream()
                .map(EntityType::getName)
                .toList();

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        tables.forEach(table -> {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        });
    }
}
