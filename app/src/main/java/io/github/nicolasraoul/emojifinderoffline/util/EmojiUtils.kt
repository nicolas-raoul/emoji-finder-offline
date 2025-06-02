package io.github.nicolasraoul.emojifinderoffline.util

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

            val block = UnicodeBlock.of(codePoint)
            val type = Character.getType(codePoint)

            val isEmoji = block == UnicodeBlock.EMOTICONS ||
                          block == UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS ||
                          block == UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS ||
                          block == UnicodeBlock.DINGBATS ||
                          type == Character.OTHER_SYMBOL.toInt() ||
                          (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) || // Supplemental Symbols and Pictographs
                          (codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF)  // Regional Indicator Symbols (flags)

            if (isEmoji) {
                uniqueEmojis.add(emojiStr)
            }
            i += charCount
        }
        return uniqueEmojis.joinToString("")
    }
}
