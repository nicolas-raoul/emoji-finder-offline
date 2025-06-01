package com.example.emojifilter.util

import java.lang.Character.UnicodeBlock

object EmojiUtils {
    fun filterUniqueEmojis(text: String): String {
        if (text.isEmpty()) {
            return ""
        }
        val uniqueEmojis = LinkedHashSet<String>()
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            val charCount = Character.charCount(codePoint)
            val emojiStr = text.substring(i, i + charCount)

            // Basic check for common emoji Unicode blocks.
            // This is a simplified check and might not cover all emojis.
            // A more robust solution might involve checking Unicode properties or using a library.
            val block = UnicodeBlock.of(codePoint)
            val type = Character.getType(codePoint)

            // Common emoji blocks and types
            val isEmoji = block == UnicodeBlock.EMOTICONS ||
                          block == UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS ||
                          block == UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS ||
                          block == UnicodeBlock.DINGBATS ||
                          type == Character.OTHER_SYMBOL.toInt() ||
                          // Add more specific ranges if needed, e.g., U+1F900–U+1F9FF (Supplemental Symbols and Pictographs)
                          (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) ||
                          // Regional Indicator Symbols (for flags)
                          (codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF)


            if (isEmoji) {
                uniqueEmojis.add(emojiStr)
            }
            i += charCount
        }
        return uniqueEmojis.joinToString("")
    }
}
