package shop.jpashop.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.jpashop.domain.cart.entity.Cart;
import shop.jpashop.domain.order.entity.Order;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    @Embedded
    private Address address;

    private String phoneNumber;

    private String password;

    @Enumerated(STRING)
    private MemberRole role;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;    // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken;

    @OneToMany(mappedBy = "member", fetch = LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "member", fetch = LAZY, cascade = CascadeType.REMOVE)
    private Cart cart;

    @Builder
    private Member(String email, String name,
                   Address address, String phoneNumber,
                   String password, MemberRole role,
                   String imageUrl, SocialType socialType,
                   String socialId, String refreshToken) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.imageUrl = imageUrl;
        this.socialType = socialType;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = MemberRole.USER;
    }

    public void Password(String password) {
        this.password = password;
    }

    public void update(String name, Address address,
                       String phoneNumber, String password) {
        changeName(name);
        changeAddress(address);
        changePhone(phoneNumber);
        changePassword(password);
    }

    private void changePassword(String password) {
        this.password = password;
    }

    private void changePhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void changeAddress(Address address) {
        this.address = address;
    }

    private void changeName(String name) {
        this.name = name;
    }

    public Long addressZipcode() {
        return address.getZipcode();
    }

    public String addressStreet() {
        return address.getStreet_address();
    }

    public String addressJibeon() {
        return address.getJibeon_address();
    }

    public String addressDetail() {
        return address.getDetail_address();
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public void oauthPhoneNum(String phone) {
        this.phoneNumber = phone;
    }

}
