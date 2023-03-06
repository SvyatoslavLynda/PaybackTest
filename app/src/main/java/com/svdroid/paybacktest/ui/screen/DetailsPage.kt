package com.svdroid.paybacktest.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.svdroid.paybacktest.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.svdroid.paybacktest.ui.InfoTextRecord
import com.svdroid.paybacktest.ui.PixabayIconButton
import com.svdroid.paybacktest.ui.SystemStatusBarPainter
import com.svdroid.paybacktest.ui.ZoomableImage
import com.svdroid.paybacktest.ui.screen.vm.DetailsViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun DetailsPage(id: Int) {
    val viewModel = hiltViewModel<DetailsViewModel>()
    val hit = viewModel.getHitBy(id)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()

    SystemStatusBarPainter(Color.Black)

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(text = hit.userName) },
                contentColor = Color.White,
                backgroundColor = Color.Transparent,
                navigationIcon = {
                    PixabayIconButton(
                        onClick = { viewModel.navigateUp() },
                        imageVector = Icons.Filled.Close,
                    )
                },
                actions = {
                    PixabayIconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                } else {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        },
                        imageVector = Icons.Filled.Info,
                        tint = Color.White,
                    )
                }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                content = {
                    InfoTextRecord(stringResource(id = R.string.title_tag), hit.tags)
                    InfoTextRecord(stringResource(id = R.string.title_likes), hit.likes.toString())
                    InfoTextRecord(stringResource(id = R.string.title_downloads), hit.downloads.toString())
                    InfoTextRecord(stringResource(id = R.string.title_comments), hit.comments.toString())
                },
            )
        },
        content = { ZoomableImage(hit.imageUrl) },
    )
}