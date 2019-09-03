package com.infinitevoid.easymessenger.presenters

import com.infinitevoid.easymessenger.contracts.LatestMessagesContract
import com.infinitevoid.easymessenger.data.ChatMessage
import com.infinitevoid.easymessenger.data.User
import com.infinitevoid.easymessenger.models.LatestMessagesModel

class LatestMessagesPresenter(private val view: LatestMessagesContract.View) :
    LatestMessagesContract.Presenter, LatestMessagesContract.OnDataReady {
    private val model = LatestMessagesModel(this)

    override fun fetchCurrentUser() {
        model.fetchCurrentUser()
    }

    override fun setMessageRead(message: ChatMessage) {
        model.setMessageRead(message)
    }

    override fun setListenerForLatest() {
        model.setListenerForLatest()
    }

    override fun setPersonalNotificationListener(message: ChatMessage) {
        model.setPersonalNotificationListener(message)
    }

    override fun setNotification(chatMessage: ChatMessage, user: User) {
        view.setNotification(chatMessage, user)
    }

    override fun onLatestChanged(chatMessage: ChatMessage, key: String) {
        view.onLatestChanged(chatMessage, key)
    }

    override fun onLatestAdded(chatMessage: ChatMessage, key: String) {
        view.onLatestAdded(chatMessage, key)
    }

    override fun verifyIsLogged() {
        if (model.verifyIsLogged()) {
            view.isLogged()
        } else {
            view.isNotLogged()
        }
    }

    override fun signOut() {
        model.signOut()
        view.onSignOut()
    }

    override fun sendUser(user: User?) {
        view.initializeUser(user)
    }
}