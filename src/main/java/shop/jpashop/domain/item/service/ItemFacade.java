package shop.jpashop.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.jpashop.domain.item.dto.ItemRequestDto;
import shop.jpashop.domain.item.dto.ItemResponseDto;
import shop.jpashop.domain.item.dto.ItemSearchCondition;
import shop.jpashop.domain.item.dto.ItemSearchDto;
import shop.jpashop.domain.item.entity.Image;
import shop.jpashop.domain.item.entity.Item;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemFacade {

    private final ItemService itemService;
    private final ImageService imageService;
    private final S3Service s3Service;

    /**
     * 상품 등록
     */
    @Transactional
    public Long register(ItemRequestDto itemRequestDto) {
        Image mainImage = imageService.uploadMainImage(itemRequestDto);
        Image detailImage = imageService.uploadDetailImage(itemRequestDto);

        Item item = toEntity(itemRequestDto);
        item.addImage(mainImage, detailImage);

        return itemService.register(item);
    }

    /**
     * 상품 목록 조회
     */
    public Page<ItemSearchDto> findItems(ItemSearchCondition itemSearchCondition, PageRequest pageRequest) {
        return itemService.searchItem(itemSearchCondition, pageRequest);
    }

    /**
     * 특정 상품 조회
     */
    public ItemResponseDto findItem(Long itemId) {
        Item item = itemService.findItem(itemId);
        String mainImageUrl = s3Service.getMainImageUrl(item.getMainImage().getStoreFileName());
        String detailImageUrl = s3Service.getDetailImageUrl(item.getDetailImage().getStoreFileName());

        return toDto(item, mainImageUrl, detailImageUrl);
    }

    /**
     * 상품 수정
     */
    @Transactional
    public Long update(Long itemId, ItemRequestDto itemRequestDto) {
        Item findItem = itemService.findItem(itemId);
        Item newItem = toEntity(itemRequestDto); // 정보

        itemService.update(findItem, newItem);
        imageService.updateMainImage(findItem, itemRequestDto);
        imageService.updateDetailImage(findItem, itemRequestDto);

        return itemId;
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void delete(Long itemId) {
        imageService.deleteImage(itemId); // 서버에 저장된 이미지 삭제
        itemService.delete(itemId);
    }

    public Item toEntity(ItemRequestDto form) {
        return Item.builder()
                .name(form.getName())
                .price(form.getPrice())
                .category(form.getCategory())
                .stockQuantity(form.getStockQuantity())
                .build();
    }

    public ItemResponseDto toDto(Item item, String mainImageUrl, String detailImageUrl) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .category(item.getCategory())
                .mainImageUrl(mainImageUrl)
                .detailImageUrl(detailImageUrl)
                .build();
    }

}
