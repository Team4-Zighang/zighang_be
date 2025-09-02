package com.zighang.member.entity.value

enum class School(
    val schoolName: String,
) {
    SEOUL("서울대학교"),
    YEONSEI("연세대학교"),
    KOREA("고려대학교"),

    SOGANG("서강대학교"),
    SUNGKYUNKWAN("성균관대학교"),
    HANYANG("한양대학교"),
    EWHA("이화여자대학교"),

    CHUNGANG("중앙대학교"),
    KYUNGHEE("경희대학교"),
    HUFS("한국외국어대학교"),
    UNIVERSITY_OF_SEOUL("서울시립대학교"),

    KONKUK("건국대학교"),
    DONGGUK("동국대학교"),
    HONGIK("홍익대학교"),
    SOOKMYUNG("숙명여자대학교"),

    KOOKMIN("국민대학교"),
    SOONGSIL("숭실대학교"),
    SEJONG("세종대학교"),
    DANKOOK("단국대학교"),
    HANYANG_ERICA("한양대학교 에리카캠퍼스"),

    KWANGWOON("광운대학교"),
    MYEONGJI("명지대학교"),
    SANGMYUNG("상명대학교"),
    CATHOLIC("가톨릭대학교"),
    SUNGSHIN("성신여자대학교"),

    INCHEON("인천대학교"),
    GACHON("가천대학교"),
    KYONGGI("경기대학교")
    ;

    companion object{

        val allSchoolNames : List<String>
            get() = entries.map { it.schoolName }

        fun fromSchoolName(schoolName: String): School {
            return entries.first { it.schoolName == schoolName }
        }
    }
}