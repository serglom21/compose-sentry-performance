package com.example.sentry

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import io.sentry.android.sqlite.SentrySupportSQLiteOpenHelper
import okhttp3.OkHttpClient

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appCtx = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appCtx: Context

        val db by lazy {
            Room.databaseBuilder(
                appCtx,
                AppDatabase::class.java, "database-name",
            )
                // TODO: Room integration auto installation doesn't work?
                .openHelperFactory { configuration ->
                    SentrySupportSQLiteOpenHelper.create(FrameworkSQLiteOpenHelperFactory().create(configuration))
                }
                .build()
        }

        val client by lazy {
            // TODO: response body not logged when `max-request-body-size` is set
            // https://docs.sentry.io/platforms/android/configuration/options/#max-request-body-size

            // TODO: can't see Http call Spans in Sentry Performance. Breadcrumbs are visible though.
            OkHttpClient.Builder()
                .build()
        }
    }
}
