package edu.example.kotlindevelop.domain.member.exception

class MemberTaskException(message: String?, val statusCode: Int) : RuntimeException(message)