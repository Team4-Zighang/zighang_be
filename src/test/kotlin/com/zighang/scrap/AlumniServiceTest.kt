// src/test/kotlin/com/zighang/scrap/service/AlumniServiceTest.kt

import com.zighang.core.exception.DomainException
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.Company
import com.zighang.jobposting.infrastructure.mapper.CompanyMapper
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.entity.value.CareerYear
import com.zighang.member.entity.value.Role
import com.zighang.member.entity.value.School
import com.zighang.member.entity.JobRole as JobRoleEntity
import com.zighang.member.exception.OnboardingErrorCode
import com.zighang.member.repository.JobRoleRepository
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import com.zighang.scrap.entity.Scrap
import com.zighang.scrap.repository.ScrapRepository
import com.zighang.scrap.service.AlumniService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.util.ReflectionTestUtils

class AlumniServiceTest : BehaviorSpec({

    val memberRepository = mockk<MemberRepository>()
    val onboardingRepository = mockk<OnboardingRepository>()
    val jobPostingRepository = mockk<JobPostingRepository>()
    val companyMapper = mockk<CompanyMapper>()
    val scrapRepository = mockk<ScrapRepository>()
    val jobRoleRepository = mockk<JobRoleRepository>()

    val alumniService = AlumniService(
        memberRepository,
        onboardingRepository,
        jobPostingRepository,
        companyMapper,
        scrapRepository,
        jobRoleRepository
    )

    // @Value 필드인 cloudfrontUrl을 수동으로 주입합니다.
    beforeSpec {
        ReflectionTestUtils.setField(alumniService, "cloudfrontUrl", "https://test.cloudfront.net/")
    }

    afterTest {
        clearAllMocks()
    }

    Given("로그인한 사용자와 여러 동문 데이터가 준비되었을 때") {
        val currentUserDetails = mockk<CustomUserDetails>()
        val currentUserId = 1L
        val currentUserOnboardingId = 10L

        val currentUser = Member(id = currentUserId, name = "나", email = "me@test.com", onboardingId = currentUserOnboardingId, profileImageUrl = "http://", role = Role.MEMBER)
        val currentUserOnboarding = Onboarding(id = currentUserOnboardingId, school = School.SEOUL, major = "컴퓨터공학과", jobCategory = "IT", careerYear = CareerYear.YEAR_0)
        val currentUserJobRoles = listOf(JobRoleEntity(onboardingId = currentUserOnboardingId, jobRole = "백엔드 개발자"))

        // 유사 동문 1 (모든 조건 만족)
        val similarAlumni1 = Member(id = 2L, name = "김동문", email = "kim@test.com", onboardingId = 11L, profileImageUrl = "http://", role = Role.MEMBER)
        val similarOnboarding1 = Onboarding(id = 11L, school = School.SEOUL, major = "컴퓨터공학과", jobCategory = "IT", careerYear = CareerYear.YEAR_0)
        val similarAlumni1JobRoles = listOf(JobRoleEntity(onboardingId = 11L, jobRole = "백엔드 개발자"))
        val similarAlumni1Scraps = (1..5).map { Scrap(memberId = 2L, jobPostingId = it.toLong(), resumeUrl = null, portfolioUrl = null) } // 5개 스크랩

        // 유사 동문 2 (스크랩 4개 미만으로 필터링될 대상)
        val similarAlumni2 = Member(id = 3L, name = "박동문", email = "park@test.com", onboardingId = 12L, profileImageUrl = "http://", role = Role.MEMBER)
        val similarOnboarding2 = Onboarding(id = 12L, school = School.SEOUL, major = "소프트웨어학과", jobCategory = "IT", careerYear = CareerYear.YEAR_0)
        val similarAlumni2Scraps = (1..3).map { Scrap(memberId = 3L, jobPostingId = (10 + it).toLong(), resumeUrl = null, portfolioUrl = null) } // 3개 스크랩

        // 스크랩된 공고 데이터
        val jobPostings = (1..5).map {
            mockk<JobPosting>().apply {
                every { id } returns it.toLong()
                every { company } returns """{"companyName":"회사${it}","companyImageUrl":null}"""
            }
        }
        val companyDtos = (1..5).map { Company("회사$it", null) }

        // Mock 설정: 각 Repository가 호출될 때 반환할 값을 지정합니다.
        every { currentUserDetails.getId() } returns currentUserId
        every { memberRepository.findByIdOrNull(currentUserId) } returns currentUser
        every { onboardingRepository.findByIdOrNull(currentUserOnboardingId) } returns currentUserOnboarding
        every { jobRoleRepository.findByOnboardingId(currentUserOnboardingId) } returns currentUserJobRoles

        // 유사 동문 조회 로직 Mocking
        every {
            onboardingRepository.findBySchoolAndJobRoleIn(School.SEOUL, listOf("백엔드 개발자"))
        } returns listOf(currentUserOnboarding, similarOnboarding1, similarOnboarding2) // 나 자신도 포함

        every {
            memberRepository.findByOnboardingIdIn(listOf(11L, 12L)) // 자기 자신(10L)은 필터링됨
        } returns listOf(similarAlumni1, similarAlumni2)

        When("스크랩을 4개 이상 한 유사 동문 목록을 조회하면") {
            // Mock 설정 (When 블록에 특화된 설정)
            every { scrapRepository.findMemberIdsWithMoreThanFourScraps(listOf(2L, 3L)) } returns listOf(2L) // 김동문(id=2)만 필터링
            every { memberRepository.findAllById(listOf(2L)) } returns listOf(similarAlumni1)
            every { onboardingRepository.findAllById(listOf(11L)) } returns listOf(similarOnboarding1)
            every { scrapRepository.findByMemberIdIn(listOf(2L)) } returns similarAlumni1Scraps
            every { jobPostingRepository.findAllById(setOf(1L, 2L, 3L, 4L, 5L)) } returns jobPostings
            every { jobRoleRepository.findByOnboardingId(11L) } returns similarAlumni1JobRoles // N+1 쿼리 Mock

            every { companyMapper.toJsonDto(any()) } returnsMany companyDtos

            val result = alumniService.getAlumniBySimilarUsers(currentUserDetails)

            Then("조건을 만족하는 동문 1명의 정보가 반환되어야 한다.") {
                result shouldHaveSize 1
                result[0].memberId shouldBe 2L
                result[0].memberName shouldBe "김동문"
                result[0].school shouldBe "서울대학교"
                result[0].major shouldBe "컴퓨터공학과"
                result[0].jobRole shouldBe listOf("백엔드 개발자")
                result[0].companyLists shouldHaveSize 4
                result[0].companyLists[0].companyName shouldBe "회사1"

                // 주요 메소드들이 정확한 인자와 함께 호출되었는지 검증
                verify(exactly = 1) { scrapRepository.findMemberIdsWithMoreThanFourScraps(listOf(2L, 3L)) }
                verify(exactly = 1) { jobRoleRepository.findByOnboardingId(11L) }
            }
        }

        When("유사 동문은 있지만 아무도 스크랩을 4개 이상 하지 않았다면") {
            every { scrapRepository.findMemberIdsWithMoreThanFourScraps(listOf(2L, 3L)) } returns emptyList()

            val result = alumniService.getAlumniBySimilarUsers(currentUserDetails)

            Then("결과는 빈 리스트여야 한다.") {
                result shouldHaveSize 0
            }
        }
    }

    Given("로그인한 사용자의 온보딩 정보가 없을 때") {
        val currentUserDetails = mockk<CustomUserDetails>()
        val currentUserId = 1L
        val currentUser = Member(id = currentUserId, name = "나", email = "me@test.com", onboardingId = null, profileImageUrl = null, role = Role.MEMBER) // onboardingId가 null

        every { currentUserDetails.getId() } returns currentUserId
        every { memberRepository.findByIdOrNull(currentUserId) } returns currentUser
        // onboardingRepository.findByIdOrNull 은 호출되지 않거나 null을 반환해야 함

        When("동문 목록 조회를 시도하면") {
            Then("OnboardingErrorCode.NOT_EXIST_ONBOARDING 예외가 발생해야 한다.") {
                val exception = shouldThrow<DomainException> {
                    alumniService.getAlumniBySimilarUsers(currentUserDetails)
                }
                exception.message shouldBe OnboardingErrorCode.NOT_EXIST_ONBOARDING.message
            }
        }
    }
})