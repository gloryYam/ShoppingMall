package shop.jpashop.repository.member;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.repository.MemberRepository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository jpaRepository;

    @Override
    public Long save(Member member) {
        log.info("save: member={}", member);
        return jpaRepository.save(member).getId();
    }

    @Override
    public void delete(Member member) {
        jpaRepository.delete(member);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

}
