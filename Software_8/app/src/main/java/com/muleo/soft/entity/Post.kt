package com.muleo.soft.entity

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val id: String? = null, // UUID
    val type: Int? = null, // 게시판 타입
    val title: String? = null, // 제목
    val post: String? = null, // 내용
    val date: Timestamp? = null, // 날짜
    val like: Long? = null, // 추천수
    val dislike: Long? = null, // 비추천수
    val userId: String? = null, // 글쓴이
    val images: List<String>? = null, // 글에 있는 이미지들의 경로
    val likesId: List<String>? = null, // 좋아요를 누른 사람
    val dislikesId: List<String>? = null, // 좋아요를 누른 사람
) : Parcelable

enum class PostType(val s: String) {
    POST("내가 쓴 글"), LIKE("좋아요 누른 글"), DISLIKE("싫어요 누른 글"), NOTICE("공지게시판"), REVIEW("후기게시판"), QNA("Q&A게시판")
}

