package root.savaris.githubrepositories.data.remote

import androidx.lifecycle.LiveData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import root.savaris.githubrepositories.BASE_URL
import root.savaris.githubrepositories.HTTP_LOG_INTERCEPTOR
import root.savaris.githubrepositories.data.remote.adapter.LiveDataCallAdapterFactory
import root.savaris.githubrepositories.data.remote.model.RepositoryRemoteItemEntity
import root.savaris.githubrepositories.data.remote.model.RepositoryList
import root.savaris.githubrepositories.data.remote.rest_utils.*

class NetworkService {

    private var interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().also {
        it.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    var client = OkHttpClient.Builder().also {
        if (HTTP_LOG_INTERCEPTOR) it.addInterceptor(interceptor)
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()

    private val service = retrofit.create(RepositoriesServiceApi::class.java)

    fun getRepositories(page: Int): LiveData<ApiResponse<RepositoryList>>{
        return service.getRepositories(page = page)
    }

    fun getRepository(owner: String, repository: String): LiveData<ApiResponse<RepositoryRemoteItemEntity>>{
        return service.getRepository(owner, repository)
    }

}
