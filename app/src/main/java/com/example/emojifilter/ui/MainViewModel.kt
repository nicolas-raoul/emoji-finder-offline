package com.example.emojifilter.ui

import com.example.emojifilter.util.EmojiUtils

class MainViewModel {

    // Placeholder for where the LLM prompt result might be stored or observed
    private var llmResult: String = "Initial LLM Result with emojis 😂 and text and duplicates 😂✨"

    // Function that processes the LLM result
    fun processLlmResult() {
        // Original result from LLM
        val originalResult = getLlmPromptResult() // Placeholder for actual data fetching

        // Filter for unique emojis
        val filteredEmojis = EmojiUtils.filterUniqueEmojis(originalResult)

        // Update the stored result or UI state with the filtered emojis
        llmResult = filteredEmojis
        // In a real app, this would likely update LiveData or a StateFlow
        println("Original LLM Result: $originalResult")
        println("Filtered Emojis: $filteredEmojis")
    }

    // Placeholder function to simulate getting an LLM prompt result
    private fun getLlmPromptResult(): String {
        // Simulate some LLM output
        return "This is a 🥳 test string from LLM with some 😊 emojis and text, plus duplicates 🥳🥳✨. End."
    }

    fun getProcessedLlmResult(): String {
        return llmResult
    }
}
