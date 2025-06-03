package com.team03.monew.service;

import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import com.team03.monew.dto.interest.InterestDto;
import com.team03.monew.dto.interest.InterestRegisterRequest;
import com.team03.monew.dto.interest.mapper.InterestMapper;
import com.team03.monew.entity.Interest;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.util.SimilarityUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;

  public InterestDto registerInterest(InterestRegisterRequest request) {
    String newName = request.name();

    // 1. 기존 이름들 가져오기
    List<String> existingNames = interestRepository.findAllNames();

    // 2. 유사도 비교 (80% 이상)
    for (String existingName : existingNames) {
      double similarity = SimilarityUtil.calculateSimilarity(newName, existingName);
      if (similarity >= 0.8) {
        throw new CustomException(ErrorCode.CONFLICT,
            new ErrorDetail("String","newName","existingName"),
            ExceptionType.INTEREST
        );
      }
    }

    // 3. 저장 로직
    Interest interest = Interest.builder()
        .name(newName)
        .keywords(request.keywords())
        .build();

    interestRepository.save(interest);

    return InterestMapper.toDto(interest);
  }

  @Transactional
  public void updateKeywords(Long interestId, List<String> newKeywords) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, new ErrorDetail(
            "interest","interesdId","interest"
        ), ExceptionType.INTEREST));

    interest.getKeywords().clear();
    interest.getKeywords().addAll(newKeywords);

    // 변경 감지로 자동 저장 (save 호출 안 해도 됨)
    interestRepository.save(interest); // 선택적
  }
  public Optional<CursorPageResponseInterestDto>  searchInterests(
      Long userId,
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {
    return Optional.of(interestRepository.searchInterests(userId, keyword, orderBy, direction, cursor, after, limit)) ;
  }
}
