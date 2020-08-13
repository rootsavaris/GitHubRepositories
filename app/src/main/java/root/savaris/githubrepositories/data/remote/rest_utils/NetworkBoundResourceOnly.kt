/*
 * Copyright 2018-2020 Andrius Baruckis www.baruckis.com | kriptofolio.app
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package root.savaris.githubrepositories.data.remote.rest_utils

import android.os.Handler
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType> - Type for the Resource data.
 * @param <RequestType> - Type for the API response.
</RequestType></ResultType> */

// It defines two type parameters, ResultType and RequestType,
// because the data type returned from the API might not match the data type used locally.
abstract class NetworkBoundResourceOnly<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    // The final result LiveData.
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {

        val apiResponse = createCall()

        fun fetch() {
            result.addSource(apiResponse) { response ->
                result.removeSource(apiResponse)
                when (response) {
                    is ApiSuccessResponse -> {
                        appExecutors.mainThread().execute {
                            setValue(Resource.successNetwork(transFormCallResult(processResponse(response))))
                        }
                    }
                    is ApiEmptyResponse -> {
                        appExecutors.mainThread().execute {
                            setValue(Resource.successDb(null))
                        }
                    }
                    is ApiErrorResponse -> {
                        onFetchFailed()
                        appExecutors.mainThread().execute {
                            setValue(Resource.error(response.errorMessage, null))
                        }
                    }
                }
            }
        }

        // Add delay before call if needed.
        val delay = fetchDelayMillis()
        if (delay > 0) {
            Handler().postDelayed({ fetch() }, delay)
        } else fetch()

    }

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected open fun onFetchFailed() {}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract fun transFormCallResult(item: RequestType): ResultType?

    // Make a call to the server after some delay for better user experience.
    protected open fun fetchDelayMillis(): Long = 0

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}
