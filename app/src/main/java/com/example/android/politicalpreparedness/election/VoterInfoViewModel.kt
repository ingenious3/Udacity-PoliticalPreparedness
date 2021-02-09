package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)
    private val repository = ElectionsRepository(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    fun getVoterInfo(electionId: Int, address: String) =
            viewModelScope.launch {
                _voterInfo.postValue(repository.getVoterInfo(electionId, address))
            }

    fun getElectionById(electionId: Int) = viewModelScope.launch { _election.postValue(repository.getElectionById(electionId)) }

    fun saveElection(election: Election) {
        election.isSaved = !election.isSaved
        viewModelScope.launch {
            repository.insertElection(election)
            _election.postValue(election)
        }
    }

    fun openUrl(url: String) {
        _url.value = url
    }

    fun openUrlFinished() {
        _url.value = null
    }

}