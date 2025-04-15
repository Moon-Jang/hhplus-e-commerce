package kr.hhplus.ecommerce.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Nested
    @DisplayName("활성 상태 확인 테스트")
    class IsActiveTest {
        @Test
        void 활성_상태인_경우_true() {
            // given
            User user = new UserFixture()
                .setWithdrawnAt(null)
                .create();

            // when
            boolean result = user.isActive();

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 활성_상태가_아닌_경우_false() {
            // given
            User user = new UserFixture()
                .setWithdrawnAt(LocalDateTime.now().minusDays(1))
                .create();

            // when
            boolean result = user.isActive();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("탈퇴 여부 확인 테스트")
    class IsWithdrawnTest {
        @Test
        void 탈퇴한_경우_true() {
            // given
            User user = new UserFixture()
                .setWithdrawnAt(LocalDateTime.now().minusDays(1))
                .create();

            // when
            boolean result = user.isWithdrawn();

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 탈퇴안한_경우_false() {
            // given
            User user = new UserFixture()
                .setWithdrawnAt(null)
                .create();

            // when
            boolean result = user.isWithdrawn();

            // then
            assertThat(result).isFalse();
        }
    }
}