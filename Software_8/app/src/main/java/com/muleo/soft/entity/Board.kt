package com.muleo.soft.entity

data class Board(
    val type: Int? = null, // 유형
    val name: String? = null, // 이름
)

enum class BoardType {
    NOTICE, REVIEW
}
