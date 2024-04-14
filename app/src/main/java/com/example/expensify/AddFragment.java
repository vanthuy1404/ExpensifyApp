package com.example.expensify;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

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
    private FirebaseAuth auth;
    Spinner languageChange;
    private String categoryID;

    String[] category= {"Chi phí", "Thu nhập"};

    String[] categoryEng={ "Expense", "Income"};

    String[] categoryFra= {"Dépense", "Revenu"};

    String[] arraySpinnerExpense = new String[]{
            "Ăn uống", "Hóa đơn & Tiện ích", "Mua sắm", "Gia đình", "Di chuyển", "Sức khỏe", "Giáo dục", "Quà tặng & Quyên góp", "Giải trí", "Bảo hiểm", "Đầu tư", "Các chi phí khác"
    };

    String[] arraySpinnerExpenseEng = new String[]{
            "Food & Beverage",
            "Bills & Utilities",
            "Shopping",
            "Family",
            "Transportation",
            "Health",
            "Education",
            "Gifts & Donations",
            "Entertainment",
            "Insurance",
            "Investments",
            "Other Expenses"
    };

    String[] arraySpinnerExpenseFra = new String[]{
            "Nourriture et Boissons",
            "Factures et Services Publics",
            "Achats",
            "Famille",
            "Transport",
            "Santé",
            "Éducation",
            "Cadeaux et Dons",
            "Divertissement",
            "Assurance",
            "Investissements",
            "Autres Dépenses"
    };

    String[] arraySpinnerIncome = new String[]{
            "Lương", "Các khoản thu khác", "Lãi", "Tiền chuyển đến", "Thu nhập khác"
    };

    String[] arraySpinnerIncomeEng = new String[]{
            "Salary",
            "Other Income",
            "Interest",
            "Money Transfers",
            "Others"
    };

    String[] arraySpinnerIncomeFra = new String[]{
            "Salaire",
            "Autres Revenus",
            "Intérêts",
            "Transferts d'Argent",
            "Autres"
    };

    public static final String[] languages = { "Vietnamese", "English", "French" };

    private ArrayAdapter<String> adapter;

    private  interface  DiaglogInterface {
        void onClick(DialogInterface dialog, int which);

    };

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private SharedPreferences userLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireStore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        auth = FirebaseAuth.getInstance();
    }

    private void ResetInputField() {
        EditText editTextExpenseAmount = getView().findViewById(R.id.editTextExpenseAmount);
        EditText editTextExpenseContent = getView().findViewById(R.id.editTextExpenseContent);
        EditText editTextDate = getView().findViewById(R.id.editTextDate);
        Spinner spinnerSelectionCategory = getView().findViewById(R.id.spinnerSelectionCategory);

        editTextExpenseAmount.setText("");
        editTextExpenseContent.setText("");
        editTextDate.setText("");
        spinnerSelectionCategory.setSelection(0);
    }

    private void getDataFromUser() throws ParseException {
        EditText editTextExpenseAmount = getView().findViewById(R.id.editTextExpenseAmount);
        Number finalAmount = NumberFormat.getInstance().parse(editTextExpenseAmount.getText().toString());
        EditText editTextExpenseContent = getView().findViewById(R.id.editTextExpenseContent);
        EditText editTextDate = getView().findViewById(R.id.editTextDate);
        Spinner spinnerSelectionCategory = getView().findViewById(R.id.spinnerSelectionCategory);

        String expenseAmount = editTextExpenseAmount.getText().toString();
        String expenseContent = editTextExpenseContent.getText().toString();
        String dateString = editTextDate.getText().toString();
        String expenseSelectionCategory = spinnerSelectionCategory.getSelectedItem().toString();
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> expense = new HashMap<>();

        expense.put("created_at", myCalendar.getTime());
        expense.put("amount", finalAmount);
        expense.put("note", expenseContent);
        expense.put("category_detail", expenseSelectionCategory);
        expense.put("category_id", categoryID);
        expense.put("user_id", "user/" + userId);

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
        auth = FirebaseAuth.getInstance();
        Button add = getView().findViewById(R.id.buttonAddExpense);
        add.setOnClickListener((e) -> {
            try {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Expensify")
                            .setContentText("Thêm thành công")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSound(null);
                    notificationManager.notify(1, builder.build());
                } else {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
                getDataFromUser();
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            ResetInputField();
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
            myCalendar.getTime();
        });

        userLanguage = requireActivity().getSharedPreferences("userLanguage", requireActivity().MODE_PRIVATE);

        languageChange = getView().findViewById(R.id.languageSpinner);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageChange.setAdapter(languageAdapter);
        languageChange.setSelection(0);

        String currentLocale = userLanguage.getString("language", "vi");
        String activityLocale = requireActivity().getResources().getConfiguration().getLocales().get(0).getLanguage();
        Spinner categorySelect = getView().findViewById(R.id.spinnerCategory);

        if (!activityLocale.equals(currentLocale)) {
            setLocale(currentLocale);
        }
        if (currentLocale.equals("vi")) {
            languageChange.setSelection(0);
        }
        else if (currentLocale.equals("en")) {
            languageChange.setSelection(1);
        }
        else if (currentLocale.equals("fr")) {
            languageChange.setSelection(2);
        }
        languageChange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (("Vietnamese").equals(item.toString())) {
                    String currentLocale = userLanguage.getString("language", "vi");
                    adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, category);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySelect.setAdapter(adapter);
                    categorySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // An item was selected. You can retrieve the selected item using
                            // parent.getItemAtPosition(pos)
                            Object item = parent.getItemAtPosition(position);

                            if (("Expense").equals(item.toString()) || ("Dépense").equals(item.toString()) || ("Chi phí").equals(item.toString())) {
                                categoryID = "category/0sZQzPZx64wLdM4aauqZ";
                                Spinner spinnerSelectionCategory = (Spinner) getView().findViewById(R.id.spinnerSelectionCategory);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerExpense);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerSelectionCategory.setAdapter(adapter);
                            } else if (("Income").equals(item.toString()) || ("Revenu").equals(item.toString()) || ("Thu nhập").equals(item.toString())){
                                categoryID = "category/mQWS7VpkMR6BPlhknobM";
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
                    if (!currentLocale.equals("vi")) {
                        setLocale("vi");
                    }
                }
                else if (("English").equals(item.toString())) {
                    String currentLocale = userLanguage.getString("language", "en");
                    adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, categoryEng);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySelect.setAdapter(adapter);
                    categorySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // An item was selected. You can retrieve the selected item using
                            // parent.getItemAtPosition(pos)
                            Object item = parent.getItemAtPosition(position);

                            if (("Expense").equals(item.toString()) || ("Dépense").equals(item.toString()) || ("Chi phí").equals(item.toString())) {
                                categoryID = "category/0sZQzPZx64wLdM4aauqZ";
                                Spinner spinnerSelectionCategory = (Spinner) getView().findViewById(R.id.spinnerSelectionCategory);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerExpenseEng);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerSelectionCategory.setAdapter(adapter);
                            } else if (("Income").equals(item.toString()) || ("Revenu").equals(item.toString()) || ("Thu nhập").equals(item.toString())){
                                categoryID = "category/mQWS7VpkMR6BPlhknobM";
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
                    if (!currentLocale.equals("en")) {
                        setLocale("en");
                    }
                }
                else if (("French").equals(item.toString())) {
                    String currentLocale = userLanguage.getString("language", "fr");
                    adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, categoryFra);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySelect.setAdapter(adapter);
                    categorySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // An item was selected. You can retrieve the selected item using
                            // parent.getItemAtPosition(pos)
                            Object item = parent.getItemAtPosition(position);

                            if (("Expense").equals(item.toString()) || ("Dépense").equals(item.toString()) || ("Chi phí").equals(item.toString())) {
                                categoryID = "category/0sZQzPZx64wLdM4aauqZ";
                                Spinner spinnerSelectionCategory = (Spinner) getView().findViewById(R.id.spinnerSelectionCategory);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, arraySpinnerExpenseFra);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerSelectionCategory.setAdapter(adapter);
                            } else if (("Income").equals(item.toString()) || ("Revenu").equals(item.toString()) || ("Thu nhập").equals(item.toString())){
                                categoryID = "category/mQWS7VpkMR6BPlhknobM";
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
                    if (!currentLocale.equals("fr")) {
                        setLocale("fr");
                    }
                }
            }
            public void onNothingSelected (AdapterView < ? > parent){

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_fragment, container, false);
    }

    private void setLocale(String localeCode){
        userLanguage.edit().putString("language", localeCode).apply();
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(configuration, displayMetrics);
        configuration.locale = new Locale(localeCode.toLowerCase());
        resources.updateConfiguration(configuration, displayMetrics);
        requireActivity().recreate();
    }
}