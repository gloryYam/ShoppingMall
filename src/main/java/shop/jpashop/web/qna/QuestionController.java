package shop.jpashop.web.qna;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static shop.jpashop.domain.member.entity.MemberRole.ADMIN;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.jpashop.domain.common.Pages;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.service.MemberService;
import shop.jpashop.domain.qna.dto.AnswerFormDto;
import shop.jpashop.domain.qna.dto.QuestionCheckPasswordFormDto;
import shop.jpashop.domain.qna.dto.QuestionFormDto;
import shop.jpashop.domain.qna.dto.QuestionSaveFormDto;
import shop.jpashop.domain.qna.dto.QuestionSearchCondition;
import shop.jpashop.domain.qna.dto.QuestionSearchDto;
import shop.jpashop.domain.qna.service.QnAFacade;
import shop.jpashop.oauth.CustomOAuth2User;
import shop.jpashop.web.CommonUserDetails;
import shop.jpashop.web.anotation.CurrentUser;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QuestionController {

    private final QnAFacade qnAFacade;
    private final MemberService memberService;

    @GetMapping("/new")
    public String newQuestionForm(@ModelAttribute QuestionSaveFormDto questionSaveFormDto) {
        return "qna/register";
    }

    @PostMapping("/new")
    public String newQuestion(@Valid @ModelAttribute QuestionSaveFormDto questionSaveFormDto,
                              BindingResult bindingResult,
                              @CurrentUser CommonUserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "qna/register";
        }
        qnAFacade.questionRegister(questionSaveFormDto, userDetails);
        return "redirect:/qna";
    }

    @GetMapping
    public String searchQuestion(Model model,
                                 @ModelAttribute QuestionSearchCondition questionSearchCondition,
                                 Pageable pageable) {
        Page<QuestionSearchDto> questions = qnAFacade.searchQuestion(pageable, questionSearchCondition);
        Pages<QuestionSearchDto> pages = Pages.of(questions, 5);

        model.addAttribute("questions", questions);
        model.addAttribute("pages", pages.getPages());
        return "qna/list";
    }

    @RequestMapping(value = "/{questionId}/authority", method = {GET, POST})
    public String findOne(@PathVariable Long questionId,
                          Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @ModelAttribute AnswerFormDto answerFormDto,
                          @CurrentUser CommonUserDetails userDetails) {
        Page<AnswerFormDto> answers = qnAFacade.findAnswersByQuestionId(questionId, page);
        Pages<AnswerFormDto> pages = Pages.of(answers, 4);

        QuestionFormDto questionFormDto = qnAFacade.toDto(questionId);
        model.addAttribute("questionFormDto", questionFormDto);
        model.addAttribute("answers", answers);
        model.addAttribute("pages", pages.getPages());
        model.addAttribute("checkRole", qnAFacade.isAuthorized(questionId, userDetails));
        return "qna/detail-view";
    }

    @GetMapping("/{questionId}/update")
    public String updateForm(@PathVariable Long questionId,
                             Model model,
                             @CurrentUser CommonUserDetails userDetails) {
        if (!qnAFacade.isAuthorized(questionId, userDetails)) {
            return String.format("redirect:/qna/%s", questionId);
        }

        model.addAttribute("questionFormDto", qnAFacade.toDto(questionId));
        model.addAttribute("checkRole", qnAFacade.isAuthorized(questionId, userDetails));
        return "qna/update";
    }

    @PostMapping("/{questionId}/update")
    public String update(@PathVariable Long questionId,
                         @Valid @ModelAttribute QuestionFormDto questionFormDto,
                         @AuthenticationPrincipal User user) {
        qnAFacade.update(questionId, questionFormDto, user);
        return String.format("redirect:/qna/%s", questionId);
    }

    @PostMapping("/{questionId}/delete")
    public String delete(@PathVariable Long questionId,
                         @ModelAttribute QuestionFormDto questionFormDto,
                         @CurrentUser CommonUserDetails userDetails) {
        if (qnAFacade.isAuthorized(questionId, userDetails)) {
            qnAFacade.deleteQuestion(questionId);
        }
        return "redirect:/qna";
    }

    @GetMapping("/{questionId}")
    public String passwordCheckForm(
            @ModelAttribute QuestionCheckPasswordFormDto questionCheckPasswordFormDto,
            @PathVariable Long questionId,
            @AuthenticationPrincipal User user) {
        String userEmail = user.getUsername();
        Member member = memberService.findMember(userEmail);

        if (member.getRole()==ADMIN) {
            return String.format("forward:/qna/%d/authority", questionId);
        }

        return "qna/check-password";
    }

    @PostMapping("/{questionId}")
    public String passwordCheck(
            @Valid @ModelAttribute QuestionCheckPasswordFormDto questionCheckPasswordFormDto,
            BindingResult bindingResult,
            @PathVariable Long questionId) {
        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "qna/check-password";
        }

        if (qnAFacade.checkPassword(questionId, questionCheckPasswordFormDto)) {
            return String.format("forward:/qna/%d/authority", questionId);
        }

        return "qna/check-password";
    }

}
