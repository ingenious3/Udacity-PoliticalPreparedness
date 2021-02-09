package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElection(election: Election)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertElections(elections: List<Election>)

    @Query("SELECT * FROM ${Election.TABLE_NAME}")
    fun getAllElections(): List<Election>

    @Query("SELECT * FROM ${Election.TABLE_NAME} where isSaved = 1")
    fun getSavedElections(): List<Election>

    @Query("SELECT * FROM ${Election.TABLE_NAME} WHERE id=:id")
    fun getElectionById(id: Int): Election

    @Query("DELETE FROM ${Election.TABLE_NAME} where id=:id")
    fun deleteElectionById(id: Int)

    @Query("DELETE FROM ${Election.TABLE_NAME}")
    fun deleteAllElections()
}