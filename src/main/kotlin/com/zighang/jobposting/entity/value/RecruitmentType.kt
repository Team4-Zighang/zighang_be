package com.zighang.jobposting.entity.value

enum class RecruitmentType(val displayName: String) {
    FULL_TIME("정규직"),
    CONTRACT("계약직"),
    CONVERTIBLE_INTERN("전환형 인턴"),
    DAY_WORKER("일용직"),
    EXPERIENTIAL_INTERN("체험형 인턴"),
    ALTERNATIVE_MILITARY_SERVICE("대체복무"),
    FREELANCER("프리랜서");
}