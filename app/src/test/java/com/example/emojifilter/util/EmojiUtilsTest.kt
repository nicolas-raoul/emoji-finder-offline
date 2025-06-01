package com.example.emojifilter.util

import org.junit.Assert.assertEquals
import org.junit.Test

class EmojiUtilsTest {

    @Test
    fun testFilterUniqueEmojis_mixedString() {
        assertEquals("😂👍✨", EmojiUtils.filterUniqueEmojis("Hello 😂 World 👍 This is a test ✨ with emojis."))
    }

    @Test
    fun testFilterUniqueEmojis_duplicateEmojis() {
        assertEquals("😂👍✨", EmojiUtils.filterUniqueEmojis("😂👍✨😂😂👍👍✨✨"))
    }

    @Test
    fun testFilterUniqueEmojis_onlyEmojis() {
        assertEquals("😂👍✨", EmojiUtils.filterUniqueEmojis("😂👍✨"))
    }

    @Test
    fun testFilterUniqueEmojis_noEmojis() {
        assertEquals("", EmojiUtils.filterUniqueEmojis("Hello World This is a test."))
    }

    @Test
    fun testFilterUniqueEmojis_emptyString() {
        assertEquals("", EmojiUtils.filterUniqueEmojis(""))
    }

    @Test
    fun testFilterUniqueEmojis_emojisWithTextBetween() {
        assertEquals("😂✨", EmojiUtils.filterUniqueEmojis("😂Hello✨World"))
    }

    @Test
    fun testFilterUniqueEmojis_variousEmojis() {
        assertEquals("😀🚗💖✅🧠", EmojiUtils.filterUniqueEmojis("Text 😀 with 🚗 many 💖 different ✅ emojis 🧠 here."))
    }

    @Test
    fun testFilterUniqueEmojis_flags() {
        assertEquals("🇺🇸", EmojiUtils.filterUniqueEmojis("Hello 🇺🇸 World")) // U+1F1FA U+1F1F8
    }

    @Test
    fun testFilterUniqueEmojis_textSurroundingEmojis() {
        assertEquals("👍🎉", EmojiUtils.filterUniqueEmojis("Start👍Middle🎉End"))
    }

    @Test
    fun testFilterUniqueEmojis_nonEmojiSymbols() {
        assertEquals("", EmojiUtils.filterUniqueEmojis("Test with symbols like © ® ™ $ € ¥ + - = ≠"))
    }

    @Test
    fun testFilterUniqueEmojis_complexEmojisAndSequences() {
        // Test with skin tone modifier. U+1F44D (👍) and U+1F3FD (🏾) are both emojis by the current logic.
        assertEquals("👍🏾", EmojiUtils.filterUniqueEmojis("This is a test 👍🏾"))
        assertEquals("👍🏾", EmojiUtils.filterUniqueEmojis("This is a test 👍🏾 and another 👍🏾"))


        // Test with ZWJ sequence. Constituent parts are emojis, ZWJ is not.
        // 👨 (U+1F468), 👩 (U+1F469), 👧 (U+1F467), 👦 (U+1F466) are emojis.
        // ‍ (U+200D) is not.
        assertEquals("👨👩👧👦", EmojiUtils.filterUniqueEmojis("A family: 👨‍👩‍👧‍👦"))
        assertEquals("👨👩👧👦", EmojiUtils.filterUniqueEmojis("Families: 👨‍👩‍👧‍👦, 👨‍👩‍👧‍👦"))
    }
}
