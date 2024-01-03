package shop.jpashop.domain.payment.repository;

import java.util.Optional;
import shop.jpashop.domain.payment.entity.Payment;

public interface PaymentRepository {

    Long save(Payment payment);

    Optional<Payment> findByNum(Long Number);

}
