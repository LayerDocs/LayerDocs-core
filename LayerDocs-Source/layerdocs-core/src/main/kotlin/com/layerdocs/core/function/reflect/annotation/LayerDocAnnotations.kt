package com.layerdocs.core.function.reflect.annotation

// Annotations that do not have any runtime effect, but are used for documentation purposes in LayerDoc.

/**
 * When a library function parameter is annotated with `@LikelyBody`,
 * it is marked as a body parameter that usually expects a body argument ([wiki](https://layerdocs.com/wiki/syntax-of-a-function-call#block-vs-inline-function-calls)).
 * This does not have any runtime effect, but is rather used for documentation purposes (see LayerDoc).
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class LikelyBody

/**
 * When a library function parameter is annotated with `@LikelyNamed`,
 * it is marked as a parameter that usually expects a named argument ([wiki](https://layerdocs.com/wiki/syntax-of-a-function-call)).
 * This does not have any runtime effect, but is rather used for documentation purposes (see LayerDoc).
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class LikelyNamed

/**
 * When a library function is annotated with `@LikelyChained`,
 * it is marked as a parameter that usually expects to be chained ([wiki](https://layerdocs.com/wiki/syntax-of-a-function-call#chaining-calls)).
 * This does not have any runtime effect, but is rather used for documentation purposes (see LayerDoc).
 */
@Target(AnnotationTarget.FUNCTION)
annotation class LikelyChained
