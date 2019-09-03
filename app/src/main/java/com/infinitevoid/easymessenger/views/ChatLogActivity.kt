package com.infinitevoid.easymessenger.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.infinitevoid.easymessenger.R
import com.infinitevoid.easymessenger.contracts.ChatLogContract
import com.infinitevoid.easymessenger.adapters.ChatFromItem
import com.infinitevoid.easymessenger.adapters.ChatToItem
import com.infinitevoid.easymessenger.data.User
import com.infinitevoid.easymessenger.data.ChatMessage
import com.infinitevoid.easymessenger.presenters.ChatLogPresenter
import com.infinitevoid.easymessenger.utils.Constants
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity(), ChatLogContract.View {
    private lateinit var chatPartner: User
    private var latestMessage: ChatMessage? = null
    private val adapter = GroupAdapter<ViewHolder>()
    private val presenter = ChatLogPresenter(this)
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        chatPartner = intent.getParcelableExtra(Constants.USER_KEY)
        latestMessage = intent.getParcelableExtra(Constants.MESSAGE_KEY)
        my_toolbar.title = chatPartner.username

        chat_log_recycler.adapter = adapter

        presenter.setListenerForMessages(chatPartner.uid)

        send.setOnClickListener {
            if (((selectedImageUri?.toString() == "") || (selectedImageUri == null)) && (chat_log.text.toString() == "")) {
                Toast.makeText(
                    this,
                    getString(R.string.type_smth_or_choose_an_image_to_send),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                presenter.sendMessage(chat_log.text.toString(), chatPartner.uid, selectedImageUri)
                chat_log.text.clear()
                choose_mark.visibility = View.GONE
                selectedImageUri = null
                loading_mark.visibility = View.VISIBLE
            }
        }

        choose_file.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun showMessageFrom(message: ChatMessage) {
        adapter.add(ChatFromItem(message, chatPartner))
        if ((message.message == latestMessage?.message) && (message.timestamp == latestMessage?.timestamp)) {
            chat_log_recycler.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun showMessageTo(message: ChatMessage) {
        val currentUser = LatestMessagesActivity.currentUser ?: return
        adapter.add(ChatToItem(message, currentUser))
        if (message.fromId == currentUser.uid) {
            chat_log_recycler.scrollToPosition(adapter.itemCount - 1)
        }
        loading_mark.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            choose_mark.visibility = View.VISIBLE
        }
    }
}
