package com.example.expensify;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {
    Button saveButton, returnButton;
    EditText oldPassword, newPassword, confirmPassword;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        saveButton = findViewById(R.id.saveButton);
        returnButton = findViewById(R.id.returnButton);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ChangePassword.this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChangePassword.this);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ChangePassword.this, "default")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Expensify")
                            .setContentText("Đổi mật khẩu thành công")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSound(null);
                    notificationManager.notify(1, builder.build());
                } else {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(ChangePassword.this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
                changePassword();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changePassword() {
        String oldPasswordText = oldPassword.getText().toString().trim();
        String newPasswordText = newPassword.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPasswordText)) {
            oldPassword.setError("Nhập mật khẩu cũ của bạn");
            return;
        }

        if (TextUtils.isEmpty(newPasswordText)) {
            newPassword.setError("Nhập mật khẩu mới của bạn");
            return;
        }

        if (TextUtils.isEmpty(confirmPasswordText)) {
            confirmPassword.setError("Nhập lại mật khẩu mới của bạn");
            return;
        }

        if (!newPasswordText.equals(confirmPasswordText)) {
            confirmPassword.setError("Mật khẩu mới không khớp");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        // Re-authenticate người dùng
        assert user != null;
        user.reauthenticate(EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPasswordText))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Nếu xác thực thành công, cập nhật mật khẩu mới trong Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("user").document(userId);
                            userRef.update("password", newPasswordText)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            currentUser.updatePassword(newPasswordText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Nếu cập nhật thành công, cập nhật lại SharedPreferences với mật khẩu mới
                                                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString("password", newPasswordText);
                                                        editor.apply();
                                                        Toast.makeText(ChangePassword.this, "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ChangePassword.this, "Không thể thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChangePassword.this, "Không thể thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(ChangePassword.this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePassword.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
