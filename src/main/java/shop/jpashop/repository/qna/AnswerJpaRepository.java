package shop.jpashop.repository.qna;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.qna.entity.Answer;
import shop.jpashop.domain.qna.entity.Question;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestion(Question question);

    Page<Answer> findSearchByQuestion(Question question, Pageable pageable);

}
