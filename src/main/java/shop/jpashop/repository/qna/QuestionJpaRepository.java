package shop.jpashop.repository.qna;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.qna.entity.Question;

public interface QuestionJpaRepository extends JpaRepository<Question, Long>, QuestionCustom {

}
