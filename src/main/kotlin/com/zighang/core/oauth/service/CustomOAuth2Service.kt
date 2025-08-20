package com.zighang.core.oauth.service

import com.zighang.core.oauth.dto.KaKaoResponse
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2Service(
    // TODO : MemberRepository 채워넣기
) : DefaultOAuth2UserService() {

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest) : OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        val registrationId = oAuth2UserRequest.clientRegistration.registrationId
        val oAuth2UserResponse = when (registrationId) {
            "kakao" -> KaKaoResponse(oAuth2User.attributes)
            else -> throw OAuth2ErrorCode.OAUTH2_PROVIDER_ERROR.toException()
        }

        // TODO : 이메일로 유저 찾아서 없으면 가입시키고, 있으면 로그인 진행
        return oAuth2User
    }
}