package shop.jpashop.domain.notice.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.jpashop.domain.notice.entity.Notice;

public interface NoticeRepository {

    Long save(Notice notice);

    Optional<Notice> findById(Long id);

    void delete(Notice notice);

    Page<Notice> searchNotices(Pageable pageable);

}
