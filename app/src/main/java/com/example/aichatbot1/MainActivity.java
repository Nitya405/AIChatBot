package com.example.aichatbot1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText userInput;
    private ImageButton sendButton;
    private Button logoutButton;
    private RecyclerView recyclerView;

    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        logoutButton = findViewById(R.id.logoutBtn);
        recyclerView = findViewById(R.id.recyclerView);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        // Listen for new messages from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database error: ", error.toException());
            }
        });

        // Send message button
        sendButton.setOnClickListener(v -> {
            String messageText = userInput.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
                userInput.setText("");
            }
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void sendMessage(String text) {
        // Add user message
        Message userMessage = new Message(text, "user");
        databaseReference.push().setValue(userMessage);

        messageList.add(userMessage);
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageList.size() - 1);

        // Generate bot reply
        String botReply;
        if (text.equalsIgnoreCase("hello")) {
            botReply = "Hello! How can I assist you today?";
        } else if (text.equalsIgnoreCase("good morning")) {
            botReply = "Good morning! How can I assist you today?";
        } else {
            botReply = "I'm here to chat! Ask me anything.";
        }

        // Add bot message
        Message botMessage = new Message(botReply, "chatbot");
        databaseReference.push().setValue(botMessage);

        messageList.add(botMessage);
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }
}
