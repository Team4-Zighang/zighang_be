package com.zighang.core.clova.util

object JsonCleaner {
    fun cleanJson(rawOutput: String): String {
        // 1) 코드펜스 제거 (```json, ``` 등 - 대소문자/공백 허용)
        val withoutFences = rawOutput
            .replace("(?mi)^```\\s*\\w*\\s*$".toRegex(), "")
            .trim()

        // 2) 문자열 리터럴 바깥의 주석만 제거
        val cleaned = stripJsonComments(withoutFences)
        return cleaned.trim()
    }

    private fun stripJsonComments(text: String): String {
        val sb = StringBuilder(text.length)
        var inString = false
        var escape = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (inString) {
                sb.append(c)
                if (escape) {
                    escape = false
                } else {
                    if (c == '\\') escape = true
                    else if (c == '"') inString = false
                }
                i++; continue
            }
            if (c == '"') { inString = true; sb.append(c); i++; continue }
            if (c == '/' && i + 1 < text.length) {
                val n = text[i + 1]
                if (n == '/') { i += 2; while (i < text.length && text[i] != '\n') i++; continue }
                if (n == '*') { i += 2; while (i + 1 < text.length && !(text[i] == '*' && text[i + 1] == '/')) i++; i = minOf(i + 2, text.length); continue }
            }
            sb.append(c); i++
        }
        return sb.toString()
    }
}