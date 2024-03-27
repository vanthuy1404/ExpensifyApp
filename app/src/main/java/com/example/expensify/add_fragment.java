package com.example.expensify;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link add_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class add_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final Calendar myCalendar = Calendar.getInstance();
    EditText editText;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore fireStore;

    private String categoryID;

    String[] category={"Expense", "Income"};

    String[] arraySpinnerExpense = new String[]{
            "Ăn uống", "Hóa đơn & Tiện ích", "Mua sắm", "Gia đình", "Di chuyển", "Sức khỏe", "Giáo dục", "Quà tặng & Quyên góp", "Giải trí", "Bảo hiểm", "Đầu tư", "Các chi phí khác"
    };

    String[] arraySpinnerIncome = new String[]{
            "Lương", "Thưởng", "Lãi", "Tiền chuyển đến", "Thu nhập khác"
    };

    private  interface  DiaglogInterface {
        void onClick(DialogInterface dialog, int which);

    };

    public add_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment add_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static add_fragment newInstance(String param1, String param2) {
        add_fragment fragment = new add_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireStore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void getDataFromUser() {
        EditText editTextExpenseAmount = getView().findViewById(R.id.editTextExpenseAmount);
        EditText editTextExpenseContent = getView().findViewById(R.id.editTextExpenseContent);
        EditText editTextDate = getView().findViewById(R.id.editTextDate);
        Spinner spinnerSelectionCategory = getView().findViewById(R.id.spinnerSelectionCategory);

        String expenseAmount = editTextExpenseAmount.getText().toString();
        String expenseContent = editTextExpenseContent.getText().toString();
        String dateString = editTextDate.getText().toString();
        String expenseSelectionCategory = spinnerSelectionCategory.getSelectedItem().toString();

        Map<String, String> expense = new HashMap<>();

        expense.put("created_at", dateString);
        expense.put("amount", expenseAmount);
        expense.put("note", expenseContent);
        expense.put("catogory_detail", expenseSelectionCategory);
        expense.put("catogory_id", categoryID);

        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .create();
        ad.setCancelable(false);
        ad.setTitle("Xác nhận thêm chi tiêu");
        ad.setMessage("Số tiền là: " + expenseAmount + "\n" +
                "Nội dung: " + expenseContent + "\n" +
                "Ngày: " + dateString + "\n" +
                "Loại chi tiêu: " + expenseSelectionCategory + "\n");

        ad.setButton(AlertDialog.BUTTON_POSITIVE, "Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });

        ad.show();

        fireStore.collection("expense").add(expense);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button add = getView().findViewById(R.id.buttonAddExpense);
        add.setOnClickListener((e) -> {
            getDataFromUser();
        });
        getView().findViewById(R.id.datePicker).setOnClickListener(e -> {
            new DatePickerDialog(
                    getView().getContext(),
                    (v, y, m, d) -> {
                        myCalendar.set(Calendar.YEAR, y);
                        myCalendar.set(Calendar.MONTH, m);
                        myCalendar.set(Calendar.DATE, d);

                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                        ((EditText) getView().findViewById(R.id.editTextDate))
                                .setText(sdf.format(myCalendar.getTime()));
                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get((Calendar.DATE))).show();
        });

        Spinner categorySelect = getView().findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySelect.setAdapter(adapter);
        categorySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Object item = parent.getItemAtPosition(position);

                if (("Expense").equals(item.toString())) {
                    categoryID = "0sZQzPZx64wLdM4aauqZ";
                    Spinner spinnerSelectionCategory = (Spinner) getView().findViewById(R.id.spinnerSelectionCategory);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerExpense);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSelectionCategory.setAdapter(adapter);
                } else if (("Income").equals(item.toString())) {
                    categoryID = "mQWS7VpkMR6BPlhknobM";
                    Spinner spinnerSelectionCategory = (Spinner) getView().findViewById(R.id.spinnerSelectionCategory);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerIncome);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSelectionCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_fragment, container, false);
    }
}