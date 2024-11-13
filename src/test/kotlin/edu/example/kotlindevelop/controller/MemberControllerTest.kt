package edu.example.kotlindevelop.controller


import edu.example.kotlindevelop.domain.member.controller.MemberController
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.util.ValidationUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

@SpringBootTest
@Transactional
class MemberControllerTest {

    @Autowired
    private lateinit var memberController: MemberController

    private lateinit var member: Member

    @BeforeEach
    fun setUp() {
        // Member 인스턴스 초기화
        member = Member(
            loginId = "testUser",
            pw = "testPassword",
            name = "Test User",
            email = "test@example.com"
        )
    }

    @Test
    fun validateTest() {
        val bindingResult: BindingResult = BeanPropertyBindingResult(member, "member")
        //에러 추가
        bindingResult.addError(FieldError("member", "loginId", "Login ID is required"))
        //Action
        val errorMessage = ValidationUtils.generateErrorMessage(bindingResult)
        if (errorMessage != null) {
            println(errorMessage)
        }
        println("member")
    }
}