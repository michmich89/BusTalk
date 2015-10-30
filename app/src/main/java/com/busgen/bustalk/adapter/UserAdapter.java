package com.busgen.bustalk.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD:app/src/main/java/com/busgen/bustalk/UserAdapter.java
import android.widget.BaseAdapter;
import android.widget.TextView;

=======
import android.widget.*;

import com.busgen.bustalk.R;
>>>>>>> Created new packages.:app/src/main/java/com/busgen/bustalk/adapter/UserAdapter.java
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IUser;

import java.util.List;

/**
 * This class is responsible for transforming User objects into suitable view items that
 * are to be inserted in the ListView belonging to the UserActivity.
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		IUser user = getItem(position);

		if(convertView == null){
			convertView = inflater.inflate(R.layout.user_item, null);
			holder = createViewHolder(convertView);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.userName.setText(user.getUserName());
		holder.userInterest.setText(user.getInterest());
		if (user.getUserName().equals(Client.getInstance().getUserName())) {
				holder.userName.setTextColor(Color.rgb(0, 110, 0));
		}

		return convertView;
	}

	/**
	 * Viewholder class that is used as part of the ViewHolder design pattern.
	 */
	private static class ViewHolder{
		public TextView userName;
		public TextView userInterest;
	}

	/**
	 * Creates a ViewHolder corresponding to the View passed in as a parameter.
	 *
	 * @param v The View that is used to create the ViewHolder.
	 * @return Returns the ViewHolder corresponding to the view passed in as a parameter.
	 */
	private ViewHolder createViewHolder(View v){
		ViewHolder holder = new ViewHolder();
		holder.userName = (TextView) v.findViewById(R.id.user_name);
		holder.userInterest = (TextView) v.findViewById(R.id.user_interest);
		return holder;
	}
}
