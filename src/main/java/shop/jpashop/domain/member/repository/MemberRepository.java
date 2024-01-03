package shop.jpashop.domain.member.repository;

import java.util.Optional;
import shop.jpashop.domain.member.entity.Member;

public interface MemberRepository {

    Long save(Member member);

    void delete(Member member);

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

}
