package shop.jpashop.domain.qna.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.jpashop.domain.qna.entity.Answer;
import shop.jpashop.domain.qna.entity.Question;

public interface AnswerRepository {

    Long save(Answer answer);

    Optional<Answer> findById(Long id);

    List<Answer> findByQuestion(Question question);

    void delete(Answer answer);

    Page<Answer> findSearchByQuestion(Question question, Pageable pageable);

}
