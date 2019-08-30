package com.topaz.easymessenger.views


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.topaz.easymessenger.R
import com.topaz.easymessenger.contracts.LatestMessagesContract
import com.topaz.easymessenger.data.ChatMessage
import com.topaz.easymessenger.data.User
import com.topaz.easymessenger.presenters.LatestMessagesPresenter
import com.topaz.easymessenger.adapters.LatestMessagesItem
import com.topaz.easymessenger.views.NewMessageActivity.Companion.USER_KEY
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity(), LatestMessagesContract.View {
    companion object {
        var currentUser: User? = null
    }

    private val presenter = LatestMessagesPresenter(this)
    private val adapter = GroupAdapter<ViewHolder>()
    private val latestMessagesMap = LinkedHashMap<String, ChatMessage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, (item as LatestMessagesItem).userPartner)
            startActivity(intent)
        }
        latest_messages_recycler.adapter = adapter
        latest_messages_recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        presenter.verifyIsLogged()
    }

    override fun onLatestChanged(chatMessage: ChatMessage, key: String) {
        latestMessagesMap[key] = chatMessage
        adapter.clear()
        latestMessagesMap.toSortedMap(compareBy { -latestMessagesMap[it]?.timestamp!! })
            .values.forEach {
            adapter.add(LatestMessagesItem(it))
        }
    }

    override fun onLatestAdded(chatMessage: ChatMessage, key: String) {
        latestMessagesMap[key] = chatMessage
        adapter.clear()
        progress_bar.visibility = View.GONE
        latestMessagesMap.toSortedMap(compareBy { -latestMessagesMap[it]?.timestamp!! })
            .values.forEach {
            adapter.add(LatestMessagesItem(it))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }

            R.id.menu_sign_out -> {
                presenter.signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun isLogged() {
        presenter.setListenerForLatest()
        presenter.fetchCurrentUser()
        if (latestMessagesMap.isEmpty()) {
            progress_bar.visibility = View.GONE
        }
    }

    override fun initializeUser(user: User?) {
        currentUser = user
    }

    override fun isNotLogged() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onSignOut() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
