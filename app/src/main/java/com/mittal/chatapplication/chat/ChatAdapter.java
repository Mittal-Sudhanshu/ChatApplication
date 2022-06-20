package com.mittal.chatapplication.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mittal.chatapplication.MemoryData;
import com.mittal.chatapplication.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatList> chatLists;
    private final Context context;
    private String userMobile;
    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
        this.userMobile = MemoryData.getData(context);
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatList list2=chatLists.get(position);
        if (list2.getMobile().equals(userMobile)){
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppLayout.setVisibility(View.GONE);

            holder.myMessage.setText(list2.getMessage());
            holder.myTime.setText(list2.getDate()+" "+list2.getTime());
        }
        else
        {
            holder.oppLayout.setVisibility(View.VISIBLE);
            holder.myLayout.setVisibility(View.GONE);

            holder.oppMessage.setText(list2.getMessage());
            holder.oppTime.setText(list2.getDate()+" "+list2.getTime());
        }
    }
    @Override
    public int getItemCount() {
        return chatLists.size();
    }
    public void updateChatList(List<ChatList> chatLists){
        this.chatLists=chatLists;
        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout oppLayout,myLayout;
        private TextView oppMessage,myMessage;
        private TextView oppTime,myTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oppLayout=itemView.findViewById(R.id.oppoLayout);
            myLayout=itemView.findViewById(R.id.myLayout);
            oppMessage=itemView.findViewById(R.id.opoMessage);
            oppTime=itemView.findViewById(R.id.opoTime);
            myMessage=itemView.findViewById(R.id.myMessage);
            myTime=itemView.findViewById(R.id.myTime);
        }
    }
}
