package shop.jpashop.repository.notice;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import shop.jpashop.domain.notice.entity.Notice;
import shop.jpashop.domain.notice.repository.NoticeRepository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository jpaRepository;

    @Override
    public Long save(Notice notice) {
        return jpaRepository.save(notice)
                .getId();
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void delete(Notice notice) {
        jpaRepository.delete(notice);
    }

    @Override
    public Page<Notice> searchNotices(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

}
