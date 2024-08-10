package com.example.tinkerbell.oAuth.resolver;

import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.service.OAuthService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LoginResolver implements HandlerMethodArgumentResolver {
    private final OAuthService oAuthService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Login paramAnnotation = parameter.getParameterAnnotation(Login.class);
        Class<?> paramType = parameter.getParameterType();
        return Objects.nonNull(paramAnnotation) && paramType.equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String token = webRequest.getHeader("Authorization");
        if (Objects.isNull(token)) {
            throw new ValidationException("토큰 인증 실패: 토큰값이 존재하지 않음");
        }
        if (this.oAuthService.verifyToken(token)) {
            return this.oAuthService.getUserFromToken(token);
        }
        throw new ValidationException("토큰 인증 실패: " + token);
    }
}
