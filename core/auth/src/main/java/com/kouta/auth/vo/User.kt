package com.kouta.auth.vo

data class User(
    val name: String,
    val profileUrl: String
) {
    companion object {
        fun fixture() = User(
            name = "name",
            profileUrl = "profileUrl"
        )
    }
}
