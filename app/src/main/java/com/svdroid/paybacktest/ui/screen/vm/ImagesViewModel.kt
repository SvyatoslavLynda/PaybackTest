package com.svdroid.paybacktest.ui.screen.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.svdroid.paybacktest.data.ImagesRepository
import com.svdroid.paybacktest.data.ui.UIHitListModel
import com.svdroid.paybacktest.utils.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val repository: ImagesRepository,
    private val navController: NavHostController
) : ViewModel() {
    fun searchImages(query: String): Flow<PagingData<UIHitListModel>> {
        val searchQuery = query.ifBlank { "fruits" }

        return repository.searchImages(searchQuery).cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { hit ->
                UIHitListModel(
                    id = hit.id,
                    imageUrl = hit.previewURL,
                    userName = hit.user ?: "-/-",
                    tags = hit.tags ?: "-/-",
                )
            }
        }
    }

    fun handleSearchClick(searchQuery: String) {
        navController.navigate(Destination.Search.createRoute(searchQuery))
    }

    fun handleItemClick(hitId: Int?) {
        navController.navigate(Destination.ConfirmNavigationToDetails.createRoute(hitId))
    }
}