package io.github.nicolasraoul.emojifinderoffline

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var emojiRecyclerView: RecyclerView
    private lateinit var emojiAdapter: EmojiAdapter

    // Static list of emojis for now
    private val emojis = listOf("🗓️", "⏳", "👩‍💻", "😵‍💫", "🤯")

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

        emojiRecyclerView = findViewById(R.id.emoji_recycler_view)
        emojiRecyclerView.layoutManager = GridLayoutManager(this, 5) // 5 columns in the grid

        emojiAdapter = EmojiAdapter(emojis) { emoji ->
            copyToClipboard(emoji)
            Snackbar.make(mainView, getString(R.string.copied_to_clipboard, emoji), Snackbar.LENGTH_SHORT).show()
        }
        emojiRecyclerView.adapter = emojiAdapter
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("emoji", text)
        clipboard.setPrimaryClip(clip)
    }
}