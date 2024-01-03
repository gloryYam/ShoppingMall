package shop.jpashop.domain.qna.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.jpashop.domain.qna.dto.QuestionSearchCondition;
import shop.jpashop.domain.qna.dto.QuestionSearchDto;
import shop.jpashop.domain.qna.entity.Question;

public interface QuestionRepository {

    Long save(Question question);

    Optional<Question> findById(Long id);

    void delete(Question question);

    Page<QuestionSearchDto> searchQuestion(QuestionSearchCondition condition, Pageable pageable);

}
