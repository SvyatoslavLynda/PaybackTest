package com.svdroid.paybacktest.ui.screen.vm

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.svdroid.paybacktest.data.db.HitDao
import com.svdroid.paybacktest.data.ui.UIHitDetailsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val hitDao: HitDao,
    private val navController: NavHostController
) : ViewModel() {
    fun getHitBy(id: Int): UIHitDetailsModel {
        val hit = hitDao.getBy(id)

        return UIHitDetailsModel(
            imageUrl = hit.largeImageURL,
            userName = hit.user ?: "-/-",
            tags = hit.tags ?: "-/-",
            likes = hit.likes,
            downloads = hit.downloads,
            comments = hit.comments,
        )
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}