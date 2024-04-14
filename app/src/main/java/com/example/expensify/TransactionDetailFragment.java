package com.example.expensify;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.window.SplashScreen;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransactionDetailFragment() {
        // Required empty public constructor
    }
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference expenseCollectionRef = firebaseFirestore.collection("expense");

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionDetailFragment newInstance(String param1, String param2) {
        TransactionDetailFragment fragment = new TransactionDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);

        // Retrieve the TransactionModel object from the arguments Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            TransactionModel transactionModel = (TransactionModel) bundle.getSerializable("transaction");

            // Set text view
            TextView txtCategory = view.findViewById(R.id.txtCategory);
            TextView txtNote = view.findViewById(R.id.txtNote);
            TextView txtAmount = view.findViewById(R.id.txtAmount);
            TextView txtDate = view.findViewById(R.id.txtDate);

            if (transactionModel != null) {
                String categoryDetail = transactionModel.getCategoryDetail();
                String note = transactionModel.getNote();
                double amount = transactionModel.getAmount();
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String date = transactionModel.getCreatedAt();

                txtDate.setText(date);
                txtCategory.setText(categoryDetail);
                txtNote.setText(note);
                txtAmount.setText(decimalFormat.format(amount));

                Context context = getContext();
                if (context != null) {
                    if ("category/0sZQzPZx64wLdM4aauqZ".equals(transactionModel.getCategoryId())) {
                        txtAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        txtAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
                    }
                }

                // Handle button Delete
                Button btnDelete = view.findViewById(R.id.btnDelete);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                fragmentManager.popBackStack();
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
                    }
                });

                // Handle button Edit
                Button btnEdit = view.findViewById(R.id.btnEdit);

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditFragment fragment = new EditFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("transaction", transactionModel);
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.Frame_layout, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        }

        // Handle button Back
        Button btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return view;
    }
}