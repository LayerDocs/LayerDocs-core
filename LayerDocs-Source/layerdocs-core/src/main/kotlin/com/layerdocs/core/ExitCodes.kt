package com.layerdocs.core

/**
 * Exit code when a LayerDocs function was invoked by an incompatible call.
 * @see com.layerdocs.core.function.error.InvalidFunctionCallException
 */
const val BAD_FUNCTION_CALL_EXIT_CODE = 66

/**
 * Exit code when a LayerDocs function can't be resolved.
 * @see com.layerdocs.core.function.error.UnresolvedReferenceException
 */
const val UNRESOLVED_REFERENCE_EXIT_CODE = 67

/**
 * Exit code when a dynamic value cannot be converted to a static type via [com.layerdocs.core.function.value.factory.ValueFactory].
 * @see com.layerdocs.core.function.value.factory.IllegalRawValueException
 */
const val ILLEGAL_TYPE_CONVERSION_EXIT_CODE = 68

/**
 * Exit code when an element (e.g. an enum value from a LayerDocs function argument)
 * does not exist in a look-up table.
 * @see com.layerdocs.core.function.error.NoSuchElementException
 */
const val NO_SUCH_ELEMENT_EXIT_CODE = 69

/**
 * Exit code when a I/O error occurs.
 * @see com.layerdocs.core.pipeline.error.IOPipelineException
 */
const val IO_ERROR_EXIT_CODE = 70

/**
 * Exit code when a runtime error occurs.
 */
const val RUNTIME_ERROR_EXIT_CODE = 71

/**
 * Exit code when a required permission is not granted.
 * @see com.layerdocs.core.permissions.MissingPermissionException
 */
const val MISSING_PERMISSION_EXIT_CODE = 72

/**
 * Exit code when execution times out, matching GNU `timeout` convention.
 */
const val TIMEOUT_EXIT_CODE = 124
