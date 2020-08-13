package root.savaris.githubrepositories.data.remote.rest_utils

import retrofit2.Response

internal const val UNKNOWN_CODE = -1

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body)
                }
            } else {
                ApiErrorResponse(response.code(), response.errorBody()?.string()?:response.message())
            }
        }

        fun <T> create(errorCode: Int, error: String): ApiErrorResponse<T> {
            return ApiErrorResponse(errorCode, error)
        }

        fun <T> create(errorCode: Int, throwable: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(errorCode, throwable.message ?: "Error")
        }

        fun <T> create(throwable: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(0, throwable.message ?: "Error")
        }

        fun <T> create(response: T): ApiResponse<T> {
            return ApiSuccessResponse(response)
        }

        fun <T> create(apiResponse: ApiResponse<T>): ApiResponse<T> {
            return apiResponse
        }

    }
}

class ApiEmptyResponse<T> : ApiResponse<T>()
data class ApiErrorResponse<T>(val errorCode: Int, val errorMessage: String): ApiResponse<T>()
data class ApiSuccessResponse<T>(val body: T): ApiResponse<T>()