package shop.jpashop.domain.item.dto;

import static lombok.AccessLevel.PROTECTED;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import shop.jpashop.domain.item.entity.Category;

/* 상품 상세 및 수정 폼 조회 시 전달 */
@Data
@NoArgsConstructor(access = PROTECTED)
public class ItemResponseDto {

    @NotNull
    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Length(min = 0, max = 20, message = "이름은 최대 20글자입니다.")
    private String name;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockQuantity;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    private String mainImage; // 메인 이미지 s3 url

    private String detailImage; // 상세 이미지 s3 url

    @Builder
    private ItemResponseDto(Long id, String name, Integer price, Integer stockQuantity, Category category, String mainImageUrl, String detailImageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.mainImage = mainImageUrl;
        this.detailImage = detailImageUrl;
    }

}
