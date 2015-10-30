package com.busgen.bustalk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

<<<<<<< HEAD:app/src/main/java/com/busgen/bustalk/UserActivity.java
=======
import com.busgen.bustalk.R;
import com.busgen.bustalk.adapter.UserAdapter;
>>>>>>> Created new packages.:app/src/main/java/com/busgen/bustalk/activity/UserActivity.java
import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.service.EventBus;

import java.util.ArrayList;

/**
 * This class is responsible for listing users that are currently active in the chat.
 */
public class UserActivity extends AppCompatActivity implements IEventBusListener{
	private UserAdapter userAdapter;
	private IChatroom myChatroom;
	private ListView userListView;
    private EventBus eventBus;
    private Client client;
    private int chatId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
        initComponents();
        refreshUserList();
	}

    /**
     * Initiates the components of the activity as well as registers it to the EventBus.
     */
	private void initComponents(){
        userListView = (ListView) findViewById(R.id.user_list_view);
        myChatroom = (IChatroom) getIntent().getSerializableExtra("Chatroom");
        userAdapter = new UserAdapter(UserActivity.this, new ArrayList<IUser>());
        userListView.setAdapter(userAdapter);

        eventBus = EventBus.getInstance();
        eventBus.register(this);
        client = Client.getInstance();
        chatId = myChatroom.getChatID();
	}

    /**
     * Refreshes the users to be listed in this activity.
     */
    private void refreshUserList(){
        for (IChatroom c : client.getChatrooms()) {
            if (c.getChatID() == chatId) {
                myChatroom = c;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userAdapter.setUsers(myChatroom.getUsers());
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This method reacts to events posted on the EventBus that are directed to activities.
     *
     * @param event The event received from the EventBus.
     */
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();
        if (event instanceof ToActivityEvent){
            if (message instanceof MsgLostUserInChat) {
                if(((MsgLostUserInChat) message).getChatID() == chatId) {
                    refreshUserList();
                }
            } else if (message instanceof MsgNewUserInChat) {
                if(((MsgNewUserInChat) message).getChatID() == chatId) {
                    refreshUserList();
                }
            }
        }
    }
}
