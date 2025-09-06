package com.zighang.scrap.util

class SystemMessageFactory {


    companion object{
        private val jobAnalysisSystemMessage: String = """
            유저가 보내주는 공고 요약을 보고 자격요건, 우대사항, 경력, 채용유형, 학력조건을 분석해주세요.
            **반드시 아래 규칙을 지켜주세요:**
            
            1. 출력은 **JSON 형식**으로 하고, **다른 키를 만들지 마세요.**
            2. JSON 키는 반드시 영어로:
               - 자격요건 → "qualification"
               - 우대사항 → "preferentialTreatment"
               - 경력 → "career"
            3. "qualification"과 "preferentialTreatment"는 반드시 **줄글 형식**, '•'로 시작, **구어체 존댓말(-합니다, -해요)** 사용
            4. "career"는 `1년차`, `4~10년차` 와 같은 형식으로 작성
            5. 공고에 나온 **모든 내용**(부가 조건, 수습기간, 급여, 보험 등)을 기존 키 안에 포함
            6. 절대 배열(`[]`) 형태로 만들지 마세요. 모든 내용은 하나의 문자열 안에 줄바꿈 `\n`으로 구분
            7. 공고 내용이 분석 불가 시:
               - "qualification"과 "preferentialTreatment"에는 `"해당 공고에 대한 자격요건/우대사항을 분석할 수 없습니다."`
               - "career"에는 `"null"`
            
            아래 형식에 맞춰 오직 JSON만 출력해주세요.  
            설명, 마크다운(````json 포함), 주석 없이 JSON 본문만 출력하고  
            모든 key는 아래 형식과 정확히 일치해야 하며, 대소문자나 철자를 절대 변경하지 마세요.
            
            [예시 출력]
            {
              "qualification": "• B2B 서비스 기획 경험이 있으시면 좋습니다.\n• 수습기간, 급여 조건, 4대 보험, 퇴직금도 포함해 주세요.",
              "preferentialTreatment": "• Jira/Confluence 등 협업 도구 경험이 있으면 우대합니다.\n• 관련 자격증이 있으시면 우대합니다.",
              "career": "4~10년차"
            }

        """.trimIndent()

        fun jobAnalysisSystemMessageFactory(): String {
            return jobAnalysisSystemMessage
        }
    }
}