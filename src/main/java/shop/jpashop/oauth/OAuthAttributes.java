package shop.jpashop.oauth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.entity.MemberRole;
import shop.jpashop.domain.member.entity.SocialType;
import shop.jpashop.oauth.userinfo.GoogleOAuth2UserInfo;
import shop.jpashop.oauth.userinfo.KakaoOAuth2UserInfo;
import shop.jpashop.oauth.userinfo.NaverOAuth2UserInfo;
import shop.jpashop.oauth.userinfo.OAuth2UserInfo;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey;
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SocialType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return Member.builder()
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .name(oauth2UserInfo.getNickname())
                .imageUrl(oauth2UserInfo.getImageUrl())
                .role(MemberRole.GUEST)
                .build();
    }
}