package shop.jpashop.web.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shop.jpashop.domain.cart.dto.CartItemDto;
import shop.jpashop.domain.cart.dto.CartListDto;
import shop.jpashop.domain.cart.service.CartFacade;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    /**
     * 장바구니에 상품 담기
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<String> addCartItem(@Valid @ModelAttribute CartItemDto cartItemDto,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal User user) {
        log.info("장바구니에 담을 상품의 id = {}", cartItemDto.getItemId());
        log.info("장바구니에 담을 상품의 수량 = {}", cartItemDto.getQuantity());

        // 상품 재고 검증
        if (Boolean.FALSE.equals(cartFacade.checkItemStock(cartItemDto))) {
            bindingResult.rejectValue("quantity", "StockError",
                    String.format("현재 남은 수량은 %d개 입니다.%n%d개 이하로 담아주세요.", cartFacade.getItemStock(cartItemDto), cartFacade.getItemStock(cartItemDto)));
        }

        // 검증 실패시 400
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();

            for (FieldError error : bindingResult.getFieldErrors())
                sb.append(error.getDefaultMessage());

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 검증 성공시 장바구니에 상품 담기 + 201
        cartFacade.addCartItem(cartItemDto, user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * 장바구니에 담긴 상품 조회
     */
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal User user) {
        List<CartListDto> cartListDtos = cartFacade.findCartItems(user);

        model.addAttribute("cartListDtos", cartListDtos);

        return "carts/list";
    }

    /**
     * 장바구니에 담긴 상품 수량 변경
     */
    @PostMapping("/{cartItemId}/update")
    public @ResponseBody
    ResponseEntity<String> update(@Valid @ModelAttribute CartItemDto cartItemDto,
                                  BindingResult bindingResult,
                                  @PathVariable Long cartItemId) {

        // 상품 재고 검증
        if (Boolean.FALSE.equals(cartFacade.checkItemStock(cartItemDto))) {
            bindingResult.rejectValue("quantity", "StockError",
                    String.format("현재 남은 수량은 %d개 입니다.%n%d개 이하로 담아주세요.", cartFacade.getItemStock(cartItemDto), cartFacade.getItemStock(cartItemDto)));
        }

        // 검증 실패시 400
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();

            for (FieldError error : bindingResult.getFieldErrors())
                sb.append(error.getDefaultMessage());

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 검증 성공시 장바구니 상품 수량 변경 + 200
        cartFacade.updateCartItemQuantity(cartItemId, cartItemDto);
        return ResponseEntity.ok("cartItem quantity update success");
    }

    /**
     * 장바구니에 담긴 상품 삭제
     */
    @PostMapping("/{cartItemId}/delete")
    public @ResponseBody
    ResponseEntity<String> delete(@PathVariable Long cartItemId) {

        cartFacade.deleteCartItem(cartItemId);
        return ResponseEntity.ok("cartItem delete success");
    }

}
