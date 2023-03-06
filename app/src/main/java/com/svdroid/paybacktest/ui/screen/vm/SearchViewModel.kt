package com.svdroid.paybacktest.ui.screen.vm

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.svdroid.paybacktest.data.db.SearchSuggestionDao
import com.svdroid.paybacktest.data.db.Suggestion
import com.svdroid.paybacktest.utils.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSuggestionDao: SearchSuggestionDao,
    private val navController: NavHostController
) : ViewModel() {
    private val searchSuggestions = searchSuggestionDao.getSuggestions().toMutableList()

    fun getSuggestions(): List<Suggestion> {
        return searchSuggestionDao.getSuggestions()
    }

    fun handleQuery(rowQuery: String) {
        val query = rowQuery.trim()

        if (query.isBlank()) {
            return
        }

        handleSuggestion(query)
    }

    fun handleSuggestion(query: String) {
        searchSuggestions.apply {
            removeIf { it.query == query }
            add(0, Suggestion(query = query))
        }
        searchSuggestionDao.deleteAndInsert(searchSuggestions.take(7))

        navigateToList(query)
    }

    fun handleBackPress(prevQuery: String) {
        navigateToList(prevQuery)
    }

    private fun navigateToList(query: String) {
        navController.navigate(Destination.Main.createRoute(query)) {
            popUpTo(Destination.Main.route) {
                saveState = true
                inclusive = true
            }
        }
    }
}