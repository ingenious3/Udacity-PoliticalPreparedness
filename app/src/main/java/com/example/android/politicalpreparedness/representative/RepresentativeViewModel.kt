package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch


class RepresentativeViewModel(application: Application) : AndroidViewModel(application) {

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address?>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun getRepresentatives() {
        if (address.value != null) {
            viewModelScope.launch {
                try {
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address.value!!.toFormattedString()).await()
                    _representatives.postValue(offices.flatMap { office -> office.getRepresentatives(officials) })
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setAddress(address: Address?) {
        _address.postValue(address)
    }
    
}
