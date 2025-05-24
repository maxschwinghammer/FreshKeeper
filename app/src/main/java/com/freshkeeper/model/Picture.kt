package com.freshkeeper.model

data class Picture(
    val image: String? = null,
    val type: ImageType? = null,
) {
    constructor() : this(null, null)
}
