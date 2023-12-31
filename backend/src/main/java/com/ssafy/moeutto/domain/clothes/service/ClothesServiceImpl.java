package com.ssafy.moeutto.domain.clothes.service;

import com.ssafy.moeutto.domain.S3.dto.response.S3ResponseDto;
import com.ssafy.moeutto.domain.S3.service.S3Service;
import com.ssafy.moeutto.domain.clothes.dto.request.ClothesListByFriendsRequestDto;
import com.ssafy.moeutto.domain.clothes.dto.request.ClothesListRequestDto;
import com.ssafy.moeutto.domain.clothes.dto.request.ClothesRegistRequestDto;
import com.ssafy.moeutto.domain.clothes.dto.request.ClothesUpdateRequestDto;
import com.ssafy.moeutto.domain.clothes.dto.response.*;
import com.ssafy.moeutto.domain.clothes.entity.*;
import com.ssafy.moeutto.domain.clothes.repository.ClothesRepository;
import com.ssafy.moeutto.domain.guestBook.dto.response.GuestBookListResponseDto;
import com.ssafy.moeutto.domain.guestBook.service.GuestBookService;
import com.ssafy.moeutto.domain.largeCategory.repository.LargeCategoryRepository;
import com.ssafy.moeutto.domain.member.entity.Member;
import com.ssafy.moeutto.domain.member.repository.MemberRepository;
import com.ssafy.moeutto.domain.middleCategory.entity.MiddleCategory;
import com.ssafy.moeutto.domain.middleCategory.repository.MiddleCategoryRepository;
import com.ssafy.moeutto.global.response.BaseException;
import com.ssafy.moeutto.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    private final ClothesRepository clothesRepository;
    private final MemberRepository memberRepository;
    private final MiddleCategoryRepository middleCategoryRepository;
    private final LargeCategoryRepository largeCategoryRepository;
    private final S3Service s3Service;
    private final GuestBookService guestBookService;

    /**
     * 옷 정보를 등록합니다.
     *
     * @param clothesRegistRequestDto - 옷 등록 정보
     * @param memberId                - 사용자 UUID
     * @param token                   - accessToken
     * @param file                    - 옷 이미지
     * @return ClothesRegistResponseDto - 등록된 옷 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesRegistResponseDto registClothes(ClothesRegistRequestDto clothesRegistRequestDto, UUID memberId, String token, MultipartFile file) throws BaseException {
        // 사용자 체크
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 중분류 카테고리 체크
        MiddleCategory middleCategory = middleCategoryRepository.findById(clothesRegistRequestDto.getMiddleCategoryId()).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MIDDLE_CATEGORY));

        // 옷 사진 S3에 등록
        S3ResponseDto s3ResponseDto;
        try {
            s3ResponseDto = s3Service.uploadImage(token, file);
        } catch (IOException e) {
            throw new BaseException(BaseResponseStatus.S3_FILE_IO_ERROR);
        }

        // 옷 정보 저장
        Clothes clothes = Clothes.builder()
                .member(member)
                .middleCategory(middleCategory)
                .name(clothesRegistRequestDto.getName())
                .season(clothesRegistRequestDto.getSeason())
                .color(clothesRegistRequestDto.getColor())
                .thickness(clothesRegistRequestDto.getThickness())
                .price(clothesRegistRequestDto.getPrice())
                .shop(clothesRegistRequestDto.getShop())
                .textile(clothesRegistRequestDto.getTextile())
                .frequency(0)
                .star(0)
                .imageUrl(s3ResponseDto.getAccessUrl())
                .regDate(new Date(System.currentTimeMillis()))
                .recentDate(new Date(System.currentTimeMillis()))
                .build();

        Clothes newClothes = clothesRepository.save(clothes);

        // 등록된 옷 정보 반환
        Clothes checkClothes = clothesRepository.findById(newClothes.getId()).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES));
        return ClothesRegistResponseDto.builder()
                .clothes(checkClothes)
                .build();
    }

    /**
     * 옷 정보를 조회합니다.
     *
     * @param id       - 옷 정보 id
     * @param memberId - 사용자 UUID
     * @return ClothesDetailResponseDto - 옷 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesDetailResponseDto detailClothes(Integer id, UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 옷 정보 체크
        Clothes clothes = clothesRepository.findByIdAndMemberId(id, memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES));

        // 조회한 옷 정보 반환
        return ClothesDetailResponseDto.builder()
                .id(clothes.getId())
                .middleCategoryId(clothes.getMiddleCategory().getId())
                .largeCategoryId(clothes.getMiddleCategory().getLargeCategory().getId())
                .name(clothes.getName())
                .season(clothes.getSeason())
                .color(clothes.getColor())
                .thickness(clothes.getThickness())
                .price(clothes.getPrice())
                .shop(clothes.getShop())
                .textile(clothes.getTextile())
                .frequency(clothes.getFrequency())
                .star(clothes.getStar())
                .imageUrl(clothes.getImageUrl())
                .recentDate(clothes.getRecentDate())
                .build();
    }

    /**
     * 옷 목록을 조회합니다.
     *
     * @param memberId              - 사용자 UUID
     * @param clothesListRequestDto - 옷 정렬 기준 정보
     * @return List<ClothesListResponseDto> - 옷 목록 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public List<ClothesListResponseDto> listClothes(UUID memberId, ClothesListRequestDto clothesListRequestDto) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        String categoryId = clothesListRequestDto.getCategoryId(); // 카테고리 id
        String largeCategoryId = categoryId.substring(0, 3); // 대분류 카테고리 id
        String middleCategoryId = categoryId.substring(3); // 중분류 카테고리 id
        String sortBy = clothesListRequestDto.getSortBy(); // 정렬 기준
        Integer orderBy = clothesListRequestDto.getOrderBy(); // 정렬 순서

        List<Clothes> clothesList; // 옷 목록 정보

        // 조회 조건에 따른 조건문
        if (largeCategoryId.equals("000")) { // 전체 조회
            clothesList = listClothesAll(memberId, sortBy, orderBy);
        } else if (middleCategoryId.equals("000")) { // 대분류 카테고리 조회
            clothesList = listClothesByLargeCategoryId(memberId, largeCategoryId, sortBy, orderBy);
        } else { // 중분류 카테고리 조회
            clothesList = listClothesByMiddleCategoryId(memberId, categoryId, sortBy, orderBy);
        }

        if (clothesList == null || clothesList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        // 필요한 속성 추출 및 옷 정보 반환
        return getClothesListResponseDto(clothesList);
    }

    /**
     * 옷 목록과 방명록을 가져옵니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesListAndGuestBookResponseDto - 옷 목록 + 방명록 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesListAndGuestBookResponseDto listClothesAndGuestBooks(UUID memberId) throws BaseException {
        // 옷 목록에 필요한 매개변수 => 기본 정렬
        ClothesListRequestDto clothesListRequestDto = ClothesListRequestDto.builder()
                .categoryId("000")
                .sortBy("initial")
                .orderBy(0)
                .build();

        List<ClothesListResponseDto> clothesListResponseDtoList = listClothes(memberId, clothesListRequestDto); // 옷 목록
        List<GuestBookListResponseDto> guestBookListResponseDtoList = guestBookService.listGuestBook(memberId); // 방명록 글 목록

        // 옷 목록 + 방명록 정보 반환
        return ClothesListAndGuestBookResponseDto.builder()
                .clothesListResponseDto(clothesListResponseDtoList)
                .guestBookListResponseDto(guestBookListResponseDtoList)
                .build();
    }

    /**
     * 사용자 id로 목록을 조회합니다.
     *
     * @param memberId - 사용자 UUID
     * @param sortBy   - 정렬 기준
     * @param orderBy  - 오름차순 / 내림차순
     * @return List<Clothes> - 옷 목록 정보
     */
    @Override
    public List<Clothes> listClothesAll(UUID memberId, String sortBy, Integer orderBy) {
        if (sortBy.equals("initial")) { // 기본 정렬
            return clothesRepository.findAllByMemberId(memberId);
        }

        if (sortBy.equals("regDate")) { // 등록일 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdOrderByRegDateAsc(memberId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdOrderByRegDateDesc(memberId);
            }
        }

        if (sortBy.equals("frequency")) { // 빈도 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdOrderByFrequencyAsc(memberId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdOrderByFrequencyDesc(memberId);
            }
        }

        if (sortBy.equals("color")) { // 색상 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdOrderByColorAsc(memberId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdOrderByColorDesc(memberId);
            }
        }

        return null;
    }

    /**
     * 사용자 id와 대분류 카테고리 id로 목록을 조회합니다.
     *
     * @param memberId   - 사용자 UUID
     * @param categoryId - 대분류 카테고리 id
     * @param sortBy     - 정렬 기준
     * @param orderBy    - 오름차순 / 내림차순
     * @return List<Clothes> - 옷 목록 정보
     */
    @Override
    public List<Clothes> listClothesByLargeCategoryId(UUID memberId, String categoryId, String sortBy, Integer orderBy) {
        if (sortBy.equals("initial")) { // 기본 정렬
            return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWith(memberId, categoryId);
        }

        if (sortBy.equals("regDate")) { // 등록일 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWithOrderByRegDateAsc(memberId, categoryId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWithOrderByRegDateDesc(memberId, categoryId);
            }
        }

        if (sortBy.equals("frequency")) { // 빈도 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWithOrderByFrequencyAsc(memberId, categoryId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWithOrderByFrequencyDesc(memberId, categoryId);
            }
        }

        if (sortBy.equals("color")) { // 색상 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWithOrderByColorAsc(memberId, categoryId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdStartingWithOrderByColorDesc(memberId, categoryId);
            }
        }

        return null;
    }

    /**
     * 사용자 id와 중분류 카테고리 id로 목록을 조회합니다.
     *
     * @param memberId   - 사용자 UUID
     * @param categoryId - 중분류 카테고리 id
     * @param sortBy     - 정렬 기준
     * @param orderBy    - 오름차순 / 내림차순
     * @return List<Clothes> - 옷 목록 정보
     */
    @Override
    public List<Clothes> listClothesByMiddleCategoryId(UUID memberId, String categoryId, String sortBy, Integer orderBy) {
        if (sortBy.equals("initial")) { // 기본 정렬
            return clothesRepository.findAllByMemberIdAndMiddleCategoryId(memberId, categoryId);
        }

        if (sortBy.equals("regDate")) { // 등록일 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdOrderByRegDateAsc(memberId, categoryId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdOrderByRegDateDesc(memberId, categoryId);
            }
        }

        if (sortBy.equals("frequency")) { // 빈도 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdOrderByFrequencyAsc(memberId, categoryId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdOrderByFrequencyDesc(memberId, categoryId);
            }
        }

        if (sortBy.equals("color")) { // 색상 순 정렬
            if (orderBy == 0) { // 오름차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdOrderByColorAsc(memberId, categoryId);
            } else if (orderBy == 1) { // 내림차순 정렬
                return clothesRepository.findAllByMemberIdAndMiddleCategoryIdOrderByColorDesc(memberId, categoryId);
            }
        }

        return null;
    }

    /**
     * 조회한 목록에서 필요한 속성을 추출합니다.
     *
     * @param clothesList - 옷 목록 정보
     * @return List<ClothesListResponseDto> - 반환할 옷 목록 정보
     */
    @Override
    public List<ClothesListResponseDto> getClothesListResponseDto(List<Clothes> clothesList) {
        // 옷 목록 정보 반환 (필요한 정보만 추출)
        List<ClothesListResponseDto> clothesListResponseDtoList = new ArrayList<>();

        for (Clothes clothes : clothesList) {
            ClothesListResponseDto clothesListResponseDto = ClothesListResponseDto.builder()
                    .id(clothes.getId())
                    .middleCategoryId(clothes.getMiddleCategory().getId())
                    .largeCategoryId(clothes.getMiddleCategory().getLargeCategory().getId())
                    .color(clothes.getColor())
                    .frequency(clothes.getFrequency())
                    .star(clothes.getStar())
                    .imageUrl(clothes.getImageUrl())
                    .regDate(clothes.getRegDate())
                    .build();

            clothesListResponseDtoList.add(clothesListResponseDto);
        }

        return clothesListResponseDtoList;
    }

    /**
     * 옷 정보를 수정합니다.
     *
     * @param clothesUpdateRequestDto - 옷 수정 정보
     * @param memberId                - 사용자 UUID
     * @param token                   - accessToken
     * @param file                    - 옷 이미지
     * @return ClothesUpdateResponseDto - 수정된 옷 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesUpdateResponseDto updateClothes(ClothesUpdateRequestDto clothesUpdateRequestDto, UUID memberId, String token, MultipartFile file) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        Integer id = clothesUpdateRequestDto.getId(); // 옷 정보 id

        // 옷 정보 체크
        Clothes clothes = clothesRepository.findById(id).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES));

        // 중분류 카테고리 정보 체크
        MiddleCategory middleCategory = middleCategoryRepository.findById(clothesUpdateRequestDto.getMiddleCategoryId()).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MIDDLE_CATEGORY));

        // 옷 사진 S3에 등록
        S3ResponseDto s3ResponseDto;
        try {
            s3ResponseDto = s3Service.uploadImage(token, file);
        } catch (IOException e) {
            throw new BaseException(BaseResponseStatus.S3_FILE_IO_ERROR);
        }

        // 옷 정보 수정
        Clothes newClothes = clothes.toBuilder()
                .middleCategory(middleCategory)
                .name(clothesUpdateRequestDto.getName())
                .season(clothesUpdateRequestDto.getSeason())
                .color(clothesUpdateRequestDto.getColor())
                .thickness(clothesUpdateRequestDto.getThickness())
                .price(clothesUpdateRequestDto.getPrice())
                .shop(clothesUpdateRequestDto.getShop())
                .textile(clothesUpdateRequestDto.getTextile())
                .imageUrl(s3ResponseDto.getAccessUrl())
                .build();

        clothesRepository.save(newClothes);

        // 수정된 옷 정보 반환
        Clothes updateClothes = clothesRepository.findById(id).get();
        return ClothesUpdateResponseDto.builder()
                .clothes(updateClothes)
                .build();
    }

    /**
     * 옷 정보를 삭제합니다.
     *
     * @param id       - 옷 정보 id
     * @param memberId - 사용자 UUID
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public void deleteClothes(Integer id, UUID memberId) throws BaseException {
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER)); // 사용자 체크
        clothesRepository.deleteById(id); // 옷 정보 삭제
    }

    /**
     * 옷 즐겨찾기를 등록 / 삭제합니다.
     *
     * @param id       - 옷 정보 id
     * @param memberId - 사용자 UUID
     * @return ClothesStarResponseDto - 옷 즐겨찾기 여부 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesStarResponseDto starClothes(Integer id, UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 옷 정보 체크
        Clothes clothes = clothesRepository.findById(id).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES));

        // 옷 즐겨찾기 정보 수정 (등록 => 해제, 해제 => 등록)
        Clothes newClothes = clothes.toBuilder()
                .id(clothes.getId())
                .star(clothes.getStar() == 0 ? 1 : 0)
                .build();

        clothesRepository.save(newClothes);

        // 옷 즐겨찾기 정보 반환
        Clothes starClothes = clothesRepository.findById(id).get();
        return ClothesStarResponseDto.builder()
                .id(starClothes.getId())
                .star(starClothes.getStar())
                .build();
    }

    /**
     * 옷장을 색상 기준으로 분석합니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesAnalysisColorResponseDto - 옷 색상 분석 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesAnalysisColorResponseDto analysisColor(UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        List<IClothesAnalysisColor> clothesAnalysisColorMyList = clothesRepository.findByColorMember(memberId); // 내 옷장 분석
        List<IClothesAnalysisColor> clothesAnalysisColorUserList = clothesRepository.findByColor(); // 모든 사용자 옷장 분석

        // 옷 존재 여부 체크
        if (clothesAnalysisColorMyList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        // 색상 분석 정보 반환
        return ClothesAnalysisColorResponseDto.builder()
                .myAnalysisColor(clothesAnalysisColorMyList)
                .userAnalysisColor(clothesAnalysisColorUserList)
                .build();
    }

    /**
     * 옷장을 계절 기준으로 분석합니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesAnalysisSeasonResponseDto - 옷 계절 분석 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesAnalysisSeasonResponseDto analysisSeason(UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 옷 존재 여부 체크
        List<Clothes> clothesList = clothesRepository.findAllByMemberId(memberId);
        if (clothesList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        List<IClothesAnalysisSeason> springClothes = clothesRepository.findBySeasonMember("1", memberId); // 봄 옷 분석
        List<IClothesAnalysisSeason> summerClothes = clothesRepository.findBySeasonMember("2", memberId); // 여름 옷 분석
        List<IClothesAnalysisSeason> autumnClothes = clothesRepository.findBySeasonMember("3", memberId); // 가을 옷 분석
        List<IClothesAnalysisSeason> winterClothes = clothesRepository.findBySeasonMember("4", memberId); // 겨울 옷 분석

        // 계절 분석 정보 반환
        return ClothesAnalysisSeasonResponseDto.builder()
                .springClothes(springClothes)
                .summerClothes(summerClothes)
                .autumnClothes(autumnClothes)
                .winterClothes(winterClothes)
                .build();
    }

    /**
     * 옷장을 빈도 기준으로 분석합니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesAnalysisFrequencyResponseDto - 옷 빈도 분석 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesAnalysisFrequencyResponseDto analysisFrequency(UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        List<IClothesAnalysisFrequency> maxList = clothesRepository.findByFrequencyMax(memberId); // 빈도가 높은 최대 3개 분석
        List<IClothesAnalysisFrequency> minList = clothesRepository.findByFrequencyMin(memberId); // 빈도가 높은 최소 3개 분석

        if (maxList.size() == 0 || minList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        // 빈도 분석 정보 반환
        return ClothesAnalysisFrequencyResponseDto.builder()
                .myMostFrequency(maxList)
                .myLeastFrequency(minList)
                .build();
    }

    /**
     * 옷장을 가격 기준으로 분석합니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesAnalysisCostResponseDto - 옷 가격 분석 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesAnalysisCostResponseDto analysisCost(UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 내 옷장 총 가격
        Integer myTotalCost = clothesRepository.findPriceByMemberId(memberId);

        // 모든 회원의 옷 가격 평균
        Integer avgOfMembers = clothesRepository.findAvgOfPrice();

        // 카데고리별 정보 받아오기
        List<IClothesAnalysisCost> itemList = clothesRepository.findCostOfMyClothesByCategory(memberId);

        // 가격 분석 정보 반환
        return ClothesAnalysisCostResponseDto.builder()
                .myAnalysisCost(itemList)
                .myTotalCost(myTotalCost)
                .userTotalAvgCost(avgOfMembers)
                .build();
    }

    /**
     * 옷장을 미니멀 / 맥시멀 기준으로 분석합니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesAnalysisMinMaxResponseDto - 옷 미니멀 / 맥시멀 분석 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesAnalysisMinMaxResponseDto analysisAmount(UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 옷 존재 여부 체크
        List<Clothes> clothesList = clothesRepository.findAllByMemberId(memberId);
        if (clothesList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        Long myTotalAmount = clothesRepository.countByMemberId(memberId); // 사용자가 소유한 옷 세기

        Long userTotalAmount = clothesRepository.countBy(); // 모든 옷 세기
        Long memberAmount = memberRepository.countBy(); // 사용자 수

        Long userTotalAmountAvg = userTotalAmount / (memberAmount == 0 ? 1 : memberAmount); // 모든 사용자 평균 옷 총 개수

        // 미니멀 / 맥시멀로 분석 정보 반환
        List<IClothesAnalysisAmount> iClothesAnalysisAmountList = clothesRepository.findByMinMaxMember(memberId);
        return ClothesAnalysisMinMaxResponseDto.builder()
                .myTotalAmount(myTotalAmount)
                .myAnalysisAmount(iClothesAnalysisAmountList)
                .userTotalAmountAvg(userTotalAmountAvg)
                .build();
    }

    /**
     * 옷장을 활용도 기준으로 분석합니다.
     *
     * @param memberId - 사용자 UUID
     * @return ClothesAnalysisAvailabilityResponseDto - 옷 활용도 분석 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesAnalysisAvailabilityResponseDto analysisAvailability(UUID memberId) throws BaseException {
        // 사용자 체크
        memberRepository.findById(memberId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 옷 존재 여부 체크
        List<Clothes> clothesList = clothesRepository.findAllByMemberId(memberId);
        if (clothesList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        // 사용자 옷 개수
        Long totalAmount = clothesRepository.countByMemberId(memberId);

        // 최근 n개월 내 한번이라도 입은 옷 찾기 - 일단 임의의 값은 3일로 했음. 만약 ~월부터 ~월까지로 한다면 쿼리문 다시 짜야함.
        Long usedAmount = clothesRepository.findRecentDateForNMonthByMemberId(memberId);

        // 최근 n개월 내 입은 옷 분석 - 대분류 카테고리 별 분석
        List<IClothesAnalysisAvailability> amountList = clothesRepository.findMyAnalysisAmountByMemberId(memberId);

        int max = -1, min = 101;
        String maxLargeCategoryId = "001", minLargeCategoryId = "001";
        for (IClothesAnalysisAvailability iClothesAnalysisAvailability : amountList) {
            if (iClothesAnalysisAvailability.getTotalAmount() == 0) {
                continue;
            }

            if (max < iClothesAnalysisAvailability.getUsedAmount() * 100 / iClothesAnalysisAvailability.getTotalAmount()) {
                max = iClothesAnalysisAvailability.getUsedAmount() * 100 / iClothesAnalysisAvailability.getTotalAmount();
                maxLargeCategoryId = iClothesAnalysisAvailability.getLargeCategoryId();
            }

            if (min > iClothesAnalysisAvailability.getUsedAmount() * 100 / iClothesAnalysisAvailability.getTotalAmount()) {
                min = iClothesAnalysisAvailability.getUsedAmount() * 100 / iClothesAnalysisAvailability.getTotalAmount();
                minLargeCategoryId = iClothesAnalysisAvailability.getLargeCategoryId();
            }
        }

        String maxLargeCategoryName = largeCategoryRepository.findById(maxLargeCategoryId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LARGE_CATEGORY)).getName();
        String minLargeCategoryName = largeCategoryRepository.findById(minLargeCategoryId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LARGE_CATEGORY)).getName();

        return ClothesAnalysisAvailabilityResponseDto.builder()
                .totalAmount(totalAmount)
                .usedAmount(usedAmount)
                .analysisAmountList(amountList)
                .maxLargeCategoryName(maxLargeCategoryName)
                .minLargeCategoryName(minLargeCategoryName)
                .build();
    }

    /**
     * 친구가 소유한 옷 목록을 조회합니다.
     *
     * @param memberId                       - 사용자 UUID
     * @param clothesListByFriendsRequestDto - 친구 이메일 + 옷 목록 정렬 기준 정보
     * @return List<ClothesListByFriendsResponseDto> - 친구가 소유한 옷 목록
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public List<ClothesListResponseDto> getListByFriends(UUID memberId, ClothesListByFriendsRequestDto clothesListByFriendsRequestDto) throws BaseException {
        // 친구 정보 조회
        Member friendInfo = memberRepository.findByEmail(clothesListByFriendsRequestDto.getEmail()).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 친구 옷장 공개 여부 체크
        if (!friendInfo.isClosetFind()) {
            throw new BaseException(BaseResponseStatus.UNDISCLOSED_CLOSET);
        }

        String categoryId = clothesListByFriendsRequestDto.getCategoryId(); // 카테고리 id
        String largeCategoryId = categoryId.substring(0, 3); // 대분류 카테고리 id
        String middleCategoryId = categoryId.substring(3); // 중분류 카테고리 id
        String sortBy = clothesListByFriendsRequestDto.getSortBy(); // 정렬 기준
        Integer orderBy = clothesListByFriendsRequestDto.getOrderBy(); // 정렬 순서

        List<Clothes> clothesList; // 옷 목록 정보

        // 조회 조건에 따른 조건문
        if (largeCategoryId.equals("000")) { // 전체 조회
            clothesList = listClothesAll(friendInfo.getId(), sortBy, orderBy);
        } else if (middleCategoryId.equals("000")) { // 대분류 카테고리 조회
            clothesList = listClothesByLargeCategoryId(friendInfo.getId(), largeCategoryId, sortBy, orderBy);
        } else { // 중분류 카테고리 조회
            clothesList = listClothesByMiddleCategoryId(friendInfo.getId(), categoryId, sortBy, orderBy);
        }

        // 옷장 목록이 없는 경우
        if (clothesList == null || clothesList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES_LIST);
        }

        // 필요한 속성 추출 및 옷 정보 반환
        return getClothesListResponseDto(clothesList);
    }

    /**
     * 친구가 소유한 옷 목록과 방명록을 조회합니다.
     *
     * @param memberId - 사용자 UUID
     * @param email    - 친구 이메일
     * @return ClothesListAndGuestBookResponseDto - 친구 옷 목록 + 방명록 목록 정보
     * @throws BaseException - BaseResponse Error 처리
     */
    @Override
    public ClothesListAndGuestBookResponseDto getListClothesAndGuestBookByFriends(UUID memberId, String email) throws BaseException {
        // 초기 정렬 기준
        ClothesListByFriendsRequestDto clothesListByFriendsRequestDto = ClothesListByFriendsRequestDto.builder()
                .email(email)
                .categoryId("000")
                .sortBy("initial")
                .orderBy(0)
                .build();

        // 친구 정보 조회
        Member friendInfo = memberRepository.findByEmail(email).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // 친구 옷장 공개 여부 체크
        if (!friendInfo.isClosetFind()) {
            throw new BaseException(BaseResponseStatus.UNDISCLOSED_CLOSET);
        }

        List<ClothesListResponseDto> clothesListResponseDtoList = getListByFriends(memberId, clothesListByFriendsRequestDto); // 친구 옷장 목록
        List<GuestBookListResponseDto> guestBookListResponseDtoList = guestBookService.listGuestBook(friendInfo.getId()); // 친구 방명록

        // 친구 옷장 + 방명록 반환
        return ClothesListAndGuestBookResponseDto.builder()
                .clothesListResponseDto(clothesListResponseDtoList)
                .guestBookListResponseDto(guestBookListResponseDtoList)
                .build();
    }
}
