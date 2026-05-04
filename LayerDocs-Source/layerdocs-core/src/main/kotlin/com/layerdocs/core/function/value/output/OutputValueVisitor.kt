package com.layerdocs.core.function.value.output

import com.layerdocs.core.function.value.BooleanValue
import com.layerdocs.core.function.value.DictionaryValue
import com.layerdocs.core.function.value.DynamicValue
import com.layerdocs.core.function.value.GeneralCollectionValue
import com.layerdocs.core.function.value.NodeValue
import com.layerdocs.core.function.value.NoneValue
import com.layerdocs.core.function.value.NumberValue
import com.layerdocs.core.function.value.ObjectValue
import com.layerdocs.core.function.value.OrderedCollectionValue
import com.layerdocs.core.function.value.PairValue
import com.layerdocs.core.function.value.StringValue
import com.layerdocs.core.function.value.UnorderedCollectionValue
import com.layerdocs.core.function.value.VoidValue

/**
 * A visitor that produces values the same type for each [com.layerdocs.core.function.value.OutputValue] type.
 */
interface OutputValueVisitor<T> {
    fun visit(value: StringValue): T

    fun visit(value: NumberValue): T

    fun visit(value: BooleanValue): T

    fun visit(value: ObjectValue<*>): T

    fun visit(value: OrderedCollectionValue<*>): T

    fun visit(value: UnorderedCollectionValue<*>): T

    fun visit(value: GeneralCollectionValue<*>): T

    fun visit(value: PairValue<*, *>): T

    fun visit(value: DictionaryValue<*>): T

    fun visit(value: NodeValue): T

    fun visit(value: NoneValue): T

    fun visit(value: VoidValue): T

    fun visit(value: DynamicValue): T
}
