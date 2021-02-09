package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.util.*

class ElectionsRepository(val database: ElectionDatabase) {

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            val electionResponse: ElectionResponse = CivicsApi.retrofitService.getElections().await()
            database.electionDao.insertElections(electionResponse.elections)
        }
    }

    suspend fun getVoterInfo(electionId: Int, address: String): VoterInfoResponse? {
        var voterInfo : VoterInfoResponse? = null
        try {
            withContext(Dispatchers.IO) {
                val voterInfoResponse: VoterInfoResponse = CivicsApi.retrofitService.getVoterInfo(electionId, address).await()
                voterInfo = voterInfoResponse
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return voterInfo
    }

    suspend fun insertElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insertElection(election)
        }
    }

    suspend fun getSavedElections(): List<Election>? {
        var savedElections:List<Election>? = null
        withContext(Dispatchers.IO) {
            savedElections = database.electionDao.getSavedElections()
        }
        return savedElections
    }

    suspend fun getUpcomingElections(): List<Election>? {
        var upcomingElections: List<Election>? = null
        withContext(Dispatchers.IO) {
            upcomingElections = database.electionDao.getAllElections()
        }
        return upcomingElections
    }

    suspend fun getElectionById(id: Int) : Election? {
        var electionById: Election? = null
        withContext(Dispatchers.IO) {
            electionById = database.electionDao.getElectionById(id)
        }
        return electionById
    }

}