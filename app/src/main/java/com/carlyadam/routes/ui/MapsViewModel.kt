package com.carlyadam.routes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlyadam.routes.data.api.Result
import com.carlyadam.routes.data.api.responses.GoogleApiResponse
import com.carlyadam.routes.repository.MapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val mapsRepository: MapsRepository
) :
    ViewModel() {

    private var mapsJob: Job? = null

    private val _responseLiveData = MutableLiveData<GoogleApiResponse>()
    val responseLiveData: LiveData<GoogleApiResponse> get() = _responseLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    fun routes(
        origin: String
    ) {
        mapsJob = viewModelScope.launch {
            when (val result = mapsRepository.routes(origin)) {
                is Result.Success -> _responseLiveData.postValue(result.data)
                is Result.Error -> _errorLiveData.postValue(result.exception.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mapsJob?.cancel()
    }


}