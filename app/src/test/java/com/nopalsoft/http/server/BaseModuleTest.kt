package com.nopalsoft.http.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nopalsoft.http.server.server.handleException
import com.nopalsoft.http.server.server.model.ResponseBase
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.withTestApplication
import java.lang.reflect.Type
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.ktor.ext.Koin

abstract class BaseModuleTest {

    private val gson = Gson()
    protected var koinModules: Module? = null
    protected var moduleList: Application.() -> Unit = { }

    init {
        stopKoin()
    }

    fun <R> withBaseTestApplication(test: TestApplicationEngine.() -> R) {
        withTestApplication({
            install(ContentNegotiation) { gson { } }
            handleException()
            koinModules?.let {
                install(Koin) {
                    modules(it)
                }
            }
            moduleList()
        }) {
            test()
        }
    }

    fun toJsonBody(obj: Any): String = gson.toJson(obj)

    fun <T> TestApplicationResponse.parseBody(clazz: Class<T>): ResponseBase<T> {
        val typeOfT: Type = TypeToken.getParameterized(ResponseBase::class.java, clazz).type
        return gson.fromJson(content, typeOfT)
    }

    fun <T> TestApplicationResponse.parseListBody(clazz: Class<T>): ResponseBase<List<T>> {
        val typeList = TypeToken.getParameterized(List::class.java, clazz).type
        val typeOfT: Type = TypeToken.getParameterized(ResponseBase::class.java, typeList).type
        return gson.fromJson(content, typeOfT)
    }
}