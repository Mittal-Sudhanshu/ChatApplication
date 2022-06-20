package com.mittal.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    EditText etName,etPhNo,etEmail;
    AppCompatButton btnRegister;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference= FirebaseDatabase.getInstance().getReference();

        etEmail=findViewById(R.id.etEmail);
        etName=findViewById(R.id.etName);
        etPhNo=findViewById(R.id.etPhNo);
        btnRegister=findViewById(R.id.RegisterBtn);
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        if (MemoryData.getData(this).isEmpty()){
            Intent intent=new Intent(Register.this,MainActivity.class);
            intent.putExtra("phoneNo",MemoryData.getData(this));
            intent.putExtra("name",MemoryData.getName(this));
            intent.putExtra("email","");
            startActivity(intent);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                final String emailText=etEmail.getText().toString();
                final String nameText=etName.getText().toString();
                final String phText=etPhNo.getText().toString();
                if (emailText.isEmpty()||nameText.isEmpty()||phText.isEmpty())
                {
                    Toast.makeText(Register.this,"Please fill the Fields",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressDialog.dismiss();
                            if (snapshot.child("users").hasChild(phText)) {
                                Toast.makeText(Register.this, "Phone Number Already Exists", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                databaseReference.child("users").child(phText).child("email").setValue(emailText);
                                databaseReference.child("users").child(phText).child("name").setValue(nameText);
                                databaseReference.child("users").child(phText).child("profile_pic").setValue("");
                                MemoryData.saveData(phText,Register.this);
                                MemoryData.saveName(nameText,Register.this);

                                Toast.makeText(Register.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Register.this,MainActivity.class);
                                intent.putExtra("phoneNo",phText);
                                intent.putExtra("name",nameText);
                                intent.putExtra("email",emailText);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });



    }
}