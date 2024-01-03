package shop.jpashop.repository.payment;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import shop.jpashop.domain.payment.entity.Payment;
import shop.jpashop.domain.payment.repository.PaymentRepository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Long save(Payment payment) {
        return jpaRepository.save(payment).getId();
    }

    @Override
    public Optional<Payment> findByNum(Long Number) {
        return jpaRepository.findByNumber(Number);
    }

}
