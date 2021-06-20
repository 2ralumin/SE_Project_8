package com.muleo.soft.control

import androidx.lifecycle.*
import com.muleo.soft.entity.Camp
import com.muleo.soft.entity.CampRepository
import com.muleo.soft.util.Ktor
import kotlinx.coroutines.launch


class CampControl(private val rep: CampRepository) : ViewModel() {

    val allCamps: LiveData<List<Camp>> = rep.allCamps.asLiveData()

    fun addOrUpdate(vararg camps: Camp) = viewModelScope.launch {
        rep.addOrUpdate(camps = camps)
    }

    val checkCamp: MutableLiveData<Camp?> = MutableLiveData()
    fun get(id: Int) {
        viewModelScope.launch {
            checkCamp.value = rep.get(campId = id)
        }
    }

    fun getCampByIdWithNet(campId: Int) {
        viewModelScope.launch {
            try {
                val c = Ktor.campById(campId = campId)
                checkCamp.value = c
                addOrUpdate(c)
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.run {
                    //TODO 에러 메세지를 통해서 무언가를 수행해도됨
                }
            }
        }
    }


    //TODO edit
}

class CampViewModelFactory(private val rep: CampRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampControl::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampControl(rep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
