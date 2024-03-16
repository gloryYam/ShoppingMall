package shop.jpashop.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.entity.MemberRole;

import java.util.ArrayList;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignResponse {

    private Long id;

    private String email;

    private String name;

    private MemberRole role;

    private String token;

    public SignResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.role = member.getRole();
        this.token = member.getRefreshToken();
    }

}