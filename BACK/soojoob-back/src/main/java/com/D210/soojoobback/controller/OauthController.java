package com.D210.soojoobback.controller;


import com.D210.soojoobback.dto.user.LoginDetailResponseDto;
import com.D210.soojoobback.dto.user.LoginResponseDto;
import com.D210.soojoobback.dto.user.ResponseDto;
import com.D210.soojoobback.entity.GoogleUser;
import com.D210.soojoobback.entity.OAuthUserInfo;
import com.D210.soojoobback.entity.Record;
import com.D210.soojoobback.entity.User;
import com.D210.soojoobback.repository.RecordRepository;
import com.D210.soojoobback.repository.UserRepository;
import com.D210.soojoobback.security.JwtTokenProvider;
import com.D210.soojoobback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final UserRepository userRepository;

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RecordRepository recordRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.token.key}")
    private String secretKey;
    @PostMapping("/oauth/jwt/google")
    @ResponseBody
    public ResponseDto jwtCreate(@RequestBody Map<String, Object> data, HttpServletResponse response) {
        System.out.println("jwtCreate 실행됨");
        OAuthUserInfo googleUser =
                new GoogleUser((Map<String, Object>)data);

        User userEntity =
                userRepository.findOByEmail(googleUser.getEmail());

        if(userEntity == null) {
            User userRequest = User.builder()
                    .username(googleUser.getProvider()+"_"+googleUser.getName())
                    .password(bCryptPasswordEncoder.encode(secretKey))
                    .email(googleUser.getEmail())
                    .provider(googleUser.getProvider())
                    .providerId(googleUser.getProviderId())
                    .role("ROLE_USER")
                    .build();

            userEntity = userRepository.save(userRequest);
            Record record = new Record(userEntity);
            recordRepository.save(record);
        }

        String jwtToken = jwtTokenProvider.createToken(userEntity.getEmail());

        response.setHeader("X-AUTH-TOKEN", jwtToken);

        Cookie cookie = new Cookie("X-AUTH-TOKEN", jwtToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        LoginDetailResponseDto loginDetailResponseDto = userService.toSetLoginDetailResponse(userEntity);
        loginResponseDto.setUser(loginDetailResponseDto);
        loginResponseDto.setJwtToken(jwtToken);





        return new ResponseDto(200L, "구글 로그인에 성공했습니다", loginResponseDto);
//        return jwtToken;
    }
}
