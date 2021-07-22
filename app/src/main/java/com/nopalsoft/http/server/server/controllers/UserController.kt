package com.nopalsoft.http.server.server.controllers

import com.nopalsoft.http.server.server.model.User
import com.nopalsoft.http.server.server.model.ResponseBase
import com.nopalsoft.http.server.server.services.UserService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.userController() {

    val userService by inject<UserService>()

    get("/user") {
        call.respond(ResponseBase(data = userService.userList()))
    }

    post("/user") {
        val person = call.receive<User>()
        call.respond(ResponseBase(data = userService.addUser(person)))
    }

    delete("/user/{id}") {
        val id = call.parameters["id"]?.toInt()!! // Force just for this example
        call.respond(ResponseBase(data = userService.removeUser(id)))
    }
}