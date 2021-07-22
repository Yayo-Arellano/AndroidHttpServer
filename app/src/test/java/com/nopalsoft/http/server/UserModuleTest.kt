package com.nopalsoft.http.server

import com.nopalsoft.http.server.server.repositories.UserRepository
import com.nopalsoft.http.server.server.model.User
import com.nopalsoft.http.server.server.controllers.userController
import com.nopalsoft.http.server.server.services.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.module

// Run all the tests with ./gradlew clean test --info

class UserModuleTest : BaseModuleTest() {
    private val userRepositoryMock: UserRepository = mockk()

    init {
        koinModules = module {
            single { userRepositoryMock }
            single { UserService() }
        }
        moduleList = {
            install(Routing) {
                userController()
            }
        }
    }

    @Test
    fun `Get users return successfully`() = withBaseTestApplication {
        coEvery { userRepositoryMock.userList() } returns arrayListOf(User(1, "Yayo", 28))

        val call = handleRequest(HttpMethod.Get, "/user")

        val response = call.response.parseListBody(User::class.java)

        assertEquals(call.response.status(), HttpStatusCode.OK)
        assertEquals(response.data?.get(0)?.name, "Yayo")
        assertEquals(response.data?.get(0)?.age, 28)
    }

    @Test
    fun `Missing name parameter`() = withBaseTestApplication {
        val call = handleRequest(HttpMethod.Post, "/user") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(toJsonBody(User(age = 27)))
        }
        val response = call.response.parseBody(User::class.java)

        assertEquals(call.response.status(), HttpStatusCode.OK)
        assertEquals(response.data, null)
        assertEquals(response.status, 100)
        assertEquals(response.message.contains("name"), true)
    }

    @Test
    fun `Missing age parameter`() = withBaseTestApplication {
        val call = handleRequest(HttpMethod.Post, "/user") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(toJsonBody(User(name = "Yayo")))
        }
        val response = call.response.parseBody(User::class.java)

        assertEquals(call.response.status(), HttpStatusCode.OK)
        assertEquals(response.data, null)
        assertEquals(response.status, 100)
        assertEquals(response.message.contains("age"), true)
    }

    @Test
    fun `Age under zero error`() = withBaseTestApplication {
        val call = handleRequest(HttpMethod.Post, "/user") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(toJsonBody(User(name = "Yayo", age = -5)))
        }
        val response = call.response.parseBody(User::class.java)

        assertEquals(call.response.status(), HttpStatusCode.OK)
        assertEquals(response.data, null)
        assertEquals(response.status, 999)
        assertEquals(response.message.contains("Age cannot be negative number"), true)
    }
}