package com.svdroid.paybacktest.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Suggestion(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    @ColumnInfo(name = "query") val query: String
)
