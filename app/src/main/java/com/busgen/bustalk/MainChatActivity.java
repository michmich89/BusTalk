package com.busgen.bustalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class MainChatActivity extends BindingActivity implements Observer {
    private EditText messageInputLine;
    private ListView messageListView;
    private Button sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<MsgChatMessage> messageHistory;
    private Client client;
    private Chatroom myChatroom;
    private MenuItem usersPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        client = Client.getInstance();
        initViews();

        //Testing purposes
        myChatroom = new Chatroom(1, "Group", "Fotboll", 100);
        client.joinRoom(myChatroom);
        myChatroom.addObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_chat, menu);
        this.usersPresent = menu.findItem(R.id.action_users);
        this.usersPresent.setTitle("" + myChatroom.getNbrOfUsers());
        return true;
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

                    client.addMessageToChatroom(message);
                    return;
                }
                String date = DateFormat.getDateTimeInstance().format(new Date());
                MsgChatMessage message = new MsgChatMessage(true, messageText, date, client.getUserName(), 1);

                messageInputLine.setText("");
                displayMessage(message);
            }
        });
    }

    public void displayMessage(MsgChatMessage message) {
        messageAdapter.add(message);
        messageAdapter.notifyDataSetChanged();
        //If the user sent the message, then put the message in the Chatroom-object
        if(message.getIsMe()){
            client.addMessageToChatroom(message);
        }
    }

    //Makes it seem like there has already been messages sent when the app launches
    private void loadDummyHistory(){
        messageHistory = new ArrayList<MsgChatMessage>();

        String messageText1 = "YO! bla bla bla bla bla bla bla bla bla bla blabla bla bla";
        String date1 = DateFormat.getDateTimeInstance().format(new Date());
        MsgChatMessage dummyMessage1 = new MsgChatMessage(false, messageText1, date1, "Börje Plåt", 1);
        messageHistory.add(dummyMessage1);

        String messageText2 = "Wadup bro!";
        String date2 = DateFormat.getDateTimeInstance().format(new Date());
        MsgChatMessage dummyMessage2 = new MsgChatMessage(true, messageText2, date2, client.getUserName(), 1);
        messageHistory.add(dummyMessage2);

        for(int i=0; i<messageHistory.size(); i++){
            MsgChatMessage message = messageHistory.get(i);
            displayMessage(message);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d("MyTag", "Inside update in MainChatActivity");
        MsgChatMessage message = (MsgChatMessage) data;
        if(!message.getIsMe()){
            displayMessage(message);
        }
    }
}
