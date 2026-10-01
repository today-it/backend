package com.todayit.common.pagination;

/** 페이지 요청값의 공통 조건을 검증합니다. */
public final class PaginationValidator {

  private PaginationValidator() {}

  /**
   * 페이지 요청값이 유효한지 확인합니다.
   *
   * @param page 0 이상이어야 하는 페이지 번호
   * @param size 1 이상이어야 하는 페이지 크기
   * @return 페이지 번호와 크기가 유효하면 true
   */
  public static boolean isValid(int page, int size) {
    return page >= 0 && size > 0;
  }
}
