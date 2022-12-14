package com.D210.soojoobback.service;


import com.D210.soojoobback.dto.badge.BadgeListResDto;
import com.D210.soojoobback.dto.plogging.PloggingInfoDto;
import com.D210.soojoobback.dto.plogging.PostPloggingReqDto;
import com.D210.soojoobback.entity.Plogging;
import com.D210.soojoobback.entity.Record;
import com.D210.soojoobback.entity.User;
import com.D210.soojoobback.exception.CustomErrorException;
import com.D210.soojoobback.repository.PloggingRepository;
import com.D210.soojoobback.repository.RecordRepository;
import com.D210.soojoobback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class PloggingService {

    private final PloggingRepository ploggingRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    private final RecordRepository recordRepository;

    private final BadgeService badgeService;



    @Transactional
    public List<BadgeListResDto> save(PostPloggingReqDto requestDto, User user) {
        Plogging plogging = Plogging.of(requestDto, user);
        System.out.println("plogging");
        // fetch Lazy 유저를 진짜 유저로 변환
        Long userId = user.getId();
        User ploggingUser = userRepository.findById(userId).orElseThrow(
                () -> new CustomErrorException("유저 정보를 찾을 수 없습니다.")
        );
        //유저에 플로깅 추가
        List<Plogging> ploggingList = ploggingUser.getPloggings();
        ploggingList.add(plogging);

        //

        Double distance = requestDto.getDistance();

        Integer trashCount = requestDto.getTrashCount();

//        Double calorie = requestDto.getCalorie();

        Integer timeRecord = requestDto.getTimeRecord();

        Record record = recordRepository.findById(userId);

//        Double toCal = record.getTotalCalorie();
        Double toDis = record.getTotalDistance();
        Integer toTrash = record.getTotalTrashCount();
        Integer toTimeRec = record.getTotalTimeRecord();
        Double exp = record.getExp();

//        record.setTotalCalorie(toCal + calorie);
        record.setTotalDistance(toDis + distance);
        record.setTotalTrashCount(toTrash + trashCount);
        record.setTotalTimeRecord(toTimeRec + timeRecord);
        record.setExp(exp + trashCount * 0.1);
        if (record.getExp()>99.9) {
            record.setExp(99.0);
        }

        ploggingRepository.save(plogging);
        recordRepository.save(record);


        List<BadgeListResDto> badgeListResDtos = badgeService.checkAddAll(ploggingUser, timeRecord + toTimeRec, (long) (toDis+distance), toTrash+trashCount);
        List<BadgeListResDto> badgeListResDtos1 = badgeService.checkAddOne(ploggingUser, timeRecord, distance, trashCount);
        List<BadgeListResDto> joined = Stream.concat(badgeListResDtos1.stream(), badgeListResDtos.stream())
                .collect(Collectors.toList());
        return joined;
    }

    @Transactional
    public void deletePlogging(Long ploggingId, User user) {
        Plogging plogging = getPloggingById(ploggingId);
        if(plogging.isrunnedBy(user)) {
            ploggingRepository.delete(plogging);
        } else {
            throw new CustomErrorException("잘못된 사용자 입니다.");
        }
    }




    public Plogging getPloggingById(Long ploggingId) {
        return ploggingRepository.findById(ploggingId).orElseThrow(
                () -> new CustomErrorException("플로깅을 찾을 수 없습니다.")
        );
    }

    public PloggingInfoDto getDetailPloggingById(Long ploggingId) {
        Plogging plogging = getPloggingById(ploggingId);
        PloggingInfoDto ploggingInfoDto = new PloggingInfoDto(plogging);
        return ploggingInfoDto;
    }

    // 내 플로깅 리스트
    @Transactional(readOnly = true)
    public List<PloggingInfoDto> getMyPloggingListByUser(User user) {
        Long userId = user.getId();
        User myUser = userRepository.findById(userId).orElseThrow(
                () -> new CustomErrorException("유저 정보가 없습니다.")
        );
        List<Plogging> myPloggingList = myUser.getPloggings();
        List<PloggingInfoDto> myPloggingListRes = new ArrayList<>();
        for (Plogging plogging : myPloggingList) {
            PloggingInfoDto responseDto = new PloggingInfoDto(plogging);
            myPloggingListRes.add(responseDto);
        }
        return myPloggingListRes;
    }

    // 회원별 플로깅 리스트
    @Transactional(readOnly = true)
    public List<PloggingInfoDto> getPloggingListByUser(Long userId) {
        User myUser = userRepository.findById(userId).orElseThrow(
                () -> new CustomErrorException("유저 정보가 없습니다.")
        );
        List<Plogging> myPloggingList = myUser.getPloggings();
        List<PloggingInfoDto> userPloggingListRes = new ArrayList<>();
        for (Plogging plogging : myPloggingList) {
            PloggingInfoDto responseDto = new PloggingInfoDto(plogging);
            userPloggingListRes.add(responseDto);
        }
        return userPloggingListRes;
    }
}
