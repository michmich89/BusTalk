package com.busgen.bustalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IServerMessage;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class MainChatActivity extends BindingActivity {
    private EditText messageInputLine;
    private ListView messageListView;
    private Button sendButton;
    private MessageAdapter messageAdapter;
    private Chatroom myChatroom;
    private MenuItem usersPresent;
    private String userName;
    private String interest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        initVariables();
        initViews();
    }

    private void initVariables(){
        userName = getIntent().getStringExtra("Username");
        interest = getIntent().getStringExtra("Interest");
        myChatroom = (Chatroom) getIntent().getSerializableExtra("Chatroom");
    }

    private void initViews(){
        messageInputLine = (EditText) findViewById(R.id.message_input_line);
        messageListView = (ListView) findViewById(R.id.message_list_view);
        sendButton = (Button) findViewById(R.id.send_button);
        messageAdapter = new MessageAdapter(MainChatActivity.this, new ArrayList<MsgChatMessage>());
        messageListView.setAdapter(messageAdapter);
        loadDummyHistory();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageInputLine.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    //For testing purposes, an empty messageInputLine now makes it seem like a
                    //random message has been sent to the user
                    Random rand = new Random();
                    String date = DateFormat.getDateTimeInstance().format(new Date());
                    MsgChatMessage message = new MsgChatMessage(false, "" + rand.nextInt(1000), date, "Börje Plåt", myChatroom.getChatID());

                    displayMessage(message);
                    return;
                }
                String date = DateFormat.getDateTimeInstance().format(new Date());
                MsgChatMessage message = new MsgChatMessage(true, messageText, date, userName, 1);

                messageInputLine.setText("");
                displayMessage(message);
            }
        });
    }

    public void displayMessage(MsgChatMessage message) {
        messageAdapter.add(message);
        messageAdapter.notifyDataSetChanged();
        /** Maybe not relevant anymore
         //If the user sent the message, then put the message in the Chatroom-object
         if(message.getIsMe()){
         client.addMessageToChatroom(message);
         }
         */
    }

    //Makes it seem like there has already been messages sent when the app launches
    private void loadDummyHistory(){
        ArrayList<MsgChatMessage> messageHistory = new ArrayList<MsgChatMessage>();

        String messageText1 = "YO! bla bla bla bla bla bla bla bla bla bla blabla bla bla";
        String date1 = DateFormat.getDateTimeInstance().format(new Date());
        MsgChatMessage dummyMessage1 = new MsgChatMessage(false, messageText1, date1, "Börje Plåt", 1);
        messageHistory.add(dummyMessage1);

        String messageText2 = "Wadup bro!";
        String date2 = DateFormat.getDateTimeInstance().format(new Date());
        MsgChatMessage dummyMessage2 = new MsgChatMessage(true, messageText2, date2, userName, 1);
        messageHistory.add(dummyMessage2);

        for(int i=0; i<messageHistory.size(); i++){
            MsgChatMessage message = messageHistory.get(i);
            displayMessage(message);
        }
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();
        if (event instanceof ToActivityEvent) {
            if (message instanceof MsgChatMessage) {
                MsgChatMessage chatMessage = (MsgChatMessage) message;
                if(!chatMessage.getIsMe()){
                    displayMessage(chatMessage);
                }
            } else if (message instanceof MsgChooseNickname) {
            } else if (message instanceof MsgCreateRoom) {
            } else if (message instanceof MsgJoinRoom) {
            } else if (message instanceof MsgLeaveRoom) {
            } else if (message instanceof MsgLostChatRoom) {
            } else if (message instanceof MsgLostUserInChat) {
            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
            } else if (message instanceof MsgNicknameAvailable) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_chat, menu);
        this.usersPresent = menu.findItem(R.id.action_users);
        this.usersPresent.setTitle("" + myChatroom.getNbrOfUsers());
        return true;
    }
}
