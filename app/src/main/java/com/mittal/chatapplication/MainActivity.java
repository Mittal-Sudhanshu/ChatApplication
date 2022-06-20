package com.mittal.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mittal.chatapplication.messages.MessagesAdapter;
import com.mittal.chatapplication.messages.MessagesList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private final List<MessagesList> messagesLists=new ArrayList<>();
    private String name,phone,email;
    private int unseenMessages=0;
    private String lastMessage="";
    private String chatKey="";
    private RecyclerView messagesRecyclerView;
    private boolean dataSet=false;
    long lastSeenMessage;
    private MessagesAdapter messagesAdapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CircleImageView userProfilePic=findViewById(R.id.userProfilePic);
        messagesRecyclerView=findViewById(R.id.messagesRecyclerView);
        phone=getIntent().getStringExtra("phoneNo");
        email=getIntent().getStringExtra("email");
        name=getIntent().getStringExtra("name");
        databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-application-745eb-default-rtdb.firebaseio.com/");
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter=new MessagesAdapter(messagesLists,MainActivity.this);
        messagesRecyclerView.setAdapter(messagesAdapter);
        Context context;
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profilePicUrl=snapshot.child("users").child(phone).child("profile_pic").getValue(String.class);
                if (profilePicUrl!=null&&!profilePicUrl.isEmpty())
                    Picasso.get().load(profilePicUrl).into(userProfilePic);
                else
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesLists.clear();
                unseenMessages=0;
                lastMessage="";
                chatKey="";
                for (DataSnapshot dataSnapshot:snapshot.child("users").getChildren()){
                    final String getMobile=dataSnapshot.getKey();
                    dataSet=false;
                    if (!getMobile.equals(phone)){

                        final String getName=dataSnapshot.child("name").getValue(String.class);
                        final String getProfilePic=dataSnapshot.child("profile_pic").getValue(String.class);
                        if (!dataSet){
                            dataSet=true;
                            MessagesList messagesList=new MessagesList(getName,getMobile,lastMessage,unseenMessages,getProfilePic,chatKey);
                            messagesLists.add(messagesList);
                            messagesAdapter.updateData(messagesLists);
                        }

                        databaseReference.child("chat").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCounts=(int)snapshot.getChildrenCount();
                                if (getChatCounts>0){
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                        final String getKey=dataSnapshot1.getKey();
                                        chatKey=getKey;
                                        if (dataSnapshot1.hasChild("user_1")&&dataSnapshot1.hasChild("user_2")&&dataSnapshot1.hasChild("messages")) {
                                            final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);
                                            if (getUserOne.equals(getMobile) && getUserTwo.equals(phone) || getUserOne.equals(phone) && getUserTwo.equals(getMobile)) {
                                                for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {
                                                    final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                    try {
                                                         lastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(MainActivity.this, getKey));
                                                    }
                                                    catch (NumberFormatException e){
                                                        e.printStackTrace();
                                                    }

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if (getMessageKey > lastSeenMessage) {
                                                        unseenMessages++;
                                                    }
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

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}