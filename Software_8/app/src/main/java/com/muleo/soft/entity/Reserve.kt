package com.muleo.soft.entity

import com.google.firebase.Timestamp

data class Reserve(
    val id: String? = null,
    val userId: String? = null,
    val count: Long? = null, // 인원수
    val totalPrice: Long? = null, // 총가격
    @field:JvmField
    val isAccept: Boolean? = null,
    val start: Timestamp? = null,
    val end: Timestamp? = null,
    val reserveDate: Timestamp? = null, // 예약 날짜
    val acceptDate: Timestamp? = null, // 승인 날짜
    val campName: String? = null, // 캠핑장 이름
)
