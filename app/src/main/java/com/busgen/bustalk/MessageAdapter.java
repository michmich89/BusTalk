package com.busgen.bustalk;

import android.app.Activity;
import android.content.Context;
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
        holder.messageDate.setText(message.getMessage());

        return convertView;
    }

    public void add(TempMessage message){
        messages.add(message);
    }

    public void add(List<TempMessage> messages){
        this.messages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe){
        if (!isMe) {
            holder.messageTextContent.setBackgroundResource(R.drawable.bubble_white_normal);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.messageTextContent.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageTextContent.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.topContent.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.topContent.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageText.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.messageDate.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.messageDate.setLayoutParams(layoutParams);
        } else {
            holder.messageTextContent.setBackgroundResource(R.drawable.bubble_white_normal);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.messageTextContent.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageTextContent.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.topContent.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.topContent.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageText.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.messageDate.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.messageDate.setLayoutParams(layoutParams);
        }
    }

    private ViewHolder createViewHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.messageDate = (TextView) v.findViewById(R.id.message_date);
        holder.messageText = (TextView) v.findViewById(R.id.message_text);
        holder.topContent = (LinearLayout) v.findViewById(R.id.top_content);
        holder.messageTextContent = (LinearLayout) v.findViewById(R.id.message_text_content);
        return holder;
    }

    private static class ViewHolder{
        public TextView messageText;
        public TextView messageDate;
        public LinearLayout topContent;
        public LinearLayout messageTextContent;
    }
}
