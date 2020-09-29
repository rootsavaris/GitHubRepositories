package root.savaris.githubrepositories.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import root.savaris.githubrepositories.BASE_URL
import root.savaris.githubrepositories.framework.network.RepositoriesServiceApi
import java.lang.Exception

val networkModule = module {
    factory { provideGson() }
    factory { provideConverterFactory(get())}
    factory { provideOkHttpClient() }
    single { provideRetrofit(get(), get()) }
    factory { provideServiceApi(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
    return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
        .addConverterFactory(gsonConverterFactory).build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder()
        .addInterceptor(Interceptor { chain ->

        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code / 100) {

            2 -> {
                return@Interceptor response
            }
            else -> {

                val exception: Exception?

                try{

                    val responseMessage = response.use { it.body?.byteStream()?.reader().use { reader -> reader?.readText() } }
                    exception = Exception("code " + response.code + responseMessage?: "")

                } catch (exception: Exception){
                    exception.printStackTrace()
                    return@Interceptor response
                }

                throw exception

            }
        }
    })
    .build()
}

fun provideGson(): Gson {
    return GsonBuilder().create()
}

fun provideConverterFactory(gson: Gson): GsonConverterFactory {
    return GsonConverterFactory.create(gson)
}

fun provideServiceApi(retrofit: Retrofit): RepositoriesServiceApi = retrofit.create(RepositoriesServiceApi::class.java)
