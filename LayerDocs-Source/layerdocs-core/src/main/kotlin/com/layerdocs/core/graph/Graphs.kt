@file:Suppress("FunctionName")

package com.layerdocs.core.graph

/**
 * @return a new empty directed [Graph]
 */
fun <T> DirectedGraph(): Graph<T> = PersistentDirectedGraph()
