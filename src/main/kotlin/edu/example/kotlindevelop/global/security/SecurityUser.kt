package edu.example.kotlindevelop.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class SecurityUser(
    val id: Long,  // 사용자 ID
    loginId: String,  // 사용자 로그인 ID
    password: String,  // 비밀번호
    authorities: Collection<GrantedAuthority>  // 권한 목록
) : User(loginId, password, authorities) {
    val authoritiesSet: Set<GrantedAuthority> = authorities.toSet()  // 권한 목록을 Set으로 변환하여 저장
}
