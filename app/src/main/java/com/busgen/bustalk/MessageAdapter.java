package com.busgen.bustalk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

/**
 * Created by miche on 2015-10-02.
 */
public class MessageAdapter extends BaseAdapter{

    private final List<TempMessage> messages;
    private Activity context;
    private LayoutInflater inflater;

    public MessageAdapter(Activity context, List<TempMessage> messages){
        this.context = context;
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }

	public void add(TempMessage message){
		messages.add(message);
	}

	public void add(List<TempMessage> messages){
		this.messages.addAll(messages);
	}

    @Override
    public int getCount(){
        if(messages != null){
            return messages.size();
        }else{
            return 0;
        }
    }

    @Override
    public TempMessage getItem(int position){
        if(messages != null){
            return messages.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //ViewHolder design pattern is used to improve performance by minimizing findViewById calls.
        ViewHolder holder;
        TempMessage message = getItem(position);

        //Creates a new message_item-view and a viewHolder of the same view gets set as a tag for
        //future reuse
        if(convertView == null){
            convertView = inflater.inflate(R.layout.message_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }//Reuses the holder set as tag to convertView, eliminates the need to call findViewById
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        boolean isMe = message.getIsMe();

        setAlignment(holder, isMe);
        holder.messageText.setText(message.getMessage());
        holder.messageDate.setText(message.getDate());
        holder.userName.setText(message.getUserName() + ":");

        //Underlines the username text, not applied at the moment
        //holder.userName.setPaintFlags(holder.userName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return convertView;
    }

	//Sets the layout of a message item in the ListView depending on who sent the message
    private void setAlignment(ViewHolder holder, boolean isMe){
        if (isMe) {
			//Sets a 9-patch image of a white chat bubble as background for message text
            holder.messageTextContainer.setBackgroundResource(R.drawable.bubble_white_normal_mirror);

			//Aligns the message text and its corresponding bubble to the right in message item
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.messageTextContainer.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageTextContainer.setLayoutParams(layoutParams);

			//Aligns message item to the right in the ListView
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.messageTopContainer.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.messageTopContainer.setLayoutParams(lp);

			//Aligns the message date to the right in the message item
            layoutParams = (LinearLayout.LayoutParams) holder.messageDate.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageDate.setLayoutParams(layoutParams);

			//Sets the visibility of userName TextView to "gone"
			holder.userName.setVisibility(View.GONE);

            //This could be redundant
            layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageText.setLayoutParams(layoutParams);
        } else {
            holder.messageTextContainer.setBackgroundResource(R.drawable.bubble_white_normal);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.messageTextContainer.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageTextContainer.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.messageTopContainer.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.messageTopContainer.setLayoutParams(lp);

			layoutParams = (LinearLayout.LayoutParams) holder.messageDate.getLayoutParams();
			layoutParams.gravity = Gravity.LEFT;
			holder.messageDate.setLayoutParams(layoutParams);

            //Sets the visibility of userName TextView to "visible"
            holder.userName.setVisibility(View.VISIBLE);

			//This could be redundant
            layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageText.setLayoutParams(layoutParams);
        }
    }

	//ViewHolder class used for the ViewHolder design pattern
	private static class ViewHolder{
		public TextView messageText;
		public TextView messageDate;
		public LinearLayout messageTopContainer;
		public LinearLayout messageTextContainer;
        public TextView userName;
	}

	//Returns a ViewHolder corresponding to the View that is sent in as a parameter
    private ViewHolder createViewHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.messageDate = (TextView) v.findViewById(R.id.message_date);
        holder.messageText = (TextView) v.findViewById(R.id.message_text);
        holder.messageTopContainer = (LinearLayout) v.findViewById(R.id.message_top_container);
        holder.messageTextContainer = (LinearLayout) v.findViewById(R.id.message_text_container);
        holder.userName = (TextView) v.findViewById(R.id.user_name);
        return holder;
    }
}
