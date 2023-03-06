package com.svdroid.paybacktest.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.svdroid.paybacktest.R
import com.svdroid.paybacktest.utils.Destination

@Composable
fun AppAlertDialog(hitId: Int, navController: NavHostController) {
    AlertDialog(
        text = { Text(text = stringResource(id = R.string.details_message)) },
        onDismissRequest = {},
        buttons = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                Row {
                    TextButton(onClick = { navController.popBackStack() },
                        content = { Text(text = stringResource(id = android.R.string.cancel)) })
                    TextButton(onClick = {
                        navController.navigate(Destination.Details.createRoute(hitId))
                    }, content = { Text(text = stringResource(id = android.R.string.ok)) })
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
    )
}