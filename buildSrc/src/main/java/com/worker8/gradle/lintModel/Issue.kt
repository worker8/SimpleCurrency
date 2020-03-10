package com.worker8.gradle.lintModel

data class Issue(
    val id: String,
    val severity: String,
    val message: String,
    val category: String,
    val priority: String,
    val summary: String,
    val explanation: String,
    val errorLine1: String,
    val errorLine2: String,
    val location: Location
)
