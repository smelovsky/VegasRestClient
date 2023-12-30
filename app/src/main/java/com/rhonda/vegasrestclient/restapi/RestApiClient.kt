package com.rhonda.vegasrestclient.restapi

import android.util.Log
import com.rhonda.vegasrestclient.UserItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

object RestApiClient  {

    ////////////////////////////////////////////////////////////////////////////////////////////////

    data class GetUserResponse(
        val id: Int = 0,
        val name: String? = null,
        val surname: String? = null,
        val phoneNumber: String? = null,
    )

    interface getUserService {
        @GET("users/")
        fun getUser(@Query("id") id: Int): retrofit2.Call<GetUserResponse>

        //@GET("users/{id}")
        //fun getUser(@Path("id") id: Int): retrofit2.Call<GetUserResponse>
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    interface getUsersService {

        @GET("users/{path}")
        fun getUsers(@Path("path") path: String): retrofit2.Call<List<RestApiClient.GetUserResponse>>
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    interface deleteUsersService {

        @DELETE("users/")
        fun deleteUser(@Query("id") id: Int): retrofit2.Call<GetUserResponse>

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    interface updateUsersService {

        @PUT("users/update")
        fun updateUser(@Body requestBody: GetUserResponse): retrofit2.Call<GetUserResponse>

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    var responseMessage = ""
    var errorMessage = ""
    var responseCode = 0

    var name = ""
    var surname = ""
    var phoneNumber = ""

    var users = mutableListOf<GetUserResponse>()

    fun getUser(url: String, id: Int, onError: () -> Unit, onGetUserResponse: () -> Unit) {

        lateinit var call: Call<GetUserResponse>


        var restApiClientRetrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()

        var service = restApiClientRetrofit.create(getUserService::class.java)


        call = service.getUser(id)

        call.enqueue(

            object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    responseMessage = response.message()
                    responseCode = response.code()

                    Log.d("zzz", "${response}")

                    if (response.code() == 200) {
                        response.body()?.let {
                            //Log.d("zzz", "name ${it.name}")
                            name = it.name!!
                            surname = it.surname!!
                            phoneNumber = it.phoneNumber!!
                        }

                        onGetUserResponse()

                    } else {
                        errorMessage = responseMessage
                        onError()
                    }


                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {

                    Log.d("zzz", "onFailure ${call}")
                    Log.d("zzz", "onFailure ${t.message.toString()}")

                    errorMessage = t.message.toString()
                    onError()
                }
            }
        )

    }

    fun getUsers(url: String, onError: () -> Unit, onGetUsersResponse: () -> Unit) {
        lateinit var call: Call<List<RestApiClient.GetUserResponse>>


        var retroInstance: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()

        var service = retroInstance.create(RestApiClient.getUsersService::class.java)

        call = service.getUsers("list")

        call.enqueue(

            object : Callback<List<RestApiClient.GetUserResponse>> {
                override fun onResponse(
                    call: Call<List<RestApiClient.GetUserResponse>>,
                    response: Response<List<RestApiClient.GetUserResponse>>
                ) {
                    RestApiClient.responseMessage = response.message()
                    RestApiClient.responseCode = response.code()

                    Log.d("zzz", "${response}")

                    if (response.code() == 200) {
                        response.body()?.let {
                            users = it as MutableList<GetUserResponse>
                            //users.forEach() {
                            //    Log.d("zzz", "name ${it.name}")
                            //}
                            //Log.d("zzz", "name ${it}")
                        }

                        onGetUsersResponse()

                    } else {
                        RestApiClient.errorMessage = RestApiClient.responseMessage
                        onError()
                    }


                }

                override fun onFailure(call: Call<List<RestApiClient.GetUserResponse>>, t: Throwable) {

                    Log.d("zzz", "onFailure ${call}")
                    Log.d("zzz", "onFailure ${t.message.toString()}")

                    RestApiClient.errorMessage = t.message.toString()
                    onError()
                }
            }
        )
    }

    fun deleteUser(url: String, id: Int, onError: () -> Unit, onDeleteUserResponse: () -> Unit) {

        lateinit var call: Call<GetUserResponse>


        var restApiClientRetrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()

        var service = restApiClientRetrofit.create(deleteUsersService::class.java)


        call = service.deleteUser(id)

        call.enqueue(

            object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    responseMessage = response.message()
                    responseCode = response.code()

                    Log.d("zzz", "${response}")

                    if (response.code() == 200) {
                        response.body()?.let {
                            Log.d("zzz", "response ${it}")
                        }

                        onDeleteUserResponse()

                    } else {
                        errorMessage = responseMessage
                        onError()
                    }


                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {

                    Log.d("zzz", "onFailure ${call}")
                    Log.d("zzz", "onFailure ${t.message.toString()}")

                    errorMessage = t.message.toString()
                    onError()
                }
            }
        )

    }

    fun updateUser(url: String, id: Int, name: String, onError: () -> Unit, onUpdateUserResponse: () -> Unit) {

        lateinit var call: Call<GetUserResponse>


        var restApiClientRetrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()

        var service = restApiClientRetrofit.create(updateUsersService::class.java)

        call = service.updateUser(GetUserResponse(id, name,"", ""))

        call.enqueue(

            object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    responseMessage = response.message()
                    responseCode = response.code()

                    Log.d("zzz", "${response}")

                    if (response.code() == 200) {
                        response.body()?.let {
                            Log.d("zzz", "name ${it.name}")
                            //name = it.name!!
                            //surname = it.surname!!
                            //phoneNumber = it.phoneNumber!!
                        }

                        onUpdateUserResponse()

                    } else {
                        errorMessage = responseMessage
                        onError()
                    }
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {

                    Log.d("zzz", "onFailure ${call}")
                    Log.d("zzz", "onFailure ${t.message.toString()}")

                    errorMessage = t.message.toString()
                    onError()
                }
            }
        )
    }

}