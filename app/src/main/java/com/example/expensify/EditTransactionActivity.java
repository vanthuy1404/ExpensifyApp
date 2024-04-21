package com.example.expensify;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        Intent intent = getIntent();
        TransactionModel transactionModel = (TransactionModel) intent.getSerializableExtra("transaction");
        loadCategory(transactionModel);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);
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

        // Chọn Category
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Object item = parent.getItemAtPosition(position);
                int defaultDetailPosition = 0;

                if (("Expense").equals(item.toString()) || ("Dépense").equals(item.toString()) || ("Khoản chi").equals(item.toString())) {
                    categoryID = "category/0sZQzPZx64wLdM4aauqZ";
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTransactionActivity.this, android.R.layout.simple_spinner_item, arraySpinnerExpense);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDetail.setAdapter(adapter);
                    for (int i = 0; i < arraySpinnerExpense.length; i++) {
                        if (transactionModel.getCategoryDetail().equals(arraySpinnerExpense[i])) {
                            defaultDetailPosition = i;
                            break;
                        }
                    }
                } else if (("Income").equals(item.toString()) || ("Revenu").equals(item.toString()) || ("Khoản thu").equals(item.toString())){
                    categoryID = "category/mQWS7VpkMR6BPlhknobM";
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditTransactionActivity.this, android.R.layout.simple_spinner_item, arraySpinnerIncome);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDetail.setAdapter(adapter);
                    for (int i = 0; i < arraySpinnerIncome.length; i++) {
                        if (transactionModel.getCategoryDetail().equals(arraySpinnerIncome[i])) {
                            defaultDetailPosition = i;
                            break;
                        }
                    }
                }

                spnDetail.setSelection(defaultDetailPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // LƯU thay đổi
        btnSave.setOnClickListener(v -> {
            String newNote = edtTxtNote.getText().toString();
            String newDetail = spnDetail.getSelectedItem().toString();
            Number newAmount = 0;
            try {
                newAmount = NumberFormat.getInstance().parse(edtTxtAmount.getText().toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Map<String, Object> newExpense = new HashMap<>();
            newExpense.put("created_at", myCalendar.getTime());
            newExpense.put("amount", newAmount);
            newExpense.put("note", newNote);
            newExpense.put("category_detail", newDetail);
            newExpense.put("category_id", categoryID);

            editExpenseDocument(expenseID, newExpense);
        });
    }

    private void loadCategory(TransactionModel transactionModel) {
        CollectionReference categoryCollectionRef = firebaseFirestore.collection("category");
        categoryCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Spinner spnCategory = findViewById(R.id.spinnerCategory);
                            List<String> categoryNames = new ArrayList<>();
                            int selectedIndex = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<Object> detail = (List<Object>) document.get("detail");
                                CategoryModel model = new CategoryModel(
                                        document.getId(),
                                        document.getString("group"),
                                        detail
                                );
                                categories.add(model);
                                categoryNames.add(document.getString("group"));
                                if (transactionModel.getCategoryId().equals("category/0sZQzPZx64wLdM4aauqZ")) {
                                    selectedIndex = 0;
                                } else if (transactionModel.getCategoryId().equals("category/mQWS7VpkMR6BPlhknobM")) {
                                    selectedIndex = 1;
                                }
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(EditTransactionActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCategory.setAdapter(adapter);

                            spnCategory.setSelection(selectedIndex);
//                            Log.d(TAG, categories.get(0).getGroup());
                        }
                    }
                });
    }

    public void editExpenseDocument(String expenseId, Map<String, Object> newExpense) {
        DocumentReference expenseRef = firebaseFirestore.collection("expense").document(expenseId);

        expenseRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Perform the update
                        expenseRef.update(newExpense)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> updateTask) {
                                        if (updateTask.isSuccessful()) {
                                            // Update successful
                                            AlertDialog.Builder builder = new AlertDialog.Builder(EditTransactionActivity.this);
                                            builder.setTitle("Cập nhật thành công")
                                                    .setMessage("Giao dịch của bạn đã được cập nhật!")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();

                                                            Intent updateDetailIntent = new Intent();
                                                            updateDetailIntent.putExtra("save_success", true); // Gửi kết quả là lưu thành công
                                                            setResult(Activity.RESULT_OK, updateDetailIntent);
                                                            finish();
                                                        }
                                                    });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                            Log.d(TAG, "Update successfully");
                                        } else {
                                            // Update failed
                                            Log.d(TAG, "Update unsuccessfully");
                                        }
                                    }
                                });
                    } else {
                        // Document doesn't exist
                        // Handle error
                    }
                } else {
                    // Error getting document
                    // Handle error
                }
            }
        });
    }
}