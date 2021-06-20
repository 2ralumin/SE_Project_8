package com.muleo.soft.entity

import com.google.firebase.Timestamp

data class User(
    val email: String? = null,
    val pw: String? = null,
    val age: Int? = null,
    val name: String? = null,
    val phone: String? = null,
    @field:JvmField
    val isManager: Boolean? = null,
    val date: Timestamp? = null,
)