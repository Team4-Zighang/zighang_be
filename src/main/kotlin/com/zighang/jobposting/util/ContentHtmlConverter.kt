package com.zighang.jobposting.util

import com.zighang.jobposting.entity.value.Content
import org.springframework.web.util.HtmlUtils

object ContentHtmlConverter {

    fun toHtml(content: Content): String {
        fun e(s: String?) = HtmlUtils.htmlEscape(s ?: "").takeIf { it.isNotBlank() }

        return buildString {

            e(content.name)?.let {
                append("<h2>직무</h2>")
                append("<p>$it</p>")
            }

            e(content.intro)?.let {
                append("<h2>포지션 상세</h2>")
                append("<p>${it.replace("\n", "<br>")}</p>")
            }

            e(content.mainTasks)?.let {
                append("<h2>주요업무</h2>")
                append("<p>${it.replace("\n", "<br>")}</p>")
            }

            e(content.requirements)?.let {
                append("<h2>자격요건</h2>")
                append("<p>${it.replace("\n", "<br>")}</p>")
            }

            e(content.prefferedPoints)?.let {
                append("<h2>우대사항</h2>")
                append("<p>${it.replace("\n", "<br>")}</p>")
            }

            e(content.benefits)?.let {
                append("<h2>혜택 및 복지</h2>")
                append("<p>${it.replace("\n", "<br>")}</p>")
            }
        }
    }
}
