package com.naozumi.izinboss.prototype_features

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.model.helper.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RemoteDataSource private constructor(private val firestore: FirebaseFirestore) {
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(service: FirebaseFirestore): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(service)
            }
    }

    suspend fun getLeaveRequests(companyId: String): Flow<ApiResponse<List<LeaveRequest>>> {
        //get data from remote api
        return flow {
            try {
                val leaveRequestList = mutableListOf<LeaveRequest>()
                val leaveRequestCollection = firestore.collection("Companies").document(companyId).collection("Leave Requests")
                val response = leaveRequestCollection.get().await()

                if (response.isEmpty){
                    emit(ApiResponse.Empty)
                } else {
                    for (document in response) {
                        val leaveRequest = document.toObject(LeaveRequest::class.java)
                        leaveRequestList.add(leaveRequest)
                    }
                    emit(ApiResponse.Success(leaveRequestList))
                }
            } catch (e : Exception){
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }  catch (e: FirebaseException) {
                emit(ApiResponse.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(ApiResponse.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO) // Put work on IO-specific Threads
    }

    /*
    suspend fun getAllLeaveRequests(companyId: String): LiveData<Result<List<LeaveRequest>>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val leaveRequestList = mutableListOf<LeaveRequest>()
                val leaveRequestCollection = firestore.collection("Companies").document(companyId).collection("Leave Requests")
                val leaveRequestQuery = leaveRequestCollection.get().await()

                if (leaveRequestQuery.isEmpty) {
                    // If the collection is empty, return an empty list
                    emit(Result.Success(emptyList()))
                } else {
                    for (document in leaveRequestQuery) {
                        val leaveRequest = document.toObject(LeaveRequest::class.java)
                        leaveRequestList.add(leaveRequest)
                    }

                    emit(Result.Success(leaveRequestList))
                }
            } catch (e: FirebaseException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(Result.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

     */
}