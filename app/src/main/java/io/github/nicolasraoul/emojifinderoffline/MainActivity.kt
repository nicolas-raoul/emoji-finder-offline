package io.github.nicolasraoul.emojifinderoffline

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.edge.aicore.GenerativeAIException
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.generationConfig
import io.github.nicolasraoul.emojifinderoffline.util.EmojiUtils // Added import
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var model: GenerativeModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainView = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        model =
            GenerativeModel(
                generationConfig {
                    context = applicationContext
                    temperature = 0.2f
                    topK = 16
                    maxOutputTokens = 256
                }
            )

        val inputText = findViewById<EditText>(R.id.input_text)
        val emojiDisplay = findViewById<FlexboxLayout>(R.id.emoji_display_container)
        val generateButton = findViewById<Button>(R.id.generate_button)

        generateButton.setOnClickListener {
            val text = inputText.text.toString()
            if (text.isNotEmpty()) {
                emojiDisplay.removeAllViews() // Clear previous results
                CoroutineScope(Dispatchers.Main).launch {
                    val accumulatedUniqueEmojis = LinkedHashSet<String>() // Initialize set for unique emojis
                    try {
                        model!!.generateContentStream("Output a dozen of emojis related to \"$text\". Do not output anything else than emojis.")
                            .collect { chunk ->
                                val chunkText = chunk.text ?: "" // Corrected line
                                val emojisFromChunkString = EmojiUtils.filterUniqueEmojis(chunkText)

                                // Iterate through the string of emojis returned by filterUniqueEmojis
                                var k = 0
                                while (k < emojisFromChunkString.length) {
                                    val codePoint = emojisFromChunkString.codePointAt(k)
                                    val charCount = Character.charCount(codePoint)
                                    val singleEmoji = emojisFromChunkString.substring(k, k + charCount)
                                    accumulatedUniqueEmojis.add(singleEmoji) // Add to set to ensure uniqueness
                                    k += charCount
                                }
                            }
                        // After collecting all chunks, add each unique emoji as a separate TextView
                        accumulatedUniqueEmojis.forEach { emoji ->
                            val emojiTextView = TextView(this@MainActivity).apply {
                                this.text = emoji
                                textSize = 96f // SP value
                                val paddingDp = 8
                                val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()
                                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                                setOnClickListener {
                                    val clickedEmoji = (it as TextView).text.toString()
                                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Copied Emoji", clickedEmoji)
                                    clipboard.setPrimaryClip(clip)
                                    Snackbar.make(mainView, "Copied $clickedEmoji to clipboard", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                            emojiDisplay.addView(emojiTextView)
                        }
                    } catch (e: GenerativeAIException) {
                        Log.e("EmojiSearch", "Error generating emojis", e)
                        emojiDisplay.removeAllViews()
                        val errorTextView = TextView(this@MainActivity).apply {
                            this.text = "Error generating emojis."
                            textSize = 18f
                            val paddingDp = 8
                            val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()
                            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                        }
                        emojiDisplay.addView(errorTextView)
                    }
                }
            } else {
                emojiDisplay.removeAllViews()
                Snackbar.make(mainView, "Please enter some text.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}