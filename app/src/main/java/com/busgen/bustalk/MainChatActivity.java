package com.busgen.bustalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.service.EventBus;

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
    private IChatroom myChatroom;
    private MenuItem usersPresent;
    private String userName;
    private String interest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HEJ");
        setContentView(R.layout.activity_main_chat);
        initVariables();
        initViews();


        eventBus.register(this);

        System.out.println("TAGGG4" + client);
    }

    private void initVariables(){
        userName = getIntent().getStringExtra("Username");
        interest = getIntent().getStringExtra("Interest");
        myChatroom = (IChatroom) getIntent().getSerializableExtra("Chatroom");
        //For testing
        //myChatroom = new Chatroom(1, "Mainchat");
    }

    private void initViews(){
        messageInputLine = (EditText) findViewById(R.id.message_input_line);
        messageListView = (ListView) findViewById(R.id.message_list_view);
        sendButton = (Button) findViewById(R.id.send_button);
        messageAdapter = new MessageAdapter(MainChatActivity.this, new ArrayList<MsgChatMessage>());
        messageListView.setAdapter(messageAdapter);
        //loadDummyHistory();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageInputLine.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    //For testing purposes, an empty messageInputLine now makes it seem like a
                    //random message has been sent to the user
                    Random rand = new Random();
                    String date = DateFormat.getDateTimeInstance().format(new Date());
                    MsgChatMessage message = new MsgChatMessage(false, "" + "Det är bra, själv då? ^_^", date, "Kalle Jönsson", myChatroom.getChatID());

                    Log.d("IdTag", "" + myChatroom.getChatID());
                    //Test 1 of receiving of messages
                    /*
                    Event event = new ToActivityEvent(message);
                    onEvent(event);
                    return;
                    */

                    //Test 2 of receiving of messages
                    Event event = new ToServerEvent(message);
                    eventBus.postEvent(event);

                    return;
                }
                String date = DateFormat.getDateTimeInstance().format(new Date());
                MsgChatMessage message = new MsgChatMessage(true, messageText, date, userName, myChatroom.getChatID());

                messageInputLine.setText("");
                displayMessage(message);
                myChatroom.addMessage(message);

                Event event = new ToServerEvent(message);
                eventBus.postEvent(event);
            }
        });
    }

    public void displayMessage(MsgChatMessage message) {
        System.out.println("displayTag " + message.getNickname());
        System.out.println("displayTag " + message.getDate());
        System.out.println("displayTag " + message.getMessage());
        System.out.println("displayTag " + message.getChatId());
        System.out.println("displayTag " + message.getIsMe());
        System.out.println("displayTag " + message.toString());
        System.out.println("displayTag " + client.getUserName());

        messageAdapter.add(message);
        messageAdapter.notifyDataSetChanged();
        //messageListView.setSelection(messageListView.getCount() - 1);
    }

    //Makes it seem like there has already been messages sent when the app launches
    private void loadDummyHistory(){
        ArrayList<MsgChatMessage> messageHistory = new ArrayList<MsgChatMessage>();

        String messageText1 = "Tjena mannen! Hur är läget? xD";
        String date1 = DateFormat.getDateTimeInstance().format(new Date());
        MsgChatMessage dummyMessage1 = new MsgChatMessage(false, messageText1, date1, "Kalle Jönsson", 1);
        messageHistory.add(dummyMessage1);

        String messageText2 = "Hallå där!";
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
                //if(!chatMessage.getIsMe()){
                    Log.d("MyTag", "Inside onEvent of MainChatActivity");
                    displayMessage(chatMessage);
                //}
            } else if (message instanceof MsgCreateRoom) {
            } else if (message instanceof MsgJoinRoom) {
            } else if (message instanceof MsgLeaveRoom) {
            } else if (message instanceof MsgLostChatRoom) {
            } else if (message instanceof MsgLostUserInChat) {
            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
            } else if (message instanceof MsgConnectionLost){
                connectionLostAlert();
            } else if (message instanceof MsgPlatformData) {
                /*skriver ut nästa hållplats i en label*/
                System.out.println("skriver nästa hållplats mannen");
                if (((MsgPlatformData) message).getDataType().equals("nextStop")){

                    final MsgPlatformData nextStopMessage = (MsgPlatformData) message;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String nextStop = nextStopMessage.getData();
                            ((TextView) findViewById(R.id.nextStopLabel)).setText("Nästa hållplats: " + nextStop);
                            //System.out.println("Texten i labeln är: " + ((TextView) findViewById(R.id.nextStopLabel)).getText());
                        }
                    });




                }

            }
        }
    }

    public void connectionLostAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainChatActivity.this).create();
        alertDialog.setTitle("Connection Error");
        alertDialog.setMessage("Your connection to the server has been lost");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        MainChatActivity.this.finish();
                    }
                });
        alertDialog.show();
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
