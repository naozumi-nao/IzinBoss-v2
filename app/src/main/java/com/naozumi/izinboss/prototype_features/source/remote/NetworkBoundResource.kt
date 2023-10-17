package com.naozumi.izinboss.prototype_features.source.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import com.naozumi.izinboss.model.helper.Result
import com.naozumi.izinboss.prototype_features.ApiResponse
import kotlinx.coroutines.flow.emitAll

abstract class NetworkBoundResource<ResultType, RequestType> {
    private var result: Flow<Result<ResultType>> = flow {
        emit(Result.Loading)
        val dbSource = loadFromDB().first()
        if (shouldFetch(dbSource)) {
            emit(Result.Loading)
            when (val apiResponse = createCall().first()) {
                is ApiResponse.Success -> {
                    saveCallResult(apiResponse.data)
                    emitAll(loadFromDB().map { Result.Success(it) })
                }
                is ApiResponse.Empty -> {
                    emitAll(loadFromDB().map { Result.Success(it) })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emit(Result.Error(apiResponse.errorMessage))
                }
            }
        } else {
            emitAll(loadFromDB().map { Result.Success(it) })
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract suspend fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    protected abstract suspend fun saveCallResult(data: RequestType)

    fun asFlow(): Flow<Result<ResultType>> = result
}