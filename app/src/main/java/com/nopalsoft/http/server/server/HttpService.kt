package com.nopalsoft.http.server.server

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.nopalsoft.http.server.server.controllers.userController
import com.nopalsoft.http.server.server.repositories.UserRepository
import com.nopalsoft.http.server.server.repositories.UserRepositoryImp
import com.nopalsoft.http.server.server.services.UserService

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.netty.util.internal.logging.InternalLoggerFactory
import io.netty.util.internal.logging.JdkLoggerFactory
import org.koin.dsl.module
import org.koin.ktor.ext.Koin


// Forward https://stackoverflow.com/a/14684485/3479489
const val PORT = 8080

class HttpService : Service() {
    override fun onCreate() {
        super.onCreate()
        Thread {
            InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE)
            embeddedServer(Netty, PORT) {
                install(ContentNegotiation) { gson {} }
                handleException()
                install(Koin) {
                    modules(
                        module {
                            single<UserRepository> { UserRepositoryImp() }
                            single { UserService() }
                        }
                    )
                }
                install(Routing) {
                    userController()
                }
            }.start(wait = true)
        }.start()
    }

    override fun onBind(intent: Intent): IBinder? = null
}

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootCompletedReceiver", "starting service HttpService...")
            context.startService(Intent(context, HttpService::class.java))
        }
    }
}
