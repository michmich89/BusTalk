package com.busgen.bustalk;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.List;

/**
 * Created by miche on 2015-10-02.
 */
public class UserAdapter extends BaseAdapter{

	private List<IUser> users;
	private Activity context;
	private LayoutInflater inflater;

	public UserAdapter(Activity context, List<IUser> users){
		this.context = context;
		this.users = users;
		this.inflater = LayoutInflater.from(context);
	}

	public void add(IUser user){
		users.add(user);
	}

	public void add(List<IUser> users){
		this.users.addAll(users);
	}

	public void setUsers(List<IUser> users){
		this.users = users;
	}

	@Override
	public int getCount(){
		if(users != null){
			return users.size();
		}else{
			return 0;
		}
	}

	@Override
	public IUser getItem(int position){
		if(users != null){
			return users.get(position);
		}else{
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		//Set to 0 until a suitable implementation is added
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//ViewHolder design pattern is used to improve performance by minimizing findViewById calls.
		ViewHolder holder;
		IUser user = getItem(position);

		//Creates a new message_item-view and a viewHolder of the same view gets set as a tag for
		//future reuse
		if(convertView == null){
			convertView = inflater.inflate(R.layout.user_item, null);
			holder = createViewHolder(convertView);
			convertView.setTag(holder);
		}//Reuses the holder set as tag to convertView, eliminates the need to call findViewById
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.userName.setText(user.getUserName());
		holder.userInterest.setText(user.getInterest());

		return convertView;
	}

	//ViewHolder class used for the ViewHolder design pattern
	private static class ViewHolder{
		public TextView userName;
		public TextView userInterest;
	}

	//Returns a ViewHolder corresponding to the View that is sent in as a parameter
	private ViewHolder createViewHolder(View v){
		ViewHolder holder = new ViewHolder();
		holder.userName = (TextView) v.findViewById(R.id.user_name);
		holder.userInterest = (TextView) v.findViewById(R.id.user_interest);
		return holder;
	}
}
