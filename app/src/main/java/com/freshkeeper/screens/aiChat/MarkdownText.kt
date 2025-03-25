package com.freshkeeper.screens.aiChat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser

@Suppress("ktlint:standard:function-naming")
@Composable
fun MarkdownText(
    modifier: Modifier = Modifier,
    markdown: String,
) {
    val annotatedText =
        remember(markdown) {
            parseMarkdownToAnnotatedString(markdown)
        }
    Text(
        text = annotatedText,
        fontSize = 14.sp,
        modifier = modifier,
    )
}

fun parseMarkdownToAnnotatedString(markdown: String): AnnotatedString {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val builder = AnnotatedString.Builder()
    processNodes(document, builder)
    return builder.toAnnotatedString()
}

private fun processNodes(
    node: Node,
    builder: AnnotatedString.Builder,
) {
    var child = node.firstChild
    while (child != null) {
        val nextSibling = child.next
        when (child) {
            is Text -> {
                builder.append(child.literal)
            }
            is Emphasis -> {
                val start = builder.length
                processNodes(child, builder)
                val end = builder.length
                builder.addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
            }
            is StrongEmphasis -> {
                val start = builder.length
                processNodes(child, builder)
                val end = builder.length
                builder.addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
            }
            is Code -> {
                val start = builder.length
                builder.append(child.literal)
                val end = builder.length
                builder.addStyle(
                    SpanStyle(fontFamily = FontFamily.Monospace),
                    start,
                    end,
                )
            }
            is Link -> {
                val start = builder.length
                processNodes(child, builder)
                val end = builder.length
                builder.addStringAnnotation(
                    tag = "URL",
                    annotation = child.destination,
                    start = start,
                    end = end,
                )
                builder.addStyle(
                    SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
                    start,
                    end,
                )
            }
            is Paragraph -> {
                processNodes(child, builder)
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is Heading -> {
                if (builder.isNotEmpty() && !builder.toString().endsWith("\n")) {
                    builder.append("\n")
                }
                val start = builder.length
                processNodes(child, builder)
                val end = builder.length
                val headingStyle =
                    when (child.level) {
                        1 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        2 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        3 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        4 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        5 -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        else -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                builder.addStyle(headingStyle, start, end)
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is BlockQuote -> {
                val start = builder.length
                processNodes(child, builder)
                val end = builder.length
                builder.addStyle(
                    SpanStyle(color = Color.Gray, fontStyle = FontStyle.Italic),
                    start,
                    end,
                )
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is BulletList -> {
                var listItem = child.firstChild
                while (listItem != null) {
                    if (listItem is ListItem) {
                        builder.append("• ")
                        processNodes(listItem, builder)
                        builder.append("\n")
                    }
                    listItem = listItem.next
                }
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is OrderedList -> {
                var index = child.startNumber
                var listItem = child.firstChild
                while (listItem != null) {
                    if (listItem is ListItem) {
                        builder.append("$index. ")
                        processNodes(listItem, builder)
                        builder.append("\n")
                        index++
                    }
                    listItem = listItem.next
                }
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is FencedCodeBlock -> {
                val start = builder.length
                builder.append(child.literal)
                val end = builder.length
                builder.addStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                    ),
                    start,
                    end,
                )
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is IndentedCodeBlock -> {
                val start = builder.length
                builder.append(child.literal)
                val end = builder.length
                builder.addStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                    ),
                    start,
                    end,
                )
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is ThematicBreak -> {
                builder.append("\n----------------\n")
                if (nextSibling != null) {
                    builder.append("\n")
                }
            }
            is SoftLineBreak -> {
                builder.append(" ")
            }
            is HardLineBreak -> {
                builder.append("\n")
            }
            else -> {
                processNodes(child, builder)
            }
        }
        child = nextSibling
    }
}

private fun AnnotatedString.Builder.isNotEmpty(): Boolean = this.length > 0
