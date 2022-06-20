package com.mittal.chatapplication.messages;

public class MessagesList {
    private String name,mobile,lastMessage,profilePic,chatKey;
    private int unseenMessages;

    public MessagesList(String name, String mobile, String lastMessage, int unseenMessages,String profilePic,String chatKey) {
        this.name = name;
        this.chatKey=chatKey;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
        this.profilePic=profilePic;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }
}
