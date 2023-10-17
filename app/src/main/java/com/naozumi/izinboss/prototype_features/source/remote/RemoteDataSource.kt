package com.naozumi.izinboss.prototype_features.source.remote

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.naozumi.izinboss.model.datamodel.LeaveRequest
import com.naozumi.izinboss.prototype_features.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RemoteDataSource private constructor (
    private val firestore: FirebaseFirestore
) {

    suspend fun postLeaveRequestToDatabase(companyId: String, leaveRequest: LeaveRequest): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val leaveRequestCollection = firestore.collection("Companies").document(companyId)
                    .collection("Leave Requests")
                val leaveRequestId = leaveRequestCollection.document().id // Generate a unique ID
                leaveRequest.id = leaveRequestId
                leaveRequest.companyId = companyId

                leaveRequestCollection.document(leaveRequestId).set(leaveRequest).await() // Create the document with the specified ID

                emit(ApiResponse.Success(Unit))
            }  catch (e : Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }  catch (e: FirebaseException) {
                emit(ApiResponse.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(ApiResponse.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllLeaveRequests(companyId: String): Flow<ApiResponse<List<LeaveRequest>>> {
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
            } catch (e : Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }  catch (e: FirebaseException) {
                emit(ApiResponse.Error(e.message.toString()))
            } catch (e: FirebaseFirestoreException) {
                emit(ApiResponse.Error(e.message.toString()))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(service: FirebaseFirestore): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(service)
            }
    }
}