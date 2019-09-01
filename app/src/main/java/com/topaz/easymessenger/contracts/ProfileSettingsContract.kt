package com.topaz.easymessenger.contracts

import android.net.Uri
import android.widget.ImageView
import com.topaz.easymessenger.data.User

interface ProfileSettingsContract {
    interface View {
        fun initializeViewWithUser(user: User)
        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun fetchUser()
        fun loadPic(view: ImageView, uri: String?)
        fun setNewAvatar(uri: Uri?)
        fun setNewUsername(username: String)
    }

    interface Model {
        fun fetchUser()
        fun loadPic(view: ImageView, uri: String?)
        fun setNewAvatar(uri: Uri?)
        fun setNewUsername(username: String)
    }

    interface OnDataReady {
        fun onUserFetched(user: User)
    }
}