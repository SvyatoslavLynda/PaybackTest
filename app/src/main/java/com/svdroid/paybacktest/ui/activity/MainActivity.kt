package com.svdroid.paybacktest.ui.activity

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.svdroid.paybacktest.R
import com.svdroid.paybacktest.ui.NoInternetMessage
import com.svdroid.paybacktest.ui.screen.PixabayNavHost
import com.svdroid.paybacktest.ui.theme.PaybackTestTheme
import com.svdroid.paybacktest.utils.Destination
import com.svdroid.paybacktest.utils.RecognitionContract
import com.svdroid.paybacktest.utils.hasNetwork
import com.svdroid.paybacktest.utils.openGooglePlayToDownloadGoogleQuickSearchBox
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navController: NavHostController

    private val isOnline by lazy { mutableStateOf(hasNetwork(this)) }
    private val voiceInputLauncher = registerForActivityResult(RecognitionContract()) { result ->
        navController.navigate(Destination.Main.createRoute(result)) {
            popUpTo(Destination.Main.route) {
                saveState = true
                inclusive = true
            }
        }
    }
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onVoiceInputClick()
            } else {
                val shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.RECORD_AUDIO,
                )

                if (!shouldShowRequestPermissionRationale) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_permission_is_not_granted_message),
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    private val networkListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.apply { isOnline.value = hasNetwork(this) }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PaybackTestTheme {
                Box {
                    PixabayNavHost(
                        navController = navController,
                        onVoiceInputClick = { onVoiceInputClick() }
                    )

                    if (!isOnline.value) NoInternetMessage(
                        modifier = Modifier
                            .align(alignment = Alignment.BottomCenter)
                            .background(Color.Red.copy(alpha = 0.8f), RectangleShape)
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        try {
            registerReceiver(
                networkListener,
                IntentFilter().apply { addAction("android.net.conn.CONNECTIVITY_CHANGE") }
            )
        } catch (_: Exception) {
        }
    }

    override fun onStop() {
        super.onStop()

        try {
            unregisterReceiver(networkListener)
        } catch (_: Exception) {
        }
    }

    private fun onVoiceInputClick() {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            try {
                voiceInputLauncher.launch(0)
            } catch (_: ActivityNotFoundException) {
                openGooglePlayToDownloadGoogleQuickSearchBox(this)
            }
        } else {
            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }
}