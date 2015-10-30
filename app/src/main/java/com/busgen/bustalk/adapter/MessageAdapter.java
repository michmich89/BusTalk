package com.busgen.bustalk.adapter;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busgen.bustalk.R;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.List;

/**
 * This class is responsible for transforming MsgChatMesssage objects into suitable view items that
 * are to be inserted in the ListView belonging to the MainChatActivity.
 *
 * parts taken from http://www.codeproject.com/Tips/897826/Designing-Android-Chat-Bubble-Chat-UI
 */
public class MessageAdapter extends BaseAdapter{
    private final List<MsgChatMessage> messages;
    private Activity context;
    private LayoutInflater inflater;

    public MessageAdapter(Activity context, List<MsgChatMessage> messages){
        this.context = context;
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }

	public void add(MsgChatMessage message){
		messages.add(message);
	}

	public void add(List<MsgChatMessage> messages){
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
    public MsgChatMessage getItem(int position){
        if(messages != null){
            return messages.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getChatId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MsgChatMessage message = getItem(position);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.message_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        boolean isMe = message.getIsMe();
        setAlignment(holder, isMe);
        holder.messageText.setText(message.getMessage());
        holder.messageDate.setText(message.getDate());
        holder.userName.setText(message.getUserName() + ":");

        return convertView;
    }

    /**
     * Sets the alignment and background image of a message in the chat window depending on who
     * sent the message. If the message was sent from the user it is aligned to the right. If it
     * was sent from another user it is aligned to the left.
     *
     * @param holder The ViewHolder object corresponding to a certain View.
     * @param isMe A boolean that is true if the message was sent from the user, false otherwise.
     */
    private void setAlignment(ViewHolder holder, boolean isMe){
        if (isMe) {
            holder.messageTextContainer.setBackgroundResource(R.drawable.bubble_white_normal_mirror);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.messageTextContainer.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageTextContainer.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.messageTopContainer.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.messageTopContainer.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.messageDate.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageDate.setLayoutParams(layoutParams);

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

            layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageText.setLayoutParams(layoutParams);
        }
    }

    /**
     * Viewholder class that is used as part of the ViewHolder design pattern.
     */
	private static class ViewHolder{
		public TextView messageText;
		public TextView messageDate;
		public LinearLayout messageTopContainer;
		public LinearLayout messageTextContainer;
        public TextView userName;
	}

    /**
     * Creates a ViewHolder corresponding to the View passed in as a parameter.
     *
     * @param v The View that is used to create the ViewHolder.
     * @return Returns the ViewHolder corresponding to the view passed in as a parameter.
     */
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
