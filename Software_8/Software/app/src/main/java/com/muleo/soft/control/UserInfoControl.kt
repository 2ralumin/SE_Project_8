package com.muleo.soft.control

import androidx.lifecycle.*
import com.muleo.soft.entity.User
import com.muleo.soft.entity.UserRepository
import kotlinx.coroutines.launch

class UserInfoControl(private val rep: UserRepository) : ViewModel() {

    val allUsers: LiveData<List<User>> = rep.allUsers.asLiveData()

    fun add(user: User) = viewModelScope.launch {
        rep.add(user)
    }

    val checkUser: MutableLiveData<User?> = MutableLiveData<User?>()
    fun get(id: String) {
        viewModelScope.launch {
            checkUser.value = rep.get(id)
        }
    }

    var canSignUp = false

    //TODO edit

}

class UserViewModelFactory(private val rep: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserInfoControl::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserInfoControl(rep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
