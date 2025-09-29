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

        val similarAlumni1 = Member(id = 2L, name = "김동문", email = "kim@test.com", onboardingId = 11L, profileImageUrl = "http://", role = Role.MEMBER)
        val similarOnboarding1 = Onboarding(id = 11L, school = School.SEOUL, major = "컴퓨터공학과", jobCategory = "IT", careerYear = CareerYear.YEAR_0)
        val similarAlumni1JobRoles = listOf(JobRoleEntity(onboardingId = 11L, jobRole = "백엔드 개발자"))

        val similarAlumni1Top4Scraps = (1..4).map { Scrap(memberId = 2L, jobPostingId = it.toLong(), resumeUrl = null, portfolioUrl = null) }

        val similarAlumni2 = Member(id = 3L, name = "박동문", email = "park@test.com", onboardingId = 12L, profileImageUrl = "http://", role = Role.MEMBER)
        val similarOnboarding2 = Onboarding(id = 12L, school = School.SEOUL, major = "소프트웨어학과", jobCategory = "IT", careerYear = CareerYear.YEAR_0)

        val jobPostings = (1..4).map {
            mockk<JobPosting>().apply {
                every { id } returns it.toLong()
                every { company } returns """{"companyName":"회사${it}","companyImageUrl":"http://www.cloudfront.com/image${it}.png"}"""
            }
        }
        val companyDtos = (1..4).map { Company("회사$it", "http://www.cloudfront.com/image$it.png") }

        every { currentUserDetails.getId() } returns currentUserId
        every { memberRepository.findByIdOrNull(currentUserId) } returns currentUser
        every { onboardingRepository.findByIdOrNull(currentUserOnboardingId) } returns currentUserOnboarding
        every { jobRoleRepository.findByOnboardingId(currentUserOnboardingId) } returns currentUserJobRoles

        every {
            onboardingRepository.findBySchoolAndJobRoleIn(School.SEOUL, listOf("백엔드 개발자"))
        } returns listOf(currentUserOnboarding, similarOnboarding1, similarOnboarding2)

        every {
            memberRepository.findByOnboardingIdIn(listOf(11L, 12L))
        } returns listOf(similarAlumni1, similarAlumni2)

        When("스크랩을 4개 이상 한 유사 동문 목록을 조회하면") {
            every { scrapRepository.findMemberIdsWithMoreThanFourScraps(listOf(2L, 3L)) } returns listOf(2L)
            every { memberRepository.findAllById(listOf(2L)) } returns listOf(similarAlumni1)
            every { onboardingRepository.findAllById(listOf(11L)) } returns listOf(similarOnboarding1)

            every { jobRoleRepository.findByOnboardingIdIn(listOf(11L)) } returns similarAlumni1JobRoles

            every { scrapRepository.findTopNScrapsPerMember(listOf(2L), 4) } returns similarAlumni1Top4Scraps

            every { jobPostingRepository.findAllById(setOf(1L, 2L, 3L, 4L)) } returns jobPostings

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
                result[0].companyLists[3].companyName shouldBe "회사4"

                // 주요 메소드들이 정확한 인자와 함께 호출되었는지 검증
                verify(exactly = 1) { scrapRepository.findMemberIdsWithMoreThanFourScraps(listOf(2L, 3L)) }
                verify(exactly = 1) { scrapRepository.findTopNScrapsPerMember(listOf(2L), 4) }
                verify(exactly = 1) { jobRoleRepository.findByOnboardingIdIn(listOf(11L)) }
                verify {
                    companyMapper.toJsonDto("""{"companyName":"회사1","companyImageUrl":"http://www.cloudfront.com/image1.png"}""")
                    companyMapper.toJsonDto("""{"companyName":"회사2","companyImageUrl":"http://www.cloudfront.com/image2.png"}""")
                    companyMapper.toJsonDto("""{"companyName":"회사3","companyImageUrl":"http://www.cloudfront.com/image3.png"}""")
                    companyMapper.toJsonDto("""{"companyName":"회사4","companyImageUrl":"http://www.cloudfront.com/image4.png"}""")
                }
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
        val currentUser = Member(id = currentUserId, name = "나", email = "me@test.com", onboardingId = null, profileImageUrl = null, role = Role.MEMBER)

        every { currentUserDetails.getId() } returns currentUserId
        every { memberRepository.findByIdOrNull(currentUserId) } returns currentUser

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