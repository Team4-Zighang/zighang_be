package com.zighang.core.oauth.service

import com.zighang.core.oauth.CustomOAuth2User
import com.zighang.core.oauth.dto.KaKaoResponse
import com.zighang.core.oauth.dto.UserDto
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import com.zighang.member.entity.Member
import com.zighang.member.repository.MemberRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val memberRepository: MemberRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest) : CustomOAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        val registrationId = oAuth2UserRequest.clientRegistration.registrationId
        val oAuth2UserResponse = when (registrationId) {
            "kakao" -> KaKaoResponse(oAuth2User.attributes)
            else -> throw OAuth2ErrorCode.OAUTH2_PROVIDER_ERROR.toException()
        }

        val email = oAuth2UserResponse.getEmail()
        val name = oAuth2UserResponse.getName()
        val profileImage = oAuth2UserResponse.getProfileImage()
        val username = oAuth2UserResponse.getProviderId()

        val userDto = UserDto(
            registrationId = registrationId,
            name = name,
            username = username,
            email = email,
            profileImage = profileImage,
            userId = username.toLong()
        )

        val existingMember = memberRepository.findByEmail(email)

        if(existingMember != null) {
            return CustomOAuth2User(userDto)
        } else {
            val newMember = Member.create(
                name, email, profileImage
            )
            memberRepository.save(newMember)
        }

        return CustomOAuth2User(userDto)
    }
}