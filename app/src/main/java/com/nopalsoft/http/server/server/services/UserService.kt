package com.nopalsoft.http.server.server.services

import com.nopalsoft.http.server.server.GeneralException
import com.nopalsoft.http.server.server.MissingParamsException
import com.nopalsoft.http.server.server.model.User
import com.nopalsoft.http.server.server.repositories.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class UserService : KoinComponent {

    private val userRepository by inject<UserRepository>()

    fun userList(): List<User> = userRepository.userList()

    fun addUser(user: User): User {
        if (user.name == null)
            throw MissingParamsException("name")
        if (user.age == null)
            throw MissingParamsException("age")
        if (user.age < 0)
            throw GeneralException("Age cannot be negative number")
        return userRepository.addUser(user)
    }

    fun removeUser(id: Int): User = userRepository.removeUser(id)
}