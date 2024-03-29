package shop.jpashop.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.jpashop.domain.cart.dto.CartItemDto;
import shop.jpashop.domain.cart.dto.CartListDto;
import shop.jpashop.domain.cart.entity.Cart;
import shop.jpashop.domain.cart.entity.CartItem;
import shop.jpashop.domain.item.entity.Image;
import shop.jpashop.domain.item.entity.Item;
import shop.jpashop.domain.item.service.ItemService;
import shop.jpashop.domain.item.service.S3Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartFacade {

    private final CartItemService cartItemService;
    private final CartService cartService;
    private final ItemService itemService;
    private final S3Service s3Service;

    /**
     * 장바구니 상품 추가
     */
    @Transactional
    public Long addCartItem(CartItemDto cartItemDto, User user) {
        Cart cart = cartService.findCart(user.getUsername());
        Item item = itemService.findItem(cartItemDto.getItemId());

        CartItem cartItem = toEntity(cartItemDto, item, cart);

        return cartItemService.addCartItem(cartItem);
    }

    /**
     * 장바구니 상품 조회
     */
    @Transactional
    public List<CartListDto> findCartItems(User user) {
        Cart cart = cartService.findCart(user.getUsername());
        List<CartItem> cartItems = cartItemService.findCartItemList(cart);

        List<Image> mainImages = cartItems.stream()
                .map(CartItem::getItem) // CartItem -> Item
                .map(Item::getMainImage)// Item -> Image
                .collect(toList());

        return toDto(cartItems, mainImages);
    }

    /**
     * 장바구니 상품 수량과 상품 재고 비교
     */
    public Boolean checkItemStock(CartItemDto cartItemDto) {
        Item item = itemService.findItem(cartItemDto.getItemId());
        return item.checkStock(cartItemDto.getQuantity());
    }

    /**
     * 장바구니에 담을 상품 재고 조회
     */
    public int getItemStock(CartItemDto cartItemDto) {
        Item item = itemService.findItem(cartItemDto.getItemId());
        return item.getStockQuantity();
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @Transactional
    public Long updateCartItemQuantity(Long cartItemId, CartItemDto cartItemDto) {
        return cartItemService.updateCartItemQuantity(cartItemId, cartItemDto.getQuantity());
    }

    /**
     * 장바구니 상품 삭제
     */
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
    }

    private CartItem toEntity(CartItemDto cartItemDto, Item item, Cart cart) {
        return CartItem.builder()
                .cart(cart)
                .item(item)
                .quantity(cartItemDto.getQuantity())
                .build();
    }

    private CartListDto toDto(CartItem cartItem, Image mainImage) {
        return CartListDto.builder()
                .cartItemId(cartItem.getId())
                .itemId(cartItem.getItemId())
                .itemName(cartItem.getItemName())
                .itemPrice(cartItem.getItemPrice())
                .quantity(cartItem.getQuantity())
                .mainImageUrl(s3Service.getMainImageUrl(mainImage.getStoreFileName()))
                .itemStock(cartItem.getItemStock())
                .build();
    }

    private List<CartListDto> toDto(List<CartItem> cartItems, List<Image> mainImages) {
        List<CartListDto> cartListDtos = new ArrayList<>();

        for (int i = 0; i < cartItems.size(); i++) {
            cartListDtos.add(toDto(cartItems.get(i), mainImages.get(i)));
        }

        return cartListDtos;
    }

}
