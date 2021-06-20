package com.muleo.soft.entity

import com.google.firebase.Timestamp

data class Camp(
    val id: String? = null, // 번호
    val name: String? = null, // 이름
    val latitude: Double? = null, // 위도
    val longitude: Double? = null, // 경도
    val address: String? = null, // 캠핑장 주소
    @field:JvmField
    val isEmpty: Boolean? = null, // 캠핑장이 비었는가
    val imgPath: String? = null, // 이미지 경로
    val accountNumber: String? = null, // 계좌번호
    val info: String? = null, // 정보
    val date: Timestamp? = null,
    val won: Long? = null// 인당 가격
)

