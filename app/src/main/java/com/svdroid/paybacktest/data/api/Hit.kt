package com.svdroid.paybacktest.data.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Hit(
    @ColumnInfo(name = "comments")
    @SerializedName("comments")
    val comments: Int,
    @ColumnInfo(name = "downloads")
    @SerializedName("downloads")
    val downloads: Int,
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @ColumnInfo(name = "largeImageURL")
    @SerializedName("largeImageURL")
    val largeImageURL: String?,
    @ColumnInfo(name = "likes")
    @SerializedName("likes")
    val likes: Int,
    @ColumnInfo(name = "previewURL")
    @SerializedName("previewURL")
    val previewURL: String?,
    @ColumnInfo(name = "tags")
    @SerializedName("tags")
    val tags: String?,
    @ColumnInfo(name = "user")
    @SerializedName("user")
    val user: String?,
    @ColumnInfo(name = "search_query")
    val searchQuery: String?
)