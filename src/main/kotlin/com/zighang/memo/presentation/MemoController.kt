package com.zighang.memo.presentation

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.memo.dto.request.MemoCreateRequest
import com.zighang.memo.dto.response.MemoCreateResponse
import com.zighang.memo.presentation.swagger.MemoSwagger
import com.zighang.memo.service.MemoService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/memo")
@Tag(name = "Memo", description = "공고 옆 메모 관련 API")
class MemoController(
    private val memoService: MemoService,
) : MemoSwagger {

    @PostMapping("/save")
    override fun saveMemo(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody memoCreateRequest: MemoCreateRequest,
    ) : ResponseEntity<RestResponse<MemoCreateResponse>>{
      return ResponseEntity.ok(
          RestResponse<MemoCreateResponse>(
              memoService.saveMemo(customUserDetails, memoCreateRequest)
          )
      )
    }

    @GetMapping("/{postingId}")
    override fun getMemo(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable postingId: Long,
    ): ResponseEntity<RestResponse<String?>> {
        return ResponseEntity.ok(
            RestResponse<String?> (
                memoService.getMemo(customUserDetails, postingId)
            )
        )
    }
}