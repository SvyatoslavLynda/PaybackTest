package com.svdroid.paybacktest.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svdroid.paybacktest.R
import com.svdroid.paybacktest.ui.screen.vm.SearchViewModel
import com.svdroid.paybacktest.ui.PixabayIconButton
import com.svdroid.paybacktest.ui.SystemStatusBarPainter
import kotlinx.coroutines.android.awaitFrame

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(searchQuery: String) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val suggestions = viewModel.getSuggestions()
    val searchText = remember { mutableStateOf(TextFieldValue(searchQuery, TextRange(searchQuery.length))) }
    val keyboardController = LocalSoftwareKeyboardController.current

    SystemStatusBarPainter(Color.Gray)

    BackHandler {
        viewModel.handleBackPress(searchQuery)
    }

    Scaffold(
        topBar = {
            SearchBar(
                searchQuery,
                searchText,
                viewModel,
                keyboardController = keyboardController,
            )
        },
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                if (suggestions.isNotEmpty()) {
                    val filteredSuggestions =
                        (if (searchText.value.text.isBlank()) suggestions else suggestions.filter {
                            it.query.startsWith(searchText.value.text)
                        })

                    LazyColumn(content = {
                        items(filteredSuggestions.size) {
                            val suggestion = filteredSuggestions[it]

                            SuggestionItem(
                                modifier = Modifier.clickable {
                                    keyboardController?.hide()
                                    viewModel.handleSuggestion(suggestion.query)
                                },
                                item = suggestion.query,
                                hasDivider = it < filteredSuggestions.size - 1,
                            )
                        }
                    })
                }
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    prevQuery: String,
    searchText: MutableState<TextFieldValue>,
    viewModel: SearchViewModel,
    keyboardController: SoftwareKeyboardController?,
) {
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            PixabayIconButton(onClick = { viewModel.handleBackPress(prevQuery) }, imageVector = Icons.Filled.ArrowBack)
        },
        actions = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .focusRequester(focusRequester),
                value = searchText.value,
                onValueChange = { value -> searchText.value = value },
                placeholder = { Text(text = stringResource(id = R.string.search_query_placeholder)) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.Transparent,
                    cursorColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                ),
                trailingIcon = {
                    if (searchText.value.text.isNotBlank()) PixabayIconButton(
                        onClick = { searchText.value = TextFieldValue("") },
                        imageVector = Icons.Filled.Clear,
                    )
                },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()

                    viewModel.handleQuery(searchText.value.text)
                }),
            )
        },
        backgroundColor = Color.Gray
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        awaitFrame()
        keyboardController?.show()
    }
}

@Composable
fun SuggestionItem(modifier: Modifier = Modifier, item: String, hasDivider: Boolean = false) {
    Text(item, modifier = modifier.fillMaxWidth().padding(16.dp), fontSize = 16.sp)
    if (hasDivider) Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
}