package com.busgen.bustalk;

import android.app.Activity;
import android.content.Context;
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

    public MessageAdapter(Activity context, List<TempMessage> messages){
        this.context = context;
        this.messages = messages;
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
		Log.d("myTag", "Inside getView");
        ViewHolder holder;
        TempMessage message = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.message_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        boolean isMe = message.getIsMe();

        setAlignment(holder, isMe);
        holder.messageText.setText(message.getMessage());
        holder.messageDate.setText(message.getDate());

        return convertView;
    }

	//Sets the layout of a message item (Which includes 2 TextViews: Message date and the
	//actual message text) in the ListView depending on who sent the message
    private void setAlignment(ViewHolder holder, boolean isMe){
        if (isMe) {
			//Sets a 9-patch image of a white chat bubble as background for message text
            holder.messageTextContainer.setBackgroundResource(R.drawable.speech_bubble_green);

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

			//This could be redundant
			layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
			layoutParams.gravity = Gravity.RIGHT;
			holder.messageText.setLayoutParams(layoutParams);
        } else {
            holder.messageTextContainer.setBackgroundResource(R.drawable.speech_bubble_orange);

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

			//This could be redundant
            layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageText.setLayoutParams(layoutParams);
        }
    }

	//ViewHolder class used for the ViewHolder Design Pattern
	private static class ViewHolder{
		public TextView messageText;
		public TextView messageDate;
		public LinearLayout messageTopContainer;
		public LinearLayout messageTextContainer;
	}

	//Returns a ViewHolder corresponding to the View that is sent in as a parameter
    private ViewHolder createViewHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.messageDate = (TextView) v.findViewById(R.id.message_date);
        holder.messageText = (TextView) v.findViewById(R.id.message_text);
        holder.messageTopContainer = (LinearLayout) v.findViewById(R.id.message_top_container);
        holder.messageTextContainer = (LinearLayout) v.findViewById(R.id.message_text_container);
        return holder;
    }


}
