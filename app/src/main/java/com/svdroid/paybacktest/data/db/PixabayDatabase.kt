package com.svdroid.paybacktest.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.svdroid.paybacktest.data.api.Hit

@Database(entities = [Hit::class, RemoteKeys::class, Suggestion::class], version = 1)
abstract class PixabayDatabase : RoomDatabase() {
    abstract fun hitDao(): HitDao

    abstract fun searchSuggestionDao(): SearchSuggestionDao

    abstract fun getRemoteKeysDao(): RemoteKeysDao
}

@Dao
interface SearchSuggestionDao {
    @Transaction
    fun deleteAndInsert(suggestion: List<Suggestion>) {
        deleteAll()
        insert(suggestion)
    }

    @Query("SELECT * FROM Suggestion ORDER BY _id DESC")
    fun getSuggestions(): List<Suggestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(suggestion: List<Suggestion>)

    @Query("DELETE FROM Suggestion")
    fun deleteAll()
}

@Dao
interface HitDao {
    @Query("SELECT * FROM Hit WHERE search_query = :searchQuery")
    fun getAll(searchQuery: String): PagingSource<Int, Hit>

    @Query("SELECT * FROM Hit WHERE id = :id")
    fun getBy(id: Int): Hit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(hits: List<Hit>)

    @Query("DELETE FROM Hit WHERE search_query = :searchQuery")
    fun clearAll(searchQuery: String)
}

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM RemoteKeys WHERE search_query = :searchQuery ORDER BY created_at DESC LIMIT 1")
    suspend fun getRemoteKeyByHitID(searchQuery: String): RemoteKeys?

    @Query("DELETE FROM RemoteKeys")
    suspend fun clearRemoteKeys()

    @Query("SELECT created_at FROM RemoteKeys WHERE search_query = :searchQuery ORDER BY created_at DESC LIMIT 1")
    suspend fun getCreationTime(searchQuery: String): Long?
}