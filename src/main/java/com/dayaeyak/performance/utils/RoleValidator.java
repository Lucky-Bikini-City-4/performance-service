package com.dayaeyak.performance.utils;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.common.exception.GlobalErrorCode;

public class RoleValidator {

    private RoleValidator() {} // 인스턴스화 방지

    /* 권한 확인 메서드 (MASTER)*/
    private static void validateMaster(String roles) {
        if (roles == null || !roles.contains("MASTER")) {
            throw new CustomException(GlobalErrorCode.ACCESS_DENIED);
        }
    }

    /* 권한 확인 메서드 (MASTER)*/
    private static void validateMasterOrSeller(String roles) {
        if (roles == null || !(roles.contains("MASTER") || roles.contains("SELLER"))) {
            throw new CustomException(GlobalErrorCode.ACCESS_DENIED);
        }
    }
}
