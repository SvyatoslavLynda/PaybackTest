package com.svdroid.paybacktest.utils

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContract
import java.util.*

class RecognitionContract : ActivityResultContract<Int, String>() {
    override fun createIntent(context: Context, input: Int): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.ENGLISH.language)

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
    }
}