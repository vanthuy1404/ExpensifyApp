package com.example.expensify;

import static android.view.View.inflate;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuthException;

import org.checkerframework.checker.units.qual.A;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TradeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuthException firebaseAuth;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private TransactionAdapter transactionAdapter;
    private RecyclerView transactionRecyclerView;
    public TradeFragment() {
        // Required empty public constructor
    }
    private Context context;
    private TextView balanceTextView;
    private boolean isFirstRender = true;
    private double balance;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TradeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TradeFragment newInstance(String param1, String param2) {
        TradeFragment fragment = new TradeFragment();
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
        firebaseFirestore = FirebaseFirestore.getInstance();
        transactionModelArrayList = new ArrayList<>();

        loadData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade_fragment, container, false);
        transactionRecyclerView = view.findViewById(R.id.transactions_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        transactionRecyclerView.setLayoutManager(layoutManager);
        transactionAdapter = new TransactionAdapter(context, transactionModelArrayList);
        transactionRecyclerView.setAdapter(transactionAdapter);

        balanceTextView = view.findViewById(R.id.txtBalance);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        balanceTextView.setText(decimalFormat.format(balance));

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView transactionsView = view.findViewById(R.id.transactions_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        transactionsView.setLayoutManager(layoutManager);
        transactionAdapter = new TransactionAdapter(context, transactionModelArrayList);
        transactionsView.setAdapter(transactionAdapter);

        balanceTextView = view.findViewById(R.id.txtBalance);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        balanceTextView.setText(decimalFormat.format(balance));
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isFirstRender) {
//            // Reload data to update balanceTextView
//            resetUI();
//            loadData();
//        }
//        isFirstRender = false;
    }

    private void loadData() {
        transactionModelArrayList.clear();
//       firebaseFirestore.collection("expense").document();
        CollectionReference expenseCollectionRef = firebaseFirestore.collection("expense");
        expenseCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        double balance = 0;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timestamp createdAtdata = document.getTimestamp("created_at");
                                long createdMillisecs = createdAtdata.getSeconds() * 1000 + createdAtdata.getNanoseconds() / 1000;
                                Date createdDate = new Date(createdMillisecs);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                TransactionModel model = new TransactionModel(
                                        document.getId(),
                                        document.getString("category_detail"),
                                        document.getString("note"),
                                        document.getDouble("amount"),
                                        document.getString("category_id"),
                                        document.getString("user_id"),
                                        document.getString("wallet_id"),
                                        sdf.format(createdDate)
                                );
                                transactionModelArrayList.add(model);
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            balance = getBalance();
                            if (balanceTextView != null) {
                                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                                balanceTextView.setText(decimalFormat.format(balance));
                            }
                            transactionAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private double getBalance() {
        double balance = 0;
        for (TransactionModel transaction: transactionModelArrayList) {
            if ("category/0sZQzPZx64wLdM4aauqZ".equals(transaction.getId())) {
                balance = balance - transaction.getAmount();
            } else {
                balance = balance + transaction.getAmount();
            }
        }
        return balance;
    }

    private void resetUI() {
        // Reset UI elements here
        // For example, clear RecyclerView data, reset balanceTextView, etc.
        transactionModelArrayList.clear(); // Clear the ArrayList
        transactionAdapter.notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }
}