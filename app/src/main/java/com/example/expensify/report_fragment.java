package com.example.expensify;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class report_fragment extends Fragment {

    private int currentMonth;
    private FirebaseFirestore db;
    private int tong_chi_currentMonth;
    private int tong_chi_lastMonth;
    private int tong_thu_currentMonth;
    private int tong_thu_lastMonth;

    private int an_uong, hoa_don_tien_ich, mua_sam, gia_dinh, di_chuyen, suc_khoe, giao_duc, qua_tang_quyen_gop, giai_tri, bao_hiem, dau_tu, cac_chi_phi_khac;
    private int luong;
    private int thu_nhap_khac;
    private int tien_chuyen_den;
    private int thu_lai;

    private int cac_khoan_thu_khac;
    private boolean isDataLoaded = false;

    public report_fragment() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_fragment, container, false);

        Button updateButton = rootView.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadDataAndView(rootView); // Gọi phương thức để load lại dữ liệu và cập nhật UI
            }
        });
        if (!isDataLoaded) {
            loadDataFromFirestore(rootView);
        } else {
            updateUI(rootView);
        }

        return rootView;

    }

    private void loadDataFromFirestore(View rootView) {
        tong_chi_currentMonth = 0;
        tong_chi_lastMonth = 0;
        tong_thu_currentMonth = 0;
        tong_thu_lastMonth = 0;
        an_uong = 0;
        hoa_don_tien_ich = 0;
        mua_sam = 0;
        gia_dinh = 0;
        di_chuyen = 0;
        suc_khoe = 0;
        giao_duc = 0;
        qua_tang_quyen_gop = 0;
        giai_tri = 0;
        bao_hiem = 0;
        dau_tu = 0;
        cac_chi_phi_khac = 0;
        luong = 0;
        thu_nhap_khac = 0;
        tien_chuyen_den = 0;
        thu_lai = 0;
        cac_khoan_thu_khac = 0;


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

                        int month = calendar.get(Calendar.MONTH) + 1;
                        DocumentReference categoryRef = (DocumentReference) document.get("category_id");
                        int amount = document.getLong("amount").intValue();
                        String categoryDetail = document.getString("category_detail");

                        if (month == currentMonth) {
                            if (categoryRef != null && categoryRef.getPath().equals("category/0sZQzPZx64wLdM4aauqZ")) {
                                tong_chi_currentMonth += amount;
                                if (categoryDetail.equals("Ăn uống")) {
                                    an_uong += amount;
                                } else if (categoryDetail.equals("Hóa đơn & Tiện ích")) {
                                    hoa_don_tien_ich += amount;
                                } else if (categoryDetail.equals("Mua sắm")) {
                                    mua_sam += amount;
                                } else if (categoryDetail.equals("Gia đình")) {
                                    gia_dinh += amount;
                                } else if (categoryDetail.equals("Di chuyển")) {
                                    di_chuyen += amount;
                                } else if (categoryDetail.equals("Sức khỏe")) {
                                    suc_khoe += amount;
                                } else if (categoryDetail.equals("Giáo dục")) {
                                    giao_duc += amount;
                                } else if (categoryDetail.equals("Quà tặng & Quyên góp")) {
                                    qua_tang_quyen_gop += amount;
                                } else if (categoryDetail.equals("Giải trí")) {
                                    giai_tri += amount;
                                } else if (categoryDetail.equals("Bảo hiểm")) {
                                    bao_hiem += amount;
                                } else if (categoryDetail.equals("Đầu tư")) {
                                    dau_tu += amount;
                                } else if (categoryDetail.equals("Các chi phí khác")) {
                                    cac_chi_phi_khac += amount;
                                }
                            }
                            if (categoryRef != null && categoryRef.getPath().equals("category/mQWS7VpkMR6BPlhknobM")) {
                                tong_thu_currentMonth += amount;
                                if (categoryDetail.equals("Lương")) {
                                    luong += amount;
                                } else if (categoryDetail.equals("Thu nhập khác")) {
                                    thu_nhap_khac += amount;
                                } else if (categoryDetail.equals("Tiền chuyển đến")) {
                                    tien_chuyen_den += amount;
                                } else if (categoryDetail.equals("Thu lãi")) {
                                    thu_lai += amount;
                                } else if (categoryDetail.equals("Các khoản thu khác")) {
                                    cac_khoan_thu_khac += amount;
                                }
                            }
                        }

                        if (month == (currentMonth - 1)) {
                            if (categoryRef != null && categoryRef.getPath().equals("category/0sZQzPZx64wLdM4aauqZ")) {
                                tong_chi_lastMonth += amount;
                            }
                            if (categoryRef != null && categoryRef.getPath().equals("category/mQWS7VpkMR6BPlhknobM")) {
                                tong_thu_lastMonth += amount;
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
        BarChart barChart = rootView.findViewById(R.id.barChart);
        List<BarEntry> thuEntries = new ArrayList<>();
        thuEntries.add(new BarEntry(0.5f, (float) tong_thu_currentMonth));
        thuEntries.add(new BarEntry(1.5f, (float) tong_thu_lastMonth));
        List<BarEntry> chiEntries = new ArrayList<>();
        chiEntries.add(new BarEntry(0.5f, (float) tong_chi_currentMonth));
        chiEntries.add(new BarEntry(1.5f,(float) tong_chi_lastMonth));
        BarDataSet thuDataSet = new BarDataSet(thuEntries, "Thu");
        int blueSkyColor = Color.rgb(135, 206, 235);
        thuDataSet.setColor(blueSkyColor);
        BarDataSet chiDataSet = new BarDataSet(chiEntries, "Chi");
        int lightYellowColor = Color.rgb(235, 220, 56);
        chiDataSet.setColor(lightYellowColor);
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(thuDataSet);
        dataSets.add(chiDataSet);
        // Điều chỉnh vị trí của nhãn "Thu" và "Chi"
        thuDataSet.setLabel("Thu");

        BarData barData = new BarData(dataSets);
        barData.setBarWidth(0.4f);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(2f);
        barChart.getXAxis().setGranularity(1f);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(Math.max((float) tong_thu_currentMonth, (float) tong_thu_lastMonth));
        barChart.groupBars(0f, 0.2f, 0f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Tháng này", "Tháng trước"}));
        xAxis.setLabelCount(2); // Số lượng nhãn trên trục x
        xAxis.setGranularityEnabled(true);
        barChart.getXAxis().setEnabled(true);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getAxisLeft().setEnabled(false); // Ẩn số mốc ở bên trái
        barChart.getAxisRight().setEnabled(false); // Ẩn số mốc ở bên phải
        barChart.setHighlightPerTapEnabled(false); // Ngăn chặn việc zoom khi click vào cột
        barChart.setHighlightPerDragEnabled(false); // Ngăn chặn việc zoom khi kéo
        barChart.invalidate();
// Tạo danh sách dữ liệu cho biểu đồ PieChart chi tiêu
        List<DataEntry> data = new ArrayList<>();
        if (an_uong > 0) data.add(new ValueDataEntry("Ăn uống", an_uong));
        if (hoa_don_tien_ich > 0) data.add(new ValueDataEntry("Hóa đơn & Tiện ích", hoa_don_tien_ich));
        if (mua_sam > 0) data.add(new ValueDataEntry("Mua sắm", mua_sam));
        if (gia_dinh > 0) data.add(new ValueDataEntry("Gia đình", gia_dinh));
        if (di_chuyen > 0) data.add(new ValueDataEntry("Di chuyển", di_chuyen));
        if (suc_khoe > 0) data.add(new ValueDataEntry("Sức khỏe", suc_khoe));
        if (giao_duc > 0) data.add(new ValueDataEntry("Giáo dục", giao_duc));
        if (qua_tang_quyen_gop > 0) data.add(new ValueDataEntry("Quà tặng & Quyên góp", qua_tang_quyen_gop));
        if (giai_tri > 0) data.add(new ValueDataEntry("Giải trí", giai_tri));
        if (bao_hiem > 0) data.add(new ValueDataEntry("Bảo hiểm", bao_hiem));
        if (dau_tu > 0) data.add(new ValueDataEntry("Đầu tư", dau_tu));
        if (cac_chi_phi_khac > 0) data.add(new ValueDataEntry("Các chi phí khác", cac_chi_phi_khac));

// Tạo biểu đồ PieChart và đặt dữ liệu cho nó
        Pie pie = AnyChart.pie();
        pie.data(data);
        pie.title("Chi");
// Hiển thị biểu đồ PieChart chi tiêu
        AnyChartView anyChartView = rootView.findViewById(R.id.pieChart);
        anyChartView.setChart(pie);

// barchat thu
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
        BarDataSet thuNhapKhacDataSet = new BarDataSet(thuNhapKhacEntries, "ThuNhậpKhác");
        int orangeColor = Color.rgb(255, 165, 0);
        thuNhapKhacDataSet.setColor(orangeColor);
        BarDataSet tienChuyenDenDataSet = new BarDataSet(tienChuyenDenEntries, "TiềnChuyểnĐến");
        int greenColor = Color.rgb(50, 205, 50);
        tienChuyenDenDataSet.setColor(greenColor);
        BarDataSet thuLaiDataSet = new BarDataSet(thuLaiEntries, "ThuLãi");
        int redColor = Color.rgb(255, 99, 71);
        thuLaiDataSet.setColor(redColor);
        BarDataSet cacKhoanThuKhacDataSet = new BarDataSet(cacKhoanThuKhacEntries, "CácKhoảnKhác");
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