package com.example.expensify;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class ThuFragment extends Fragment {
    private int currentMonth;
    private FirebaseFirestore db;

    private String userid;
    private FirebaseAuth auth;
    private int tong_thu_currentMonth;
    private int tong_thu_lastMonth;
    private int luong;
    private int thu_nhap_khac;
    private int tien_chuyen_den;
    private int thu_lai;
    private boolean isDataLoaded = false;
    private int cac_khoan_thu_khac;

    public ThuFragment() {
        // Khởi tạo đối tượng FirebaseFirestore
        db = FirebaseFirestore.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thu, container, false);;
        // Nạp giao diện phù hợp với hướng màn hình
        // Gán sự kiện cho nút cập nhật
        Button updateButton = rootView.findViewById(R.id.updateButton);
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("update", "Cập nhật lại dữ liệu");
                reloadDataAndView(rootView);
            }
        });

        // Nếu dữ liệu chưa được tải, thực hiện tải dữ liệu
        // Ngược lại, cập nhật giao diện với dữ liệu đã có
        if (!isDataLoaded) {
            loadDataFromFirestore(rootView);
        } else {
            updateUI(rootView);
        }

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Màn hình được xoay ngang
            // Xử lý các thay đổi cần thiết ở đây
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Màn hình được xoay dọc
            // Xử lý các thay đổi cần thiết ở đây
        }
    }
    private void loadDataFromFirestore(View rootView){
        tong_thu_currentMonth = 0;
        tong_thu_lastMonth = 0;
        luong = 0;
        thu_nhap_khac = 0;
        tien_chuyen_den = 0;
        thu_lai = 0;
        cac_khoan_thu_khac = 0;

        List<String> salaryList = Arrays.asList("Lương","Salary", "Salaire");
        List<String> otherIncomeList = Arrays.asList("Thu nhập khác","Other Income", "Autres Revenus");
        List<String> interestList = Arrays.asList("Tiền chuyển đến","Interest", "Intérêts");
        List<String> moneyTransfersList = Arrays.asList("Thu lãi","Money Transfers", "Transferts d'Argent");
        List<String> othersList = Arrays.asList("Các khoản thu khác","Others", "Autres");

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH) + 1;

        CollectionReference expensesRef = db.collection("expense");
        db.collection("expense").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timestamp createdAtTimestamp = document.getTimestamp("created_at");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(createdAtTimestamp.getSeconds() * 1000);
                        String user_id = document.getString("user_id");
                        int month = calendar.get(Calendar.MONTH) + 1;
                        String categoryRef = document.getString("category_id");
                        int amount = document.getLong("amount").intValue();
                        String categoryDetail = document.getString("category_detail");
                        if (user_id.equals(new String("user/" + userid))) {
                            if (month == currentMonth) {
                                if (categoryRef != null && categoryRef.equals("category/mQWS7VpkMR6BPlhknobM")) {
                                    tong_thu_currentMonth += amount;
                                    if (salaryList.contains(categoryDetail)) {
                                        luong += amount;
                                    } else if (otherIncomeList.contains(categoryDetail)) {
                                        thu_nhap_khac += amount;
                                    } else if (moneyTransfersList.contains(categoryDetail)) {
                                        tien_chuyen_den += amount;
                                    } else if (interestList.contains(categoryDetail)) {
                                        thu_lai += amount;
                                    } else if (othersList.contains(categoryDetail)) {
                                        cac_khoan_thu_khac += amount;
                                    }
                                }
                            }
                            if (month == (currentMonth - 1)) {
                                if (categoryRef != null && categoryRef.equals("category/mQWS7VpkMR6BPlhknobM")) {
                                    tong_thu_lastMonth += amount;
                                }
                            }

                        }
                    }
                    updateUI(rootView);
                    isDataLoaded = true;
                } else {
                    Log.d("debug", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }

    private void updateUI(View rootView) {
        TextView luongTextView = rootView.findViewById(R.id.luong);
        TextView thuNhapKhacTextView = rootView.findViewById(R.id.thu_nhap_khac);
        TextView tienChuyenDenTextView = rootView.findViewById(R.id.tien_chuyen_den);
        TextView thuLaiTextView = rootView.findViewById(R.id.thu_lai);
        TextView cacKhoanThuKhacTextView = rootView.findViewById(R.id.cac_khoan_thu_khac);
        TextView tongThuThangNayTextView = rootView.findViewById(R.id.tong_thu_thang_nay);
        TextView tongThuThangTruocTextView = rootView.findViewById(R.id.thu_thang_truoc);


// Đặt văn bản cho mỗi TextView từ giá trị tương ứng
        // Sử dụng giá trị từ strings.xml và thiết lập text cho các TextView
        tongThuThangNayTextView.setText(getString(R.string.totalIncome_currentMonth) + ": " + tong_thu_currentMonth + " VND");
        luongTextView.setText(getString(R.string.salary) + ": " + luong + " VND");
        thuNhapKhacTextView.setText(getString(R.string.otherIncome) + ": " + thu_nhap_khac + " VND");
        tienChuyenDenTextView.setText(getString(R.string.moneyTransferred) + ": " + tien_chuyen_den + " VND");
        thuLaiTextView.setText(getString(R.string.interest) + ": " + thu_lai + " VND");
        cacKhoanThuKhacTextView.setText(getString(R.string.otherRevenues) + ": " + cac_khoan_thu_khac + " VND");
        tongThuThangTruocTextView.setText(getString(R.string.totalIncome_lastMonth) + ": " + tong_thu_lastMonth + " VND");



        BarChart barChartNew = rootView.findViewById(R.id.barChart1);
        List<BarEntry> luongEntries = new ArrayList<>();
        luongEntries.add(new BarEntry(0.5f, (float) luong));
        List<BarEntry> thuNhapKhacEntries = new ArrayList<>();
        thuNhapKhacEntries.add(new BarEntry(1.5f, (float) thu_nhap_khac));
        List<BarEntry> tienChuyenDenEntries = new ArrayList<>();
        tienChuyenDenEntries.add(new BarEntry(2.5f, (float) tien_chuyen_den));
        List<BarEntry> thuLaiEntries = new ArrayList<>();
        thuLaiEntries.add(new BarEntry(3.5f, (float) thu_lai));
        List<BarEntry> cacKhoanThuKhacEntries = new ArrayList<>();
        cacKhoanThuKhacEntries.add(new BarEntry(4.5f, (float) cac_khoan_thu_khac));
        BarDataSet luongDataSet = new BarDataSet(luongEntries, "Lương");
        int blueColor = Color.rgb(65, 105, 225);
        luongDataSet.setColor(blueColor);
        BarDataSet thuNhapKhacDataSet = new BarDataSet(thuNhapKhacEntries, "T.NhậpKhác");
        int orangeColor = Color.rgb(255, 165, 0);
        thuNhapKhacDataSet.setColor(orangeColor);
        BarDataSet tienChuyenDenDataSet = new BarDataSet(tienChuyenDenEntries, "Tiền C.Đến");
        int greenColor = Color.rgb(50, 205, 50);
        tienChuyenDenDataSet.setColor(greenColor);
        BarDataSet thuLaiDataSet = new BarDataSet(thuLaiEntries, "ThuLãi");
        int redColor = Color.rgb(255, 99, 71);
        thuLaiDataSet.setColor(redColor);
        BarDataSet cacKhoanThuKhacDataSet = new BarDataSet(cacKhoanThuKhacEntries, "KhoảnKhác");
        int purpleColor = Color.rgb(128, 0, 128);
        cacKhoanThuKhacDataSet.setColor(purpleColor);
        List<IBarDataSet> dataSetsNew = new ArrayList<>();
        dataSetsNew.add(luongDataSet);
        dataSetsNew.add(thuNhapKhacDataSet);
        dataSetsNew.add(tienChuyenDenDataSet);
        dataSetsNew.add(thuLaiDataSet);
        dataSetsNew.add(cacKhoanThuKhacDataSet);
        BarData barDataNew = new BarData(dataSetsNew);
        barDataNew.setBarWidth(0.4f);
        barDataNew.setValueTextSize(10f);
        barChartNew.setData(barDataNew);
        barChartNew.getDescription().setEnabled(false);
        barChartNew.setFitBars(true);
        barChartNew.getXAxis().setAxisMinimum(0f);
        barChartNew.getXAxis().setAxisMaximum(5f);
        barChartNew.getXAxis().setGranularity(1f);
        barChartNew.getAxisLeft().setAxisMinimum(0f);
        barChartNew.getAxisLeft().setAxisMaximum(Math.max((float) luong, (float) thu_nhap_khac)); // Hãy thay getMaxValueAmongFields() bằng hàm tính giá trị lớn nhất
        barChartNew.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Lương", "ThuNhậpKhác", "TiềnChuyểnĐến", "ThuLãi", "KhoảnKhác"}));
        barChartNew.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChartNew.getXAxis().setDrawLabels(false);
        barChartNew.getAxisLeft().setEnabled(false);
        barChartNew.getAxisRight().setEnabled(false);
        barChartNew.setHighlightPerTapEnabled(false);
        barChartNew.setHighlightPerDragEnabled(false);
        barChartNew.invalidate();
    }
    private void reloadDataAndView(View rootView) {
        // Load lại dữ liệu từ Firestore
        loadDataFromFirestore(rootView);
    }
}