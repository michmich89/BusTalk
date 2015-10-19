package com.busgen.bustalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRooms;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRoomsRequest;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.model.User;
import com.busgen.bustalk.service.EventBus;

import java.util.ArrayList;

public class LoginActivity extends BindingActivity {
    private EditText userNameInput;
    private EditText interestInput;
    private Button loginButton;
    private Toast loginToast;
    private Toast testToast;
    private String interest;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        EventBus.getInstance().register(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameInput.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    loginToast.show();
                    return;
                }
                interest = interestInput.getText().toString();

                IServerMessage serverMessage = new MsgChooseNickname(userName, interest);
                Event event = new ToClientEvent(serverMessage);
                eventBus.postEvent(event);

                //For testing purposes
                Chatroom testChatroom = new Chatroom(10, "Mainchat");
                MsgAvailableRooms testMessage = new MsgAvailableRooms("1");
                testMessage.addRoomToList(testChatroom);
                Event testEvent = new ToActivityEvent(testMessage);
                onEvent(testEvent);
            }
        });
    }

    private void initViews() {
        userNameInput = (EditText) findViewById(R.id.user_name_input);
        interestInput = (EditText) findViewById(R.id.interest_input);
        loginButton = (Button) findViewById(R.id.login_button);
        loginToast = Toast.makeText(LoginActivity.this, "You have to choose a nickname",
                Toast.LENGTH_SHORT);
    }

    @Override
    public void onEvent(Event event){
        IServerMessage message = event.getMessage();

        if (event instanceof ToActivityEvent) {
            if (message instanceof MsgChatMessage) {
            } else if (message instanceof MsgChooseNickname) {
                testToast = Toast.makeText(LoginActivity.this, "The chosen username is: " + ((MsgChooseNickname) message).getNickname(),
                        Toast.LENGTH_SHORT);
                testToast.show();
            } else if (message instanceof MsgCreateRoom) {
            } else if (message instanceof MsgJoinRoom) {
                MsgJoinRoom joinRoomMessage = (MsgJoinRoom) message;
                Chatroom myChatroom = (Chatroom) joinRoomMessage.getChatroom();
                Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                intent.putExtra("Chatroom", myChatroom);
                intent.putExtra("Username", userName);
                intent.putExtra("Interest", interest);
                startActivity(intent);
                LoginActivity.this.finish();
            } else if (message instanceof MsgLeaveRoom) {
            } else if (message instanceof MsgLostChatRoom) {
            } else if (message instanceof MsgLostUserInChat) {
            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
            } else if (message instanceof MsgNicknameAvailable) {
                MsgNicknameAvailable nicknameAvailabileMessage = (MsgNicknameAvailable) message;
                if(nicknameAvailabileMessage.getAvailability()){
                    IServerMessage serverMessage = new MsgAvailableRoomsRequest();
                    Event requestEvent = new ToClientEvent(serverMessage);
                    eventBus.postEvent(requestEvent);
                }
            } else if (message instanceof MsgAvailableRooms) {
                MsgAvailableRooms availableRoomsMesssage = (MsgAvailableRooms) message;
                ArrayList<Chatroom> chatrooms = availableRoomsMesssage.getRoomList();
                Chatroom myChatroom = chatrooms.get(0);
                Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                intent.putExtra("Chatroom", myChatroom);
                intent.putExtra("Username", userName);
                intent.putExtra("Interest", interest);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
    }
}
