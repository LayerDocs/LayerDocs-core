package com.layerdocs.stdlib

import com.layerdocs.core.context.Context
import com.layerdocs.core.function.library.Library
import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.library.module.moduleOf
import com.layerdocs.core.function.reflect.annotation.Injected
import com.layerdocs.core.function.reflect.annotation.Name
import com.layerdocs.core.function.value.BooleanValue
import com.layerdocs.core.function.value.UnorderedCollectionValue
import com.layerdocs.core.function.value.wrappedAsValue

/**
 * `Library` stdlib module exporter.
 * This module handles loaded libraries and their functions.
 */
val Library: LayerDocsModule =
    moduleOf(
        ::libraryExists,
        ::functionExists,
        ::libraries,
        ::libraryFunctions,
    )

/**
 * @param context context to search in
 * @param name name of the library, case-insensitive
 * @return library with the given name, if it exists
 */
private fun findLibrary(
    context: Context,
    name: String,
): Library? = context.libraries.find { it.name.equals(name, ignoreCase = true) }

/**
 * Checks whether a library with the given name is registered in [context].
 *
 * @param name name of the library, case-insensitive
 * @return whether a library with the given name is registered in [context]
 */
@Name("libexists")
fun libraryExists(
    @Injected context: Context,
    name: String,
) = BooleanValue(findLibrary(context, name) != null)

/**
 * Checks whether a function with the given name is registered in [context].
 *
 * @param name name of the function, case-insensitive
 * @return whether a function with the given name is registered in [context]
 */
@Name("functionexists")
fun functionExists(
    @Injected context: Context,
    name: String,
) = BooleanValue(context.getFunctionByName(name) != null)

/**
 * Lists the names of all libraries loaded in [context].
 *
 * @return an unordered collection of the loaded libraries' names
 */
fun libraries(
    @Injected context: Context,
) = UnorderedCollectionValue(
    context.libraries
        .asSequence()
        .map { it.name.wrappedAsValue() }
        .toSet(),
)

/**
 * Lists the names of all functions exposed by the library with the given name.
 *
 * @param libraryName name of the library, case-insensitive
 * @return unordered set of functions exposed by the library, or an empty one if the library is not found
 */
@Name("libfunctions")
fun libraryFunctions(
    @Injected context: Context,
    libraryName: String,
) = UnorderedCollectionValue(
    findLibrary(context, libraryName)
        ?.functions
        ?.asSequence()
        ?.map { it.name.wrappedAsValue() }
        ?.toSet()
        ?: emptySet(),
)
