package com.svdroid.paybacktest.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.svdroid.paybacktest.R
import com.svdroid.paybacktest.ui.screen.vm.ImagesViewModel
import com.svdroid.paybacktest.data.ui.UIHitListModel
import com.svdroid.paybacktest.ui.PixabayIconButton
import com.svdroid.paybacktest.ui.SystemStatusBarPainter
import com.svdroid.paybacktest.ui.noRippleClickable

@Composable
fun MainPage(
    searchQuery: String,
    onVoiceInputClick: () -> Unit
) {
    val viewModel = hiltViewModel<ImagesViewModel>()
    val query = remember { mutableStateOf(searchQuery) }
    val hits = viewModel.searchImages(query.value).collectAsLazyPagingItems()

    SystemStatusBarPainter(Color.White)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchView(
                searchQuery = query,
                onVoiceInputClick = onVoiceInputClick,
                viewModel = viewModel,
            )
        },
        content = {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 8.dp), columns = GridCells.Fixed(2)
            ) {
                items(
                    count = hits.itemCount,
                    key = { index -> index },
                    itemContent = { i -> ImageItem(hit = hits[i], viewModel) }
                )

                when (hits.loadState.refresh) { //First loading
                    is LoadState.Loading -> item { LoadingItem() }
                    else -> {}
                }

                when (hits.loadState.append) { // Pagination
                    is LoadState.Loading -> item { LoadingItem() }
                    else -> {}
                }
            }
        },
    )
}

@Composable
fun SearchView(
    searchQuery: MutableState<String>,
    viewModel: ImagesViewModel,
    onVoiceInputClick: () -> Unit
) {
    TextField(
        value = searchQuery.value,
        onValueChange = {},
        shape = RoundedCornerShape(28.dp),
        placeholder = { Text(stringResource(id = R.string.search_query_placeholder)) },
        colors = TextFieldDefaults.textFieldColors(
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { viewModel.handleSearchClick(searchQuery.value) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        maxLines = 1,
        singleLine = true,
        enabled = false,
        readOnly = true,
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                Icons.Filled.Search.name,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .width(24.dp)
                    .height(24.dp),
            )
        },
        trailingIcon = {
            if (searchQuery.value.isBlank()) PixabayIconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = { onVoiceInputClick.invoke() },
                painter = painterResource(id = R.drawable.ic_microphone_24),
            )
            else PixabayIconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = { searchQuery.value = "" },
                imageVector = Icons.Filled.Clear,
            )
        },
    )
}

@Composable
fun ImageItem(hit: UIHitListModel?, viewModel: ImagesViewModel) {
    Box(contentAlignment = Alignment.BottomEnd) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .height(200.dp)
                .padding(4.dp)
                .clickable { viewModel.handleItemClick(hit?.id) },
            placeholder = painterResource(id = R.drawable.ic_placeholder_24),
            error = painterResource(id = R.drawable.ic_error_placeholder_24),
            model = hit?.imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = hit?.tags
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.75f))
                .padding(8.dp),
            content = {
                hit?.apply {
                    Text(
                        text = userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                    )

                    Text(
                        text = tags,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                    )
                }
            }
        )
    }
}

@Composable
fun LoadingItem() {
    Box(modifier = Modifier.height(150.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(id = R.string.title_loading))

            CircularProgressIndicator(color = Color.Black)
        }
    }
}