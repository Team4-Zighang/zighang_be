package com.zighang.core.oauth.service

import com.zighang.core.oauth.CustomOAuth2User
import com.zighang.core.oauth.dto.KaKaoResponse
import com.zighang.core.oauth.dto.UserDto
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    // TODO : MemberRepository 채워넣기
) : DefaultOAuth2UserService() {

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest) : CustomOAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        val registrationId = oAuth2UserRequest.clientRegistration.registrationId
        val oAuth2UserResponse = when (registrationId) {
            "kakao" -> KaKaoResponse(oAuth2User.attributes)
            else -> throw OAuth2ErrorCode.OAUTH2_PROVIDER_ERROR.toException()
        }

        // TODO : 이메일로 유저 찾아서 없으면 가입시키고, 있으면 로그인 진행
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

        return CustomOAuth2User(userDto)
    }
}