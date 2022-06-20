package com.mittal.chatapplication.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mittal.chatapplication.R;
import com.mittal.chatapplication.chat.Chat;
import com.mittal.chatapplication.chat.ChatAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;

    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessagesList list2=messagesLists.get(position);
        if (!list2.getProfilePic().isEmpty()){
            Picasso.get().load(list2.getProfilePic()).into(holder.profilePic);
        }
        holder.name.setText(list2.getName());
        holder.lastMessage.setText(list2.getLastMessage());
        if (list2.getUnseenMessages()==0){
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        }
        else{
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(list2.getUnseenMessages()+"");
//            holder.lastMessage.setTextColor(Color.parseColor("#fff"));
        }
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, Chat.class);
                intent.putExtra("mobile",list2.getMobile());
                intent.putExtra("name",list2.getName());
                intent.putExtra("profile_pic",list2.getProfilePic());
                intent.putExtra("chat_key",list2.getChatKey());
                context.startActivity(intent);
            }
        });
    }
    public void updateData(List<MessagesList> messagesLists){
        this.messagesLists=messagesLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePic;
        private TextView name,lastMessage,unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic=itemView.findViewById(R.id.profilePic);
            name=itemView.findViewById(R.id.Name);
            lastMessage=itemView.findViewById(R.id.lastMessage);
            unseenMessages=itemView.findViewById(R.id.unseenMessages);
            rootLayout=itemView.findViewById(R.id.rootLayout);

        }
    }
}