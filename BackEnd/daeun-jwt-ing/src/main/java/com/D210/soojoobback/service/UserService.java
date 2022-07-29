package com.D210.soojoobback.service;

import com.D210.soojoobback.UserDetailsImpl;
import com.D210.soojoobback.dto.user.*;
import com.D210.soojoobback.entity.User;
import com.D210.soojoobback.exception.CustomErrorException;
import com.D210.soojoobback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Long update(Long id, UserDTO userDTO){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 아이디가 없습니다."));
        user.update(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
        return id;
    }

    @Transactional
    public UserDTO findById(Long id){
        User user = userRepository.findById(id).orElseThrow(() ->  new IllegalArgumentException("해당 아이디가 없습니다."));
        return new UserDTO(user);
    }

    @Transactional
    public User save(SignupRequestDto requestDto){
        String email = requestDto.getEmail();
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String role = "ROLE_USER";
        Optional<User> emailFound = userRepository.findByEmail(email);


        if (emailFound.isPresent()) {
            throw new CustomErrorException("중복된 이메일 입니다 ");
        } else if (!isValidEmail(email)) {
            throw new CustomErrorException("이메일 형식이 올바르지 않습니다");
        } else if (password.length() < 4 || password.length() > 12) {
            throw new CustomErrorException("비밀번호를 6자 이상  12자 이하로 입력하세요");
        } else if (password.contains(email)) {
            throw new CustomErrorException("패스워드는 아이디를 포함할 수 없습니다.");
        }

        // 패스워드 인코딩
        password = passwordEncoder.encode(password);
        requestDto.setPassword(password);

        User user = new User(username, password, email, role);

        userRepository.save(user);
        return user;
    }

    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) { err = true; } return err;
    }

    public User login(LoginUserDto loginUserDto) {
        User user = userRepository.findByEmail(loginUserDto.getEmail()).orElseThrow(
                () -> new CustomErrorException("이메일을 찾을 수 없습니다")
        );
        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new CustomErrorException("비밀번호가 일치하지 않습니다");
        }
        return user;
    }

    public LoginDetailResponseDto toSetLoginDetailResponse(User user) {

        return user.toBuildDetailUser();
    }

    public boolean deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomErrorException("이메일을 찾을 수 없습니다")
        );
        userRepository.delete(user);
        return true;
    }

//    public void nicknameCheck(String nickname) {
//        User nicknameFound = userRepository.findByUsername(nickname);
//
//        if (nicknameFound.isPresent()) {
//            throw new CustomErrorException("중복된 닉네임 입니다 ");
//        }
//    }

    //이메일 중복 검사
    public void emailCheck(String email) {
        Optional<User> emailFound = userRepository.findByEmail(email);

        if (emailFound.isPresent()) {
            throw new CustomErrorException("중복된 이메일 입니다 ");
        }
    }

    public List<UserInfoDetailsDto> detailsUserInfo(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        List<UserInfoDetailsDto> userInfoDetailsDtoList = new ArrayList<>();
        UserInfoDetailsDto userInfoDetailsDto = new UserInfoDetailsDto(user);

        userInfoDetailsDtoList.add(userInfoDetailsDto);
        return userInfoDetailsDtoList;
    }

    public User userFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) userDetails).getUser();
        } else {
            throw new CustomErrorException("로그인이 필요합니다.");
        }
    }
}