package com.juul.koap

import com.juul.koap.Message.Option.Block
import kotlin.reflect.KClass

fun blockSizeOf(size: Int, bert: Boolean = false): Block.Size {
    if (bert) {
        require(size == Block.Size.Bert.size) {
            "BERT block sizes must be ${Block.Size.Bert.size}, was $size"
        }
        return Block.Size.Bert
    }
    return Block.Size.entries.firstOrNull { it.size == size }
        ?: run {
            val sizes = Block.Size.entries.map { it.size }.toSet().joinToString(", ")
            throw IllegalArgumentException("Block size of $size is invalid, allowed values: $sizes")
        }
}

internal val Block.intValue: Int
    get() {
        val moreBit = if (more) 1 else 0
        return (number shl 4) or (moreBit shl 3) or size.ordinal
    }

internal inline fun <reified T : Block> blockOf(value: Long): T =
    blockOf(T::class, value)

private fun <T : Block> blockOf(type: KClass<T>, value: Long): T {
    val number = value shr 4
    require(number in BLOCK_NUMBER_RANGE) {
        "Block number $number (from option value of $value) is outside allowable range of $BLOCK_NUMBER_RANGE"
    }
    val more = (value and 0x8L) != 0L
    val size = (value and 0b111).toInt().let {
        Block.Size.entries[it]
    }

    @Suppress("UNCHECKED_CAST")
    return when (type) {
        Message.Option.Block1::class -> Message.Option.Block1(number.toInt(), more, size)
        Message.Option.Block2::class -> Message.Option.Block2(number.toInt(), more, size)
        Message.Option.QBlock1::class -> Message.Option.QBlock1(number.toInt(), more, size)
        Message.Option.QBlock2::class -> Message.Option.QBlock2(number.toInt(), more, size)
        else -> error("Unsupported Block type: ${type.simpleName}")
    } as T
}
