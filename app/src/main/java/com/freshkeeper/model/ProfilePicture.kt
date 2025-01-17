package com.freshkeeper.model

data class ProfilePicture(
    val image: String? = null,
    val type: String? = null,
) {
    constructor() : this(null, null)
}
