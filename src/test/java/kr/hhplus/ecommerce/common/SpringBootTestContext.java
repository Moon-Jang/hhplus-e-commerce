package kr.hhplus.ecommerce.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class SpringBootTestContext {
    protected static final String ISO_DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*";

    @Autowired
    protected MockMvc mockMvc;
}
