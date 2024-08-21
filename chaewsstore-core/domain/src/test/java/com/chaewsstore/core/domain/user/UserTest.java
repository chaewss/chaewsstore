package com.chaewsstore.core.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.globalutils.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User 클래스")
class UserTest {

    @BeforeEach
    void setUp() {
        user = User.create("username", "password", "nickname", Role.ASSOCIATE);
    }

    @Test
    @DisplayName("create 메서드는 유저 생성 시, 초기 값이 올바르게 설정된다")
    void should_create_user() {
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getNickname()).isEqualTo("nickname");
        assertThat(user.getAccount()).isZero();
        assertThat(user.getRole()).isEqualTo(Role.ASSOCIATE);
        assertThat(user.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("deposit 메서드는 입금 금액만큼 계좌에 잔액이 추가된다")
    void should_deposit_money_to_account() {
        // given
        Long amountToDeposit = 1000L;

        // when
        user.deposit(amountToDeposit);

        // then
        assertThat(user.getAccount()).isEqualTo(1000L);
    }

    @Nested
    @DisplayName("withdraw 메서드는")
    class withdraw_money {

        @Test
        @DisplayName("계좌 잔액이 출금액 이상일 경우 출금에 성공한다")
        void should_withdraw_money_from_account() {
            // given
            user.deposit(1000L);
            Long amountToWithdraw = 500L;

            // when
            user.withdraw(amountToWithdraw);

            // then
            assertThat(user.getAccount()).isEqualTo(500L);
        }

        @Test
        @DisplayName("계좌 잔액이 출금액 미만일 경우 BadRequestException이 발생한다")
        void should_throw_BadRequestException_when_withdrawing_more_than_balance() {
            // given
            user.deposit(500L);
            Long amountToWithdraw = 1000L;

            // when
            BadRequestException result = assertThrows(BadRequestException.class,
                () -> user.withdraw(amountToWithdraw));

            // then
            assertEquals(UserErrorCode.INSUFFICIENT_BALANCE, result.getResponseCode());
        }
    }

    private User user;
}
