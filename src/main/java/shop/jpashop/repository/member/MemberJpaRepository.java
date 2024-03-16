package shop.jpashop.repository.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.entity.SocialType;

import javax.swing.text.html.Option;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
