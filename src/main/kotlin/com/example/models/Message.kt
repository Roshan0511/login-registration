package com.example.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Message @JsonCreator constructor(
    @JsonProperty("type") val type: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("metadata") val metadata: Map<String, String>? = null
)