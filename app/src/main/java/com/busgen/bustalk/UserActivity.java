package com.busgen.bustalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.User;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
	private UserAdapter userAdapter;
	private IChatroom myChatroom;
	private ListView userListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		userListView = (ListView) findViewById(R.id.user_list_view);
		myChatroom = (IChatroom) getIntent().getSerializableExtra("Chatroom");
		userAdapter = new UserAdapter(UserActivity.this, new ArrayList<IUser>());
		userListView.setAdapter(userAdapter);

		userAdapter.add(myChatroom.getUsers());
		userAdapter.add(new User("Nisse", "Glida"));
		userAdapter.notifyDataSetChanged();
	}

	private void initViews(){

	}
}
