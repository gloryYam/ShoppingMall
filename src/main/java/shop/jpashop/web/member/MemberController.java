package shop.jpashop.web.member;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import shop.jpashop.domain.member.dto.EmailRequest;
import shop.jpashop.domain.member.dto.MemberFormDto;
import shop.jpashop.domain.member.service.MemberFacade;
import java.net.URI;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberFacade memberFacade;

    /**
     * 회원 가입
     */
    @GetMapping("/new")
    public String newMemberForm(@ModelAttribute("memberForm") MemberFormDto memberForm) {
        log.info("member controller");

        return "members/register";
    }

    @ResponseBody
    @PostMapping("/new")
    public ResponseEntity<String> newMember(@RequestBody @Valid MemberFormDto memberFormDto, BindingResult bindingResult) {
        log.info(String.valueOf(memberFormDto));

        if (!memberFormDto.isEmailAuth()) {
            bindingResult.rejectValue("emailAuth", "emailAuthInCorrect",
                    "인증번호을 확인해주세요.");
        }

        if (!memberFormDto.getPassword1().equals(memberFormDto.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "비밀번호가 일치하지 않습니다.");
        }

        // 검증 실패시 400
        if (bindingResult.hasErrors()) {
            log.info("member controller post");
            StringBuilder sb = new StringBuilder();

            for (FieldError error : bindingResult.getFieldErrors())
                sb.append(error.getDefaultMessage());

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        } else {
            memberFacade.register(memberFormDto);

            URI location = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .path("sprot.shop")
                    .build()
                    .toUri();

            // 성공 시 201
            return ResponseEntity.created(location).build();
        }
    }

    /**
     * 이메일 전송
     */
    @PostMapping("/email")
    public ResponseEntity<String> authEmail(@RequestBody @Valid EmailRequest emailRequest) {
        log.info("member email controller");
        String authKey = memberFacade.authEmail(emailRequest);
        //성공했다면 인증번호와 함께 200 통신
        return new ResponseEntity<>(authKey, HttpStatus.OK);
    }

    /**
     * 로그인
     */
    @GetMapping("/login")
    public String loginForm() {
        log.info("member login controller");
        return "members/login-form";
    }

    /**
     * 회원 수정
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String editMemberForm(@AuthenticationPrincipal User user, Model model) {
        log.info("회원 수정 폼으로 이동");
        model.addAttribute("memberForm", memberFacade.updateForm(user));

        return "members/update";
    }

    @ResponseBody
    @PutMapping("/update")
    public ResponseEntity editMember(@RequestBody @Valid MemberFormDto memberForm, BindingResult bindingResult) {
        log.info("member update-form controller");
        log.info(String.valueOf(memberForm));

        if (!memberForm.getPassword1().equals(memberForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "비밀번호가 일치하지 않습니다.");
        }

        // 검증 실패시 400
        if (bindingResult.hasErrors()) {
            log.info("member update controller");
            StringBuilder sb = new StringBuilder();

            for (FieldError error : bindingResult.getFieldErrors())
                sb.append(error.getDefaultMessage());

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        } else {
            memberFacade.update(memberForm);

            URI location = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .path("sports.shop/members/update")
                    .build()
                    .toUri();

            // 성공 시 201
            return ResponseEntity.created(location).build();
        }
    }

    /**
     * 회원 탈퇴(삭제)
     */
    @GetMapping("/delete")
    public String deleteForm(@ModelAttribute EmailRequest emailRequest) {
        log.info("member delete form controller");
        return "members/delete";
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@AuthenticationPrincipal User user) {
        log.info("member delete controller");
        memberFacade.delete(user.getUsername());

        // 성공 시 200
        return new ResponseEntity<>(HttpStatus.OK);
    }
}