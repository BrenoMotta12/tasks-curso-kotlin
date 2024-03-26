package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PersonService
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(context: Context) {

    private val taskDatabase = TaskDatabase.getDatabase(context).priorityDAO()
    private val remote = RetrofitClient.getService(PriorityService::class.java)

    fun list(listener: APIListener<List<PriorityModel>>) {
        val call = remote.list()
        call.enqueue(object : Callback<List<PriorityModel>> {

            override fun onResponse(
                call: Call<List<PriorityModel>>, r: Response<List<PriorityModel>>
            ) {
                if (r.code() == TaskConstants.HTTP.SUCCESS) {
                    r.body()?.let { listener.onSuccess(it) }
                } else {
                    listener.onFailure(failResponse(r.errorBody()!!.string()))
                }
            }

            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {
                failResponse("Ocorreu um erro interno no servidor!")
            }

        })
    }



    fun save(list: List<PriorityModel>) {
        taskDatabase.save(list)
    }

    fun listPriorities(): List<PriorityModel> {
        return taskDatabase.list()
    }



    private fun failResponse(error: String): String = Gson().fromJson(error, String::class.java)
}