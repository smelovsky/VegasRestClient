package com.rhonda.vegasrestclient

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhonda.vegasrestclient.restapi.RestApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UserItem(
    var id: Int,
    var name: String,
    var isSelected: Boolean = false,
    var isEditMode: Boolean = false,
)

data class UserBaseItem(
    var id: Int,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val restApiClient: RestApiClient,
) : ViewModel() {

    val url = "http://10.0.2.2:8080/"

    val exitFromApp = mutableStateOf(false)

    var name = mutableStateOf("")
    var surname = mutableStateOf("")
    var phoneNumber = mutableStateOf("")

    var error = mutableStateOf("")
    var responseCode = mutableStateOf(0)
    var showError = mutableStateOf(false)

    var showSysAdminInfo = mutableStateOf(false)

    ////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        getUsers()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun restApiClientGetUserError() {
        Log.d("zzz", "errorMessage (${restApiClient.errorMessage})")
        error.value = restApiClient.errorMessage
        showError.value = true

        error.value = restApiClient.errorMessage
        responseCode.value = restApiClient.responseCode
        showError.value = true
        showSysAdminInfo.value = false

    }

    fun getSysAdmin() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                restApiClient.getUser(url, 0, ::restApiClientGetUserError) {
                    showSysAdminInfo.value = true
                    name.value = restApiClient.name
                    surname.value = restApiClient.surname
                    phoneNumber.value = restApiClient.phoneNumber

                }
            }
        }
    }

    fun getUsers() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                restApiClient.getUsers(url, ::restApiClientGetUserError) {

                    restApiClient.users.forEach() {
                        userListBase += UserBaseItem(it.id)
                        userListDetails.add(UserItem(it.id, "${it.name}"))
                    }

                }
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                restApiClient.deleteUser(url, id, ::restApiClientGetUserError) {

                }
            }
        }
    }

    fun updateUser(id: Int, name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                restApiClient.updateUser(url, id, name, ::restApiClientGetUserError) {

                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    var userListBase by mutableStateOf(listOf<UserBaseItem>())
    var userListDetails = mutableListOf<UserItem>()

    fun isEditMode() : Boolean {

        userListDetails.forEach() {
            if (it.isEditMode) return true
        }
        return false
    }

    fun selectItemAndSetEditMode(index: Int) {

        var i = 0
        while(i  <  userListDetails.size){
            if (index == i) {
                userListDetails[i].isSelected = true
                userListDetails[i].isEditMode = true
            } else {
                userListDetails[i].isSelected = false
                userListDetails[i].isEditMode = false
            }
            i++
        }


        val list = userListBase
        userListBase = listOf<UserBaseItem>()
        userListBase = list
    }

    fun selectItem(index: Int) {

        var i = 0
        while(i  <  userListDetails.size){
            userListDetails[i].isSelected = if (index == i) {
                !userListDetails[i].isSelected
            } else {
                false
            }
            i++
        }

        val list = userListBase
        userListBase = listOf<UserBaseItem>()
        userListBase = list
    }

    fun changeEditMode(index: Int) {

        userListDetails[index].isEditMode = !userListDetails[index].isEditMode

        val list = userListBase
        userListBase = listOf<UserBaseItem>()
        userListBase = list
    }

    fun renameItem(index: Int, name: String) {

        userListDetails[index].isEditMode = false
        userListDetails[index].name = name

        val list = userListBase
        userListBase = listOf<UserBaseItem>()
        userListBase = list

        updateUser(userListDetails[index].id, userListDetails[index].name)

    }

    fun deleteSelectedItem() {

        val index = getSelectedItem()

        if (index != -1) {

            val id = userListDetails[index].id

            userListBase -= userListBase[index]
            userListDetails.removeAt(index)

            val list = userListBase
            userListBase = listOf<UserBaseItem>()
            userListBase = list

            //deleteUser(id) // it looks @DELETE not supported on server side

        }
    }

    fun getSelectedItem() : Int {
        var i = 0
        while(i  <  userListDetails.size){
            if (userListDetails[i].isSelected) { return i }
            i++
        }
        return -1
    }


}
