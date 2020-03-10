package com.worker8.gradle.lintModel

data class Issues(
    val errorList: MutableList<Issue> = mutableListOf(),
    val warningList: MutableList<Issue> = mutableListOf()
)
