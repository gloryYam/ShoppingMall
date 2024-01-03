package shop.jpashop.repository.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.member.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

}
