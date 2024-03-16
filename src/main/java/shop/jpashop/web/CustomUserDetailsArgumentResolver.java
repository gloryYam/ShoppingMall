package shop.jpashop.web;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import shop.jpashop.oauth.CustomOAuth2User;
import shop.jpashop.web.anotation.CurrentUser;

public class CustomUserDetailsArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null &&
            (parameter.getParameterType().isAssignableFrom(CustomOAuth2User.class) ||
                parameter.getParameterType().isAssignableFrom(UserDetails.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if(principal instanceof CustomOAuth2User && parameter.getParameterType().isAssignableFrom(CustomOAuth2User.class)){
            return principal;
        }
        return new UserDetailsWrapper((UserDetails) principal);
    }
}
