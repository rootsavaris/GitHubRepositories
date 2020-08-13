package root.savaris.githubrepositories.data.remote

import androidx.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import root.savaris.githubrepositories.PAGE_PARAM_API
import root.savaris.githubrepositories.PER_PAGE_PARAM_API
import root.savaris.githubrepositories.QUERY_PARAM_API
import root.savaris.githubrepositories.SORT_PARAM_API
import root.savaris.githubrepositories.data.remote.model.RepositoryRemoteItemEntity
import root.savaris.githubrepositories.data.remote.model.RepositoryList
import root.savaris.githubrepositories.data.remote.rest_utils.ApiResponse

interface RepositoriesServiceApi {

    @GET("search/repositories")
    fun getRepositories(@Query("q") query: String? = QUERY_PARAM_API,
                         @Query("sort") sort: String? = SORT_PARAM_API,
                         @Query("per_page") perPage: Int? = PER_PAGE_PARAM_API,
                         @Query("page") page: Int? = PAGE_PARAM_API): LiveData<ApiResponse<RepositoryList>>

    @GET("repos/{owner}/{repository}")
    fun getRepository(@Path("owner") owner: String,
                      @Path("repository") repository: String): LiveData<ApiResponse<RepositoryRemoteItemEntity>>

}