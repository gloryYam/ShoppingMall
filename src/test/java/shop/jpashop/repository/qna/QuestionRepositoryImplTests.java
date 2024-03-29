package shop.jpashop.repository.qna;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.repository.MemberRepository;
import shop.jpashop.domain.qna.dto.QuestionSearchCondition;
import shop.jpashop.domain.qna.dto.QuestionSearchDto;
import shop.jpashop.domain.qna.entity.Answer;
import shop.jpashop.domain.qna.entity.Question;
import shop.jpashop.domain.qna.repository.AnswerRepository;
import shop.jpashop.domain.qna.repository.QuestionRepository;

@SpringBootTest
@Transactional
class QuestionRepositoryImplTests {

    @Autowired
    private QuestionRepository sut;

    @Autowired
    private QuestionJpaRepository jpaRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionCustomImpl questionCustom;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void 질문_저장_기능_테스트() {
        // given
        Member member = Member.builder().build();

        Question question = Question.of("title", "content", member);

        // when
        Long savedId = sut.save(question);

        // then
        assertTrue(jpaRepository.findById(savedId).isPresent());
    }

    @Test
    void 검색_테스트() {
        // given
        Member member = Member.builder()
                .email("abc@naver.com")
                .name("1")
                .password("123")
                .build();

        memberRepository.save(member);

        for (int i = 0; i < 50; i++) {
            Question question = Question.of("title" + i, "count", member);
            jpaRepository.save(question);

            for (int j = 0; j < i; j++) {
                Answer answer = Answer.of("content", question, member);
                answerRepository.save(answer);
            }
        }

        QuestionSearchCondition condition = QuestionSearchCondition.builder()
                .email("ab")
                .build();

        PageRequest pageRequest = PageRequest.of(0, 5);

        // when
        Page<QuestionSearchDto> questionSearchDtos = sut.searchQuestion(condition, pageRequest);
        List<QuestionSearchDto> content = questionSearchDtos.getContent();

        // then
        Assertions.assertThat(content.size()).isEqualTo(pageRequest.getPageSize());
    }

}