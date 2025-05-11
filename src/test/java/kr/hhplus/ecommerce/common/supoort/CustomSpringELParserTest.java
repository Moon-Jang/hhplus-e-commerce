package kr.hhplus.ecommerce.common.supoort;

import kr.hhplus.ecommerce.common.support.CustomSpringELParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomSpringELParserTest {

    @Test
    @DisplayName("단순 변수 표현식을 파싱할 수 있다")
    void parseSimpleVariable() {
        // given
        String[] paramNames = {"name", "age"};
        Object[] args = {"홍길동", 30};
        String expression = "#name";

        // when
        Object result = CustomSpringELParser.getDynamicValue(paramNames, args, expression);

        // then
        assertThat(result).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("숫자 변수로 산술 연산을 수행할 수 있다")
    void parseArithmeticExpression() {
        // given
        String[] paramNames = {"a", "b"};
        Object[] args = {10, 20};
        String expression = "#a + #b";

        // when
        Object result = CustomSpringELParser.getDynamicValue(paramNames, args, expression);

        // then
        assertThat(result).isEqualTo(30);
    }

    @Test
    @DisplayName("조건식을 평가할 수 있다")
    void parseConditionalExpression() {
        // given
        String[] paramNames = {"age"};
        Object[] args = {25};
        String expression = "#age > 18 ? '성인' : '미성년자'";

        // when
        Object result = CustomSpringELParser.getDynamicValue(paramNames, args, expression);

        // then
        assertThat(result).isEqualTo("성인");
    }

    @Test
    @DisplayName("객체의 메서드를 호출할 수 있다")
    void parseMethodCall() {
        // given
        String[] paramNames = {"text"};
        Object[] args = {"Hello, World!"};
        String expression = "#text.toLowerCase()";

        // when
        Object result = CustomSpringELParser.getDynamicValue(paramNames, args, expression);

        // then
        assertThat(result).isEqualTo("hello, world!");
    }

    @Test
    @DisplayName("복합 객체의 속성에 접근할 수 있다")
    void parseComplexObjectProperty() {
        // given
        Map<String, Object> user = new HashMap<>();
        user.put("name", "김철수");
        user.put("age", 35);
        
        String[] paramNames = {"user"};
        Object[] args = {user};
        String expression = "#user['name']";

        // when
        Object result = CustomSpringELParser.getDynamicValue(paramNames, args, expression);

        // then
        assertThat(result).isEqualTo("김철수");
    }

    @Test
    @DisplayName("여러 변수를 조합한 표현식을 평가할 수 있다")
    void parseCombinedExpression() {
        // given
        String[] paramNames = {"firstName", "lastName", "age"};
        Object[] args = {"길동", "홍", 30};
        String expression = "#lastName + #firstName + '은 ' + (#age >= 30 ? '30대 이상' : '20대 이하') + '입니다'";

        // when
        Object result = CustomSpringELParser.getDynamicValue(paramNames, args, expression);

        // then
        assertThat(result).isEqualTo("홍길동은 30대 이상입니다");
    }
}