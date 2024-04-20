package com.example.expensify;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class TransactionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        Intent intent = getIntent();
        TransactionModel transactionModel = (TransactionModel) intent.getSerializableExtra("transaction");

        TextView txtDate = findViewById(R.id.txtDate);
        TextView txtCategory = findViewById(R.id.txtCategory);
        TextView txtNote = findViewById(R.id.txtNote);
        TextView txtAmount = findViewById(R.id.txtAmount);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnEdit = findViewById(R.id.btnEdit);

        if (transactionModel != null) {
            double amount = transactionModel.getAmount();
            DecimalFormat decimalFormat = new DecimalFormat("#,###");

            txtDate.setText(transactionModel.getCreatedAt());
            txtCategory.setText(transactionModel.getCategoryDetail());
            txtNote.setText(transactionModel.getNote());
            txtAmount.setText(decimalFormat.format(amount));

            if ("category/0sZQzPZx64wLdM4aauqZ".equals(transactionModel.getCategoryId())) {
                txtAmount.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                txtAmount.setTextColor(ContextCompat.getColor(this, R.color.green));
            }
        }

        // Xử lý khi ấn QUAY LẠI
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Xử lý khi ấn XÓA
        btnDelete.setOnClickListener(v -> {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference expenseCollectionRef = firebaseFirestore.collection("expense");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Bạn có chắc muốn xóa giao dịch này?").setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String documentId = transactionModel.getId(); // Assuming you have a method to get the document ID

                    // Get reference to the specific document to delete
                    DocumentReference documentRef = expenseCollectionRef.document(documentId);

                    documentRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Delete unsuccessfully");
                                }
                            });
                }
            }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        });

        // Xử lý khi ấn SỬA
        btnEdit.setOnClickListener(v -> {
            Intent intentToUpdate = new Intent(this, EditTransactionActivity.class);
            intentToUpdate.putExtra("transaction", transactionModel);
            startActivityForResult(intentToUpdate, 123);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                boolean saveSuccess = data.getBooleanExtra("save_success", false);
                if (saveSuccess) {
                    finish();
                }
            }
        }
    }
}