package shop.jpashop.repository.qna;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.jpashop.domain.qna.dto.QuestionSearchCondition;
import shop.jpashop.domain.qna.dto.QuestionSearchDto;

public interface QuestionCustom {

    Page<QuestionSearchDto> searchQuestion(QuestionSearchCondition condition, Pageable pageable);

}
