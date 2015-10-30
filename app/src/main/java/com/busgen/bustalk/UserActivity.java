package com.busgen.bustalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.model.User;
import com.busgen.bustalk.service.EventBus;

import java.util.ArrayList;

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
