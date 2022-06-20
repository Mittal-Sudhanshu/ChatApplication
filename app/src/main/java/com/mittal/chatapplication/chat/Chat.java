package com.mittal.chatapplication.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mittal.chatapplication.MemoryData;
import com.mittal.chatapplication.R;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-application-745eb-default-rtdb.firebaseio.com/");
    private String chatKey;
    private String getUserMobile;
    private RecyclerView chattingRecView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime=true;
    private final List<ChatList> chatLists=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();
        final ImageView backBtn=findViewById(R.id.backBtn);
        final CircleImageView profPic=findViewById(R.id.profPic);
        final TextView namee=findViewById(R.id.namee);
        final TextView status=findViewById(R.id.status);
        final EditText messageET=findViewById(R.id.messageET);
        final ImageView sendBtn =findViewById(R.id.SendBtn);
        final String getName= getIntent().getStringExtra("name");
        chatKey=getIntent().getStringExtra("chat_key");
        chattingRecView=findViewById(R.id.chattingRecView);
        final String getProfilePic=getIntent().getStringExtra("profile_pic");
        final String getMobile=getIntent().getStringExtra("mobile");
        getUserMobile= MemoryData.getData(Chat.this);
        namee.setText(getName);
        if (getProfilePic.isEmpty()){
            Picasso.get().load(R.drawable.user_avatar).into(profPic);
        }
        else
            Picasso.get().load(getProfilePic).into(profPic);

        chattingRecView.setHasFixedSize(true);
        chattingRecView.setLayoutManager(new LinearLayoutManager(Chat.this));
        chatAdapter=new ChatAdapter(chatLists,Chat.this);
        chattingRecView.setAdapter(chatAdapter);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (chatKey.isEmpty()) {
                        chatKey = "1";

                        if (snapshot.hasChild("chat")) {
                            chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                        }
                    }
                    if (snapshot.hasChild("chat")){
                        if (snapshot.child("chat").child(chatKey).hasChild("messages")) {
                            chatLists.clear();
                            for (DataSnapshot messagesSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()) {
                                if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("mobiile")) {
                                    final String messagesTimeStamps = messagesSnapshot.getKey();
                                    final String getMobile = messagesSnapshot.child("mobiile").getValue(String.class);
                                    final String getMsg = messagesSnapshot.child("msg").getValue(String.class);
                                    Timestamp timestamp = new Timestamp(Long.parseLong(messagesTimeStamps));
                                    Date date = new Date(timestamp.getTime());
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
                                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:aa", Locale.getDefault());

                                    ChatList chatList = new ChatList(getMobile, getName, getMsg, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                    chatLists.add(chatList);

                                    if (loadingFirstTime || Long.parseLong(messagesTimeStamps) > Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey))) {
                                        loadingFirstTime=false;

                                        MemoryData.saveLastMsgTS(messagesTimeStamps, chatKey, Chat.this);
                                        chatAdapter.updateChatList(chatLists);
                                        chattingRecView.scrollToPosition(chatLists.size()-1);

                                    }

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getTxtMessage=messageET.getText().toString();
                final String currentTimeStamp=String.valueOf(System.currentTimeMillis()).substring(0,10);

                databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
                databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("msg").setValue(getTxtMessage);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("mobiile").setValue(getUserMobile);
                messageET.setText("");
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}