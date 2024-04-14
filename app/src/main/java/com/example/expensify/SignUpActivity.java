package com.example.expensify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupConfirmPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String confirmPass = signupConfirmPassword.getText().toString().trim();

                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                    signupEmail.requestFocus();
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                    signupPassword.requestFocus();
                }
                if (confirmPass.isEmpty()){
                    signupConfirmPassword.setError("Confirm password cannot be empty");
                    signupConfirmPassword.requestFocus();
                }
                if (!pass.equals(confirmPass)){
                    signupPassword.setError("Password is not same confirm password");
                    signupConfirmPassword.setError("Confirm password is not same password");
                    signupPassword.requestFocus();
                }
                else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                createUserDocumentInFirestore(user,pass,"","","");
                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
    private void createUserDocumentInFirestore(String user, String pass, String username, String name, String telephone) {
        // Lấy ID người dùng hiện tại
        String userId = auth.getCurrentUser().getUid();

        // Tạo tài liệu người dùng với các trường cần thiết (email, tên, v.v.)
        Map<String, Object> userDocument = new HashMap<>();
        userDocument.put("email", user);
        userDocument.put("password",pass);
        userDocument.put("username",username);
        userDocument.put("name", name);
        userDocument.put("telephone",telephone);

        // Tham chiếu đến bộ sưu tập "users" trong Firestore
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("user").document(userId);

        // Lưu tài liệu người dùng vào Firestore
        userRef.set(userDocument)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignUpActivity", "Tạo tài liệu người dùng thành công!");
                        } else {
                            Log.w("SignUpActivity", "Lỗi tạo tài liệu người dùng: " + task.getException().getMessage());
                        }
                    }
                });
    }

}