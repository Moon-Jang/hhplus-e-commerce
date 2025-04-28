package kr.hhplus.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.ecommerce.common.support.CommonObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfiguration {

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return new CommonObjectMapper();
    }
}
