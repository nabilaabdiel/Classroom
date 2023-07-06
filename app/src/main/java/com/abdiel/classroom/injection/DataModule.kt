package com.abdiel.classroom.injection

import android.content.Context
import androidx.databinding.ktx.BuildConfig
import com.abdiel.classroom.api.ApiService
import com.abdiel.classroom.data.constant.Const
import com.abdiel.classroom.data.session.Session
import com.crocodic.core.data.CoreSession
import com.crocodic.core.helper.okhttp.SSLTrust
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    fun provideSession(@ApplicationContext context: Context, gson: Gson) = Session(context, gson)

    @Provides
    fun provideGson() = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    @Provides
    fun providesOkHttpClient(session: Session): OkHttpClient {

        val unsafeTrustManager = SSLTrust().createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(unsafeTrustManager), null)

        val okHttpClient = OkHttpClient().newBuilder()
            .sslSocketFactory(sslContext.socketFactory, unsafeTrustManager)
            .connectTimeout(Const.TIMEOUT.NINETY_LONG, TimeUnit.SECONDS)
            .readTimeout(Const.TIMEOUT.NINETY_LONG, TimeUnit.SECONDS)
            .writeTimeout(Const.TIMEOUT.NINETY_LONG, TimeUnit.SECONDS)

            .addInterceptor {  chain ->
                val original = chain.request()
                val token = session.getString(Const.TOKEN.PREF_TOKEN)
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type","application/json")
                    .method(original.method,original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }

        if(BuildConfig.DEBUG) {
            val interceptors = HttpLoggingInterceptor()
            interceptors.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(interceptors)
        }
        return okHttpClient.build()
    }

    //Note: add base url
    @Provides
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
//            .baseUrl("https://magang.crocodic.net/ki/IlhamM/kelas-ku/public/api/")
            .baseUrl("https://magang.crocodic.net/ki/Afifun/kelasku/public/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)
    }
}