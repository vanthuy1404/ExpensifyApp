package com.example.expensify;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private ArrayAdapter<String> adapter;
    final Calendar myCalendar = Calendar.getInstance();
    ArrayList<CategoryModel> categories = new ArrayList<>();
    private String categoryID;
    private String expenseID;
    String[] arraySpinnerExpense = new String[]{
            "Ăn uống", "Hóa đơn & Tiện ích", "Mua sắm", "Gia đình", "Di chuyển", "Sức khỏe", "Giáo dục", "Quà tặng & Quyên góp", "Giải trí", "Bảo hiểm", "Đầu tư", "Các chi phí khác"
    };
    String[] arraySpinnerIncome = new String[]{
            "Lương", "Các khoản thu khác", "Lãi", "Tiền chuyển đến", "Thu nhập khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        firebaseFirestore = FirebaseFirestore.getInstance();
        loadCategory();

        Intent intent = getIntent();
        TransactionModel transactionModel = (TransactionModel) intent.getSerializableExtra("transaction");

        Button btnBack = findViewById(R.id.btnBack);
        EditText edtTxtDate = findViewById(R.id.edtTxtDate);
        EditText edtTxtNote = findViewById(R.id.edtTxtNote);
        EditText edtTxtAmount = findViewById(R.id.edtTxtAmount);
        Spinner spnCategory = findViewById(R.id.spinnerCategory);
        Spinner spnDetail = findViewById(R.id.spinnerDetail);
        ImageView imgCalendar = findViewById(R.id.datePicker);

        if (transactionModel != null) {
            String categoryDetail = transactionModel.getCategoryDetail();
            double amount = transactionModel.getAmount();
            DecimalFormat decimalFormat = new DecimalFormat("####");
            expenseID = transactionModel.getId();

            edtTxtDate.setText(transactionModel.getCreatedAt());
            edtTxtNote.setText(transactionModel.getNote());
            edtTxtAmount.setText(decimalFormat.format(amount));
        }

        // Hiện lịch chọn ngày
        imgCalendar.findViewById(R.id.datePicker).setOnClickListener(e -> {
            new DatePickerDialog(
                    this,
                    (v, y, m, d) -> {
                        myCalendar.set(Calendar.YEAR, y);
                        myCalendar.set(Calendar.MONTH, m);
                        myCalendar.set(Calendar.DATE, d);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        edtTxtDate.setText(sdf.format(myCalendar.getTime()));
                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get((Calendar.DATE))).show();
            myCalendar.getTime();
        });

        // Xử lý khi ấn HỦY
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Handle select Category
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Object item = parent.getItemAtPosition(position);

                if (("Expense").equals(item.toString()) || ("Dépense").equals(item.toString()) || ("Khoản chi").equals(item.toString())) {
                    categoryID = "category/0sZQzPZx64wLdM4aauqZ";
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTransactionActivity.this, android.R.layout.simple_spinner_item, arraySpinnerExpense);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDetail.setAdapter(adapter);
                } else if (("Income").equals(item.toString()) || ("Revenu").equals(item.toString()) || ("Khoản thu").equals(item.toString())){
                    categoryID = "category/mQWS7VpkMR6BPlhknobM";
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTransactionActivity.this, android.R.layout.simple_spinner_item, arraySpinnerIncome);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDetail.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadCategory() {
        CollectionReference categoryCollectionRef = firebaseFirestore.collection("category");
        categoryCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Spinner spnCategory = findViewById(R.id.spinnerCategory);
                            List<String> categoryNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<Object> detail = (List<Object>) document.get("detail");
                                CategoryModel model = new CategoryModel(
                                        document.getId(),
                                        document.getString("group"),
                                        detail
                                );
                                categories.add(model);
                                categoryNames.add(document.getString("group"));
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(EditTransactionActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCategory.setAdapter(adapter);
//                            Log.d(TAG, categories.get(0).getGroup());
                        }
                    }
                });
    }
}