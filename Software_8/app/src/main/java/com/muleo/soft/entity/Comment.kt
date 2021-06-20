package com.muleo.soft.entity

import com.google.firebase.Timestamp

data class Comment(
    val id: String? = null, // UUID
    val date: Timestamp? = null, // 생성 날짜
    val comment: String? = null, // 내용
    val userId: String? = null, // 댓쓴이
)
