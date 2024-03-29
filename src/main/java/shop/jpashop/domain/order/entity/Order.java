package shop.jpashop.domain.order.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.jpashop.domain.common.BaseTimeEntity;
import shop.jpashop.domain.member.entity.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(unique = true)
    private String number;

    @Enumerated(STRING) // enum 이름을 db에 저장
    private OrderStatus status;  // 주문 상태[ORDER, CANCEL]

    private Order(Member member, String orderNum, OrderStatus status) {
        this.member = member;
        this.number = orderNum;
        this.status = status;
    }

    public static Order of(Member member, OrderStatus status) {

        LocalDateTime dateTime = LocalDateTime.now();
        String subNum = "";
        for (int i = 1; i <= 6; i++) {
            subNum += (int) (Math.random() * 10);
        } // 6자리의 랜덤 번호 생성
        String orderNum = dateTime.getNano() + "_" + subNum; // 주문번호 생성(나노세컨드_랜덤번호) ex) 180343000_789123

        return new Order(member, orderNum, status);
    }

    /**
     * 주문 취소
     */
    public void cancel(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) {
            return true;
        }
        if (o==null || getClass()!=o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long zipCode() {
        return member.addressZipcode();
    }

    public String streetAddress() {
        return member.addressStreet();
    }

    public String detailAddress() {
        return member.addressDetail();
    }

    public String memberName() {
        return member.getName();
    }

    public String memberPhoneNumber() {
        return member.getPhoneNumber();
    }

}
