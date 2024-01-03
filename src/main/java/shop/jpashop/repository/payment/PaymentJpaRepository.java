package shop.jpashop.repository.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.payment.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByNumber(Long number);

}
