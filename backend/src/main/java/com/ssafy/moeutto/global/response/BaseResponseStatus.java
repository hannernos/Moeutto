package com.ssafy.moeutto.global.response;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

    // -------- 성공 코드 시작 -------- //
    SUCCESS(true, 1000, "요청에 성공했습니다."),
    // -------- 성공 코드 종료 -------- //

    // -------- 실패 코드 시작 -------- //
    /**
     * Member
     * Code : 2000번대
     */
    // ex) NOT_FOUND_MEMBER(false, 2001, "일치하는 사용자가 없습니다."), ...

    /**
     * Clothes
     * Code : 3000번대
     */


    /**
     * Calendar
     * Code : 4000번대
     */


    /**
     * Category
     * Code : 5000번대
     */
    NOT_FOUNT_LARGE_CATEGORY(false, 5001, "대분류 카테고리가 존재하지 않습니다.");

    /**
     * AICheckOutfit
     * Code : 6000번대
     */

    // -------- 필요한 에러 코드 추가 => Code 만들 때 안겹치게 몇번대 사용할 건지 얘기할 것  -------- //

    // -------- 실패 코드 종료 -------- //

    private boolean isSuccess; // 성공 여부
    private String message; // 메시지
    private int code; // 코드

    /**
     * BaseResponseStatus 에서 해당하는 코드를 매핑
     *
     * @param isSuccess
     * @param code
     * @param message
     */
    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}