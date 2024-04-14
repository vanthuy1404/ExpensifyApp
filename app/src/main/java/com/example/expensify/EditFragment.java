package com.example.expensify;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditFragment() {
        // Required empty public constructor
    }
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
    int detailId;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
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
        loadCategory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        // Set text view
        EditText edtTxtDate = view.findViewById(R.id.edtTxtDate);
        EditText edtTxtNote = view.findViewById(R.id.edtTxtNote);
        EditText edtTxtAmount = view.findViewById(R.id.edtTxtAmount);
        Spinner spnCategory = view.findViewById(R.id.spinnerCategory);
        Spinner spnDetail = view.findViewById(R.id.spinnerDetail);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Retrieve the TransactionModel object from the arguments Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            TransactionModel transactionModel = (TransactionModel) bundle.getSerializable("transaction");

            if (transactionModel != null) {
                String categoryDetail = transactionModel.getCategoryDetail();
                String note = transactionModel.getNote();
                double amount = transactionModel.getAmount();
                String date = transactionModel.getCreatedAt();
                DecimalFormat decimalFormat = new DecimalFormat("####");
                expenseID = transactionModel.getId();

                edtTxtDate.setText(date);
                edtTxtNote.setText(note);
                edtTxtAmount.setText(decimalFormat.format(amount));
            }
        }

        // Show date picker
        ImageView imgCalendar = view.findViewById(R.id.datePicker);
        imgCalendar.findViewById(R.id.datePicker).setOnClickListener(e -> {
            new DatePickerDialog(
                    getContext(),
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

        // Handle button Back
        Button btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerExpense);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDetail.setAdapter(adapter);
                } else if (("Income").equals(item.toString()) || ("Revenu").equals(item.toString()) || ("Khoản thu").equals(item.toString())){
                    categoryID = "category/mQWS7VpkMR6BPlhknobM";
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerIncome);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDetail.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Handle button Save
        btnSave.setOnClickListener(v -> {
            String newNote = edtTxtNote.getText().toString();
            String newDetail = spnDetail.getSelectedItem().toString();
            Number newAmount = null;
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

        return view;
    }

    private void loadCategory() {
        CollectionReference categoryCollectionRef = firebaseFirestore.collection("category");
        categoryCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Spinner spnCategory = (Spinner) getView().findViewById(R.id.spinnerCategory);
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCategory.setAdapter(adapter);
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
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("Cập nhật thành công")
                                                    .setMessage("Giao dịch của bạn đã được cập nhật!")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
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