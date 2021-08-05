package com.nopalsoft.http.server.server.repositories

import com.nopalsoft.http.server.server.GeneralException
import com.nopalsoft.http.server.server.model.User

interface UserRepository {
    fun userList(): ArrayList<User>

    fun addUser(user: User): User

    fun removeUser(id: Int): User
}

class UserRepositoryImp : UserRepository {
    private var idCount = 0
    private val userList = ArrayList<User>()

    override fun userList(): ArrayList<User> = userList

    override fun addUser(user: User): User {
        val newUser = user.copy(id = ++idCount);
        userList.add(newUser)
        return newUser
    }

    override fun removeUser(id: Int): User {
        userList.find { it.id == id }?.let {
            userList.remove(it)
            return it
        }
        throw GeneralException("Cannot remove user: $id")
    }

}