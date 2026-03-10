package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderDetailsTest {

    @Test
    void shouldNormalizeCouponAndOptionalAddressLine2() {
        OrderDetails details = OrderDetails.complete(
                "홍길동",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                "   ",
                " welcome10 ",
                PaymentMethod.CARD
        );

        assertThat(details.getReceiverName()).isEqualTo("홍길동");
        assertThat(details.getAddressLine2()).isNull();
        assertThat(details.getCouponCode()).isEqualTo("WELCOME10");
        assertThat(details.isCompleted()).isTrue();
    }

    @Test
    void shouldReturnEmptySingletonWhenRestoringAllNullFields() {
        OrderDetails restored = OrderDetails.restore(null, null, null, null, null, null, null);

        assertThat(restored).isSameAs(OrderDetails.empty());
        assertThat(restored.isCompleted()).isFalse();
    }

    @Test
    void shouldRestoreCompletedOrderDetails() {
        OrderDetails restored = OrderDetails.restore(
                "홍길동",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                "101동 202호",
                null,
                PaymentMethod.BANK_TRANSFER
        );

        assertThat(restored.getPaymentMethod()).isEqualTo(PaymentMethod.BANK_TRANSFER);
        assertThat(restored.getCouponCode()).isNull();
    }

    @Test
    void shouldThrowWhenPaymentMethodIsNull() {
        assertThatThrownBy(() -> OrderDetails.complete(
                "홍길동",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                null,
                null,
                null
        )).isInstanceOf(InvalidOrderPaymentMethodException.class);
    }

    @Test
    void shouldThrowWhenReceiverNameIsBlank() {
        assertThatThrownBy(() -> OrderDetails.complete(
                " ",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                null,
                null,
                PaymentMethod.CARD
        )).isInstanceOf(InvalidOrderReceiverNameException.class);
    }

    @Test
    void shouldThrowWhenReceiverPhoneIsInvalid() {
        assertThatThrownBy(() -> OrderDetails.complete(
                "홍길동",
                "010-1234-5678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                null,
                null,
                PaymentMethod.CARD
        )).isInstanceOf(InvalidOrderReceiverPhoneException.class);
    }

    @Test
    void shouldThrowWhenZipCodeIsInvalid() {
        assertThatThrownBy(() -> OrderDetails.complete(
                "홍길동",
                "01012345678",
                "6236",
                "서울특별시 강남구 테헤란로 123",
                null,
                null,
                PaymentMethod.CARD
        )).isInstanceOf(InvalidOrderZipCodeException.class);
    }

    @Test
    void shouldThrowWhenAddressLine1IsBlank() {
        assertThatThrownBy(() -> OrderDetails.complete(
                "홍길동",
                "01012345678",
                "06236",
                " ",
                null,
                null,
                PaymentMethod.CARD
        )).isInstanceOf(InvalidOrderAddressException.class);
    }
}
