package edu.example.kotlindevelop.domain.member.util

import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

object ValidationUtils {
    fun generateErrorMessage(bindingResult: BindingResult): String? {
        return if (bindingResult.hasErrors()) {
            bindingResult.allErrors.joinToString(", ") { error ->
                when (error) {
                    is FieldError -> error.defaultMessage ?: "Invalid field: ${error.field}"
                    else -> error.defaultMessage ?: "Unknown error"
                }
            }
        } else {
            null
        }
    }
}
