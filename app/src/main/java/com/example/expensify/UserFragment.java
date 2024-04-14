package com.example.expensify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";





    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore database;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment user_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        DocumentReference docRef = FirebaseFirestore.getInstance().collection("user").document("oV7QOeQQAAY703JebyXk");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        username = doc.getString("username");
                        Log.d("Document", "Username: " + username);
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });


    }
    View view;
    Button btnEdit, btnExit;
    TextView titleName,titleUsername,profileName, profileEmail, profileUsername,profileTelephone;

    private LinearLayout editLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_fragment, container, false);
        btnEdit = view.findViewById(R.id.editButton);
        btnExit = view.findViewById(R.id.exitButton);

        titleName = view.findViewById(R.id.titleName);
        titleUsername = view.findViewById(R.id.titleUsername);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileUsername = view.findViewById(R.id.profileUsername);
        profileTelephone = view.findViewById(R.id.profileTelephone);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("user").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String username = doc.getString("username");
                        Log.d("Document", "Username: " + username);
                        // Update the UI element with the username
                        titleUsername.setText(username);
                        profileUsername.setText(username);

                        String email = doc.getString("email");
                        Log.d("Document", "Email: " + email);
                        profileEmail.setText(email);

                        String name = doc.getString("name");
                        Log.d("Document", "Name: " + name);
                        titleName.setText(name);
                        profileName.setText(name);

                        String telephone = doc.getString("telephone");
                        Log.d("Document", "Telephone: " + telephone);
                        profileTelephone.setText(telephone);
                    } else {
                        Log.d("Document", "No data");
                    }
                } else {
                    // Handle error if the task is not successful
                    Log.d("Document", "Error: " + task.getException());
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Expensify")
                            .setContentText("Đăng xuất thành công")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSound(null);
                    notificationManager.notify(1, builder.build());
                } else {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
                exitAccount();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_user_fragment, container, false);
    }

    private void exitAccount() {
        // Clear the user's authentication session
        FirebaseAuth.getInstance().signOut();

        // Finish the current activity and return to the login screen
        getActivity().finish();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }
//    public void onLogOutButtonClick(View view) {
//        // Perform the necessary action to log out the user and go back to the login screen
//
//        // Get the FragmentManager and perform a popBackStack() to go back to the login screen
//        FragmentManager manager = getFragmentManager();
//        if (manager != null) {
//            manager.popBackStack();
//        }
//
//        // Sign out the user
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        mAuth.signOut();
//
//        // Redirect to the login screen
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        startActivity(intent);
//        getActivity().finish();
//    }

    public void editProfile(){
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }
}