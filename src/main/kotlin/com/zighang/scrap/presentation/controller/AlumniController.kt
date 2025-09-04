package com.zighang.scrap.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.response.alumni.AlumniSimiliarJobPostingResponseDto
import com.zighang.scrap.dto.response.alumni.AlumniTop3CompanyResponseDto
import com.zighang.scrap.dto.response.alumni.AlumniTop3JobPostingScrapResponseDto
import com.zighang.scrap.dto.response.alumni.SimilarAlumniResponseDto
import com.zighang.scrap.presentation.swagger.AlumniSwagger
import com.zighang.scrap.service.AlumniService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/alumni")
class AlumniController(
    private val alumniService: AlumniService
) : AlumniSwagger {

    @GetMapping("/similar/job-postings/top3")
    override fun getTop3ScrappedJobPostingsBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) : ResponseEntity<RestResponse<List<AlumniTop3JobPostingScrapResponseDto>>>{

        return ResponseEntity.ok(
            RestResponse<List<AlumniTop3JobPostingScrapResponseDto>>(
                alumniService.getTop3ScrappedJobPostingsBySimilarUsers(
                    customUserDetails
                )
            )
        )
    }

    @GetMapping("/similar/companies/top3")
    override fun getTop3CompanyBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<RestResponse<List<AlumniTop3CompanyResponseDto>>> {

        return ResponseEntity.ok(
            RestResponse<List<AlumniTop3CompanyResponseDto>>(
                alumniService.getTop3ScrappedCompaniesBySimilarUsers(
                    customUserDetails
                )
            )
        )
    }

    @GetMapping("/similar/scraps")
    override fun getScrappedJobPostingsBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam page: Int
    ) : ResponseEntity<RestResponse<PageResponse<AlumniSimiliarJobPostingResponseDto>>> {

        val safePage = if (page < 0) 0 else page

        return ResponseEntity.ok(
            RestResponse<PageResponse<AlumniSimiliarJobPostingResponseDto>>(
                PageResponse.from(
                    alumniService.getScrappedJobPostingsBySimilarUsers(
                        customUserDetails, safePage
                    )
                )
            )
        )
    }

    @GetMapping("/similar/info")
    override fun getAlumniBySimilarUsersController(
        customUserDetails: CustomUserDetails
    ): ResponseEntity<RestResponse<List<SimilarAlumniResponseDto>>> {

        return ResponseEntity.ok(
            RestResponse<List<SimilarAlumniResponseDto>>(
                alumniService.getAlumniBySimilarUsers(customUserDetails)
            )
        )
    }

}