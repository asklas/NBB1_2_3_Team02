package edu.example.kotlindevelop.domain.member.controller

import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.service.MemberService
import edu.example.kotlindevelop.domain.member.util.ValidationUtils
import edu.example.kotlindevelop.global.security.SecurityUser
import jakarta.mail.internet.MimeMessage
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/v1/members")
class MemberController (
    val memberService: MemberService,
    val resourceLoader: ResourceLoader,
    val mailSender: JavaMailSender
){
    @GetMapping("/loginSuccess")
    fun loginSuccess(@AuthenticationPrincipal oauth2User: OAuth2User): ResponseEntity<Any> {
        val userId = oauth2User.getAttribute<String>("providerId")
        return memberService.checkLoginIdAndPassword(userId!!, "123").let {
            val accessToken = memberService.generateAccessToken(it.id, it.loginId)
            val refreshToken = memberService.generateRefreshToken(it.id, it.loginId)

            memberService.setRefreshToken(it.id, refreshToken)

            it.apply {
                this.accessToken = accessToken
                this.refreshToken = refreshToken
            }
            ResponseEntity.ok(mapOf("redirectUrl" to "http://localhost:3000", "userInfo" to it))
        }
    }

    //회원가입
    @PostMapping("/register")
    fun register(
        @Validated @RequestBody request: MemberDTO.CreateRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        val errorMessage = ValidationUtils.generateErrorMessage(bindingResult)
        if (errorMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MemberDTO.StringResponseDto(errorMessage))
        }
        return ResponseEntity.ok(memberService.create(request))
    }

    //로그인
    @PostMapping("/login")
    fun login(
        @Validated @RequestBody request: MemberDTO.LoginRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        // 아이디, 비밀번호가 입력 되었는지 확인 과정 추가
        val errorMessage = ValidationUtils.generateErrorMessage(bindingResult)
        if (errorMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MemberDTO.StringResponseDto(errorMessage))
        }
        // 인증 성공
        return memberService.checkLoginIdAndPassword(request.loginId, request.pw).let {
                val accessToken = memberService.generateAccessToken(it.id, it.loginId)
                val refreshToken = memberService.generateRefreshToken(it.id, it.loginId)

                memberService.setRefreshToken(it.id, refreshToken)

                it.apply {
                    this.accessToken = accessToken
                    this.refreshToken = refreshToken
                }
                ResponseEntity.ok(it)
        }
    }


    @PostMapping("/refreshAccessToken")
    fun login(@RequestBody request: MemberDTO.RefreshAccessTokenRequestDto): ResponseEntity<MemberDTO.RefreshAccessTokenResponseDto> {
        val accessToken: String = memberService.refreshAccessToken(request.refreshToken)
        val responseDto: MemberDTO.RefreshAccessTokenResponseDto =
            MemberDTO.RefreshAccessTokenResponseDto(accessToken, "새로운 AccessToken 발급")
        // 리턴 값이 명백하기 때문에 일부 생략
        // return ResponseEntity.ok<MemberDTO.RefreshAccessTokenResponseDto>(responseDto)
        return ResponseEntity.ok(responseDto)

    }


    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<MemberDTO.LogoutResponseDto> {
        memberService.setRefreshToken(user.id, "null")

        return ResponseEntity.ok(MemberDTO.LogoutResponseDto("로그아웃 되었습니다"))
    }



     //나의 회원 정보 조회화기
     @GetMapping("/")
     fun getMyPage(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<MemberDTO.Response> {
         // DTO 객체의 생성자에서 기본값 지정에 따른 null 예외처리 제거 및 불필요한 변수 생성과정 삭제
         //val id: Long = user.id ?: throw IllegalArgumentException("User ID cannot be null")
         return ResponseEntity.ok(memberService.read(user.id))
     }

    //다른 유저의 회원정보 조회하기

    // 너무 과도한 권한을 주는것은 아닌지? 고민해야함

    @GetMapping("/{id}")
    fun read(@PathVariable id: Long): ResponseEntity<MemberDTO.Response> {
        return ResponseEntity.ok(memberService.read(id))
    }


    //내 정보 수정하기
    @PutMapping("/")
    fun modify(
        @AuthenticationPrincipal user: SecurityUser,
        // 원하는 필드만 수정할 수 있어야 하므로 null 허용 및 불필요한 @Validated 어노테이션 제거
        //@Validated @RequestBody dto: MemberDTO.Update
        @RequestBody dto: MemberDTO.Update
    ): ResponseEntity<MemberDTO.Update> {
        dto.id = user.id
        return ResponseEntity.ok(memberService.update(dto))
    }

    //주문 삭제하기
    @DeleteMapping("/")
    fun delete(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<Map<String, String>> {
        val id: Long = user.id
        memberService.delete(id)
        return ResponseEntity.ok(java.util.Map.of("result", "success"))
    }

    @PutMapping("/update-image")
    fun modifyImage(
        @AuthenticationPrincipal user: SecurityUser,
        @RequestParam("mImage") mImage: MultipartFile
    ): ResponseEntity<MemberDTO.ChangeImageResponse> {
        val dto: MemberDTO.ChangeImage = MemberDTO.ChangeImage(user.id,mImage)
        return ResponseEntity.ok(memberService.changeImage(dto, mImage))
    }

    // 이미지 서빙 엔드포인트
    @GetMapping("/upload/{filename:.+}")
    fun serveImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = resourceLoader.getResource("file:upload/$filename")
        return ResponseEntity.ok(file)
    }

    //아이디 찾기
    @GetMapping("/findId")
    fun findId(@RequestParam email: String): ResponseEntity<String> {
        val loginId: String = memberService.findByEmail(email)
        return ResponseEntity.ok(loginId)
    }

    //비밀번호 찾기
    @PostMapping("/findPW")
    fun findPw(
        @RequestBody request: MemberDTO.FindPWRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        val errorMessage = ValidationUtils.generateErrorMessage(bindingResult)
        if (errorMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MemberDTO.StringResponseDto(errorMessage))
        }

        //데이터베이스의 비밀번호를 임시 비밀번호로 변경
        val templatePassword: String = memberService.setTemplatePassword(request.loginId, request.email )

        // 메일 발송시도 및 예외처리
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            MimeMessageHelper(mailSender.createMimeMessage(), true, "UTF-8").run {
                setFrom("seodo1e1205@gmail.com")
                setTo(request.email)
                setSubject("데브코스 팀2 아이디/비밀번호 찾기 인증 이메일 입니다.")
                setText(
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "임시 비밀번호로 로그인 후 꼭 새로운 비밀번호로 설정해주시기 바랍니다." +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "임시비밀번호는 " + templatePassword + " 입니다. " +
                    System.lineSeparator()
                )
                mailSender.send(message)
            }
        } catch (e: Exception) {
            log.debug(e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("임시 비밀번호 전송을 실패하였습니다")
        }
        return ResponseEntity.ok("임시 비밀번호를 이메일로 전송했습니다")
    }
}