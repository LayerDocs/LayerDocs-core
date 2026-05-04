package com.layerdocs.core.util

/**
 * Name of the enum in kebab-case.
 * Example: `TOP_LEFT_CORNER` -> `top-left-corner`
 */
val Enum<*>.kebabCaseName: String
    get() = name.lowercase().replace("_", "-")
