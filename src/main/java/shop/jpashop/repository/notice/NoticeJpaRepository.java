package shop.jpashop.repository.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.notice.entity.Notice;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {

}
