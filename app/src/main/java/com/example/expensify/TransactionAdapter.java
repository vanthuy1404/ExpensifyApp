package com.example.expensify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    Context context;
    ArrayList<TransactionModel> transactionModelArrayList;

    public TransactionAdapter(Context context, ArrayList<TransactionModel> transactionModelArrayList) {
        this.context = context;
        this.transactionModelArrayList = transactionModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_transaction_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.MyViewHolder holder, int position) {
        TransactionModel model = transactionModelArrayList.get(position);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        holder.amount.setText(decimalFormat.format(model.getAmount()));
        holder.date.setText(model.getCreatedAt());
        holder.note.setText(model.getNote());
        holder.category.setText(model.getCategoryDetail());

        if (model.getCategoryId().equals("category/0sZQzPZx64wLdM4aauqZ")) {
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList == null ? 0 : transactionModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note, amount, category, date;
        CardView transactionItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.txtNote);
            amount = itemView.findViewById(R.id.txtAmount);
            category = itemView.findViewById(R.id.txtCategory);
            date = itemView.findViewById(R.id.txtDate);
            transactionItem = itemView.findViewById(R.id.transactionItem);

            transactionItem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TransactionModel transaction = transactionModelArrayList.get(position);
                    TransactionDetailFragment fragment = new TransactionDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("transaction", transaction);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.Frame_layout, fragment)
                            .addToBackStack(null)
                            .commit();
//                    Intent intent = new Intent(context, TransactionDetailFragment.class);

                }
            });

        }
    }
}
