package kotlindevelop.member.repository

import kotlindevelop.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByLoginId(loginId: String): Optional<Member>

    fun findByRefreshToken(refreshToken: String): Optional<Member>

    fun findByEmail(email: String): Optional<Member>
    fun findByLoginIdAndEmail(loginId: String, email: String): Optional<Member>
}