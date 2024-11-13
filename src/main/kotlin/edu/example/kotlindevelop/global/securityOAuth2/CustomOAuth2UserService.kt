package edu.example.kotlindevelop.global.securityOAuth2

import edu.example.kotlindevelop.domain.member.dto.MemberDTO.CustomOAuth2User
import edu.example.kotlindevelop.domain.member.dto.MemberDTO.NaverResponse
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import edu.example.kotlindevelop.domain.member.util.EncoderUtil
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service


@Service
class CustomOAuth2UserService(
    private val memberRepository: MemberRepository,
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User = super.loadUser(userRequest)
        println(oAuth2User.attributes)

        val oAuth2Response = NaverResponse(oAuth2User.attributes["response"] as Map<String, Any>)

        val role = "ROLE_USER"
        val existData  = memberRepository.findByLoginId(oAuth2Response.providerId);
        if (existData.isEmpty) {
            val userEntity = Member(
                loginId = oAuth2Response.providerId,
                name = oAuth2Response.nickname,
                email = oAuth2Response.email,
                mImage = oAuth2Response.profileImage,
                userName = "naver",
                pw = ""
            )
            userEntity.pw = EncoderUtil.encode("123")
            memberRepository.save(userEntity)
        } else {
            existData.get().apply {
                name = oAuth2Response.nickname
                email = oAuth2Response.email
                mImage = oAuth2Response.profileImage
            }
            memberRepository.save(existData.get())
        }
        return CustomOAuth2User(oAuth2Response, role)
    }
}