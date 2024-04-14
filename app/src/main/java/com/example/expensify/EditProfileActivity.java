package com.example.expensify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    TextView textEmail;
    EditText editName,editEmail,editPhone,editUsername;
    Button saveButton, returnButton;
    String nameUser, emailUser, usernameUser, telephoneUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //editEmail = findViewById(R.id.editEmail);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editTelephone);
        editUsername = findViewById(R.id.editUsername);
        saveButton = findViewById(R.id.saveButton);
        returnButton = findViewById(R.id.returnButton);
        textEmail = findViewById(R.id.textEmail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("user").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult().exists()) {
                        String email = task.getResult().getString("email");

//                        // Hiển thị email trong EditText (sau khi vô hiệu hóa)
//                        EditText emailEditText = findViewById(R.id.editEmail);
//                        emailEditText.setText(email);
//                        emailEditText.setEnabled(false);

                        textEmail.setText(email);
                    } else {
                        Log.d("EditProfileActivity", "No data found for user");
                    }
                } else {
                    Log.w("EditProfileActivity", "Error getting user data: ", task.getException());
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String username = editUsername.getText().toString();
                //String email = editEmail.getText().toString();
                String phone = editPhone.getText().toString();

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference docRef = FirebaseFirestore.getInstance().collection("user").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("username", username);
                //user.put("email", email);
                user.put("telephone", phone);

                docRef.update(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "User updated successfully");
                                Toast.makeText(EditProfileActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(EditProfileActivity.this, UserFragment.class);
//                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Firestore", "Error updating user", e);
                                Toast.makeText(EditProfileActivity.this, "Error updating user", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, UserFragment.class);
                startActivity(intent);
            }
        });
    }

}