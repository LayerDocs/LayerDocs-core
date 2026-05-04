package com.layerdocs.stdlib

import com.layerdocs.core.ast.InlineMarkdownContent
import com.layerdocs.core.ast.MarkdownContent
import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.UnorderedList
import com.layerdocs.core.attachMockPipeline
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.call.FunctionCallArgument
import com.layerdocs.core.function.error.InvalidLambdaArgumentCountException
import com.layerdocs.core.function.value.DynamicValue
import com.layerdocs.core.function.value.OutputValue
import com.layerdocs.core.function.value.StringValue
import com.layerdocs.core.function.value.VoidValue
import com.layerdocs.core.function.value.data.Lambda
import com.layerdocs.core.function.value.data.LambdaParameter
import com.layerdocs.core.function.value.data.Range
import com.layerdocs.core.function.value.factory.ValueFactory
import com.layerdocs.core.function.value.output.node.BlockNodeOutputValueVisitor
import com.layerdocs.core.function.value.output.node.InlineNodeOutputValueVisitor
import com.layerdocs.core.function.value.wrappedAsValue
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 * [Flow] module tests.
 */
class FlowTest {
    private val context = MutableContext(LayerDocsFlavor)

    @BeforeTest
    fun setup() {
        context.attachMockPipeline()
    }

    private fun call(
        functionName: String,
        arguments: List<FunctionCallArgument>,
    ): OutputValue<*> {
        with(context.getFunctionByName(functionName)) {
            assertNotNull(this)
            assertEquals(functionName, name)
            assertEquals(arguments.size, parameters.size)

            val call = FunctionCall(this, arguments, context)
            return call.execute()
        }
    }

    @Test
    fun `custom function`() {
        function(
            context,
            name = "myfunc1",
            body = Lambda(context, explicitParameters = emptyList()) { _, _ -> "Hello LayerDocs".wrappedAsValue() },
        )

        assertEquals(
            StringValue("Hello LayerDocs"),
            call("myfunc1", arguments = emptyList()),
        )

        function(
            context,
            name = "myfunc2",
            body = ValueFactory.lambda("- Hello **LayerDocs**\n- Hello", context).unwrappedValue,
        )

        call("myfunc2", arguments = emptyList()).let {
            assertIs<DynamicValue>(it)
            assertEquals("- Hello **LayerDocs**\n- Hello", it.unwrappedValue)

            // Block node conversion
            val blockNode = BlockNodeOutputValueVisitor(context).visit(it)
            assertIs<MarkdownContent>(blockNode)
            assertEquals(1, blockNode.children.size)

            // Inline node conversion
            val inlineNode = InlineNodeOutputValueVisitor(context).visit(it)
            assertIs<InlineMarkdownContent>(inlineNode)
            assertEquals(3, inlineNode.children.size)

            val list = blockNode.children.first()
            assertIs<UnorderedList>(list)
            assertEquals(2, list.children.size)
            assertIs<ListItem>(list.children[0])
            assertIs<ListItem>(list.children[1])
        }

        function(
            context,
            name = "myfunc3",
            body = ValueFactory.lambda("to from: Hello **.to** from _.from_", context).unwrappedValue,
        )

        assertEquals(
            DynamicValue("Hello **LayerDocs** from _SatyamPote_"),
            call(
                "myfunc3",
                arguments =
                    listOf(
                        FunctionCallArgument(DynamicValue("LayerDocs")),
                        FunctionCallArgument(DynamicValue("SatyamPote")),
                    ),
            ),
        )
    }

    @Test
    fun `control flow`() {
        val control1 =
            `if`(
                isLower(2, 4).unwrappedValue,
                Lambda(context, explicitParameters = emptyList()) { _, _ -> "Hello LayerDocs".wrappedAsValue() },
            )
        assertEquals("Hello LayerDocs", control1.unwrappedValue)

        val control2 =
            `if`(
                isGreater(2, 4).unwrappedValue,
                Lambda(context, explicitParameters = emptyList()) { _, _ -> "Hello LayerDocs".wrappedAsValue() },
            )
        assertEquals(VoidValue, control2)

        val control3 =
            ifNot(
                isGreater(2, 4).unwrappedValue,
                Lambda(context, explicitParameters = emptyList()) { _, _ -> "Hello LayerDocs".wrappedAsValue() },
            )
        assertEquals("Hello LayerDocs", control3.unwrappedValue)

        assertFailsWith<InvalidLambdaArgumentCountException> {
            `if`(
                isLower(2, 4).unwrappedValue,
                Lambda(context, explicitParameters = listOf(LambdaParameter("a"))) { _, _ -> "Hello LayerDocs".wrappedAsValue() },
            )
        }
    }

    @Test
    fun `loop flow`() {
        val loop1 =
            forEach(
                listOf(
                    StringValue("Hello"),
                    StringValue("LayerDocs"),
                ),
                body =
                    Lambda(context, explicitParameters = emptyList()) { args, _ ->
                        "**${args.first().unwrappedValue}**".wrappedAsValue()
                    },
            )

        assertEquals(
            listOf(
                StringValue("**Hello**"),
                StringValue("**LayerDocs**"),
            ),
            loop1.unwrappedValue,
        )

        val loop2 =
            forEach(
                Range(start = 2, end = 4),
                // Explicit lambda placeholder
                body = ValueFactory.lambda("n: \nN: .n", context).unwrappedValue,
            )

        assertEquals(
            listOf(
                DynamicValue("N: 2"),
                DynamicValue("N: 3"),
                DynamicValue("N: 4"),
            ),
            loop2.unwrappedValue,
        )

        val loop3 =
            forEach(
                ValueFactory.range("..4").unwrappedValue,
                body = ValueFactory.lambda("N\\: .1", context).unwrappedValue,
            )

        assertEquals(
            listOf(
                DynamicValue("N: 1"),
                DynamicValue("N: 2"),
                DynamicValue("N: 3"),
                DynamicValue("N: 4"),
            ),
            loop3.unwrappedValue,
        )

        // Iterating ranges with indefinite right end is not allowed.
        assertFails {
            forEach(
                ValueFactory.range("1..").unwrappedValue,
                body = ValueFactory.lambda("N\\: .1", context).unwrappedValue,
            )
        }
    }
}
