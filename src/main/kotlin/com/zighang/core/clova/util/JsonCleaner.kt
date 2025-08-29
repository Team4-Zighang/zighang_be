package com.zighang.core.clova.util

object JsonCleaner {
    fun cleanJson(rawOutput: String): String {
        // 1. ```json 또는 ``` 제거
        var cleaned = rawOutput.replace("(?m)^```json|^```".toRegex(), "").trim { it <= ' ' }

        // 2. 한 줄 주석 제거 (// ...)
        cleaned = cleaned.replace("(?m)//.*?$".toRegex(), "")

        // 3. 블록 주석 제거 (/* ... */)
        cleaned = cleaned.replace("/\\*(?s).*?\\*/".toRegex(), "")

        return cleaned
    }
}