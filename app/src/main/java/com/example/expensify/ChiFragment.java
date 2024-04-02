package com.example.expensify;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChiFragment extends Fragment {

    private int currentMonth;
    private FirebaseFirestore db;
    private int tong_chi_currentMonth;
    private int tong_chi_lastMonth;
    private int an_uong, hoa_don_tien_ich, mua_sam, gia_dinh, di_chuyen, suc_khoe, giao_duc, qua_tang_quyen_gop, giai_tri, bao_hiem, dau_tu, cac_chi_phi_khac;
    private boolean isDataLoaded = false;
    public ChiFragment() {
        db= FirebaseFirestore.getInstance();
    }


    public static ChiFragment newInstance(String param1, String param2) {
        ChiFragment fragment = new ChiFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chi, container, false);
        Button updateButton = rootView.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("update","Cap nhat lai du lieu");
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

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        tong_chi_currentMonth = 0;
        tong_chi_lastMonth = 0;
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
                        String categoryRef = document.getString("category_id");
                        int amount = document.getLong("amount").intValue();
                        String categoryDetail = document.getString("category_detail");
                        if (month == currentMonth) {
                            if (categoryRef != null && categoryRef.equals("category/0sZQzPZx64wLdM4aauqZ")) {
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
                        }
                        if (month == (currentMonth - 1)) {
                            if (categoryRef != null && categoryRef.equals("category/0sZQzPZx64wLdM4aauqZ")) {
                                tong_chi_lastMonth += amount;
                            }
                        }
                        updateUI(rootView);
                        isDataLoaded = true;
                    }
                } else {
                    Log.d("debug", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }

    private void updateUI(View rootView) {
        TextView detail1 = rootView.findViewById(R.id.detail1);
        TextView detail2 = rootView.findViewById(R.id.detail2);
        TextView detail3 = rootView.findViewById(R.id.detail3);
        TextView detail4 = rootView.findViewById(R.id.detail4);
        TextView detail5 = rootView.findViewById(R.id.detail5);
        TextView detail6 = rootView.findViewById(R.id.detail6);
        TextView detail7 = rootView.findViewById(R.id.detail7);
        TextView detail8 = rootView.findViewById(R.id.detail8);
        TextView detail9 = rootView.findViewById(R.id.detail9);
        TextView detail10 = rootView.findViewById(R.id.detail10);
        TextView detail11 = rootView.findViewById(R.id.detail11);
        TextView detail12 = rootView.findViewById(R.id.detail12);
        TextView tongChiThangNayTextView = rootView.findViewById(R.id.tong_chi_thang_nay);
        TextView tongChiThangTruocTextView = rootView.findViewById(R.id.chi_thang_truoc);


        detail1.setText("1. Ăn uống: " + an_uong + " VND");
        detail2.setText("2. Hóa đơn Tiện ích: " + hoa_don_tien_ich + " VND");
        detail3.setText("3. Mua sắm: " + mua_sam + " VND");
        detail4.setText("4. Gia đình: " + gia_dinh + " VND");
        detail5.setText("5. Di chuyển: " + di_chuyen + " VND");
        detail6.setText("6. Sức khỏe: " + suc_khoe + " VND");
        detail7.setText("7. Giáo dục: " + giao_duc + " VND");
        detail8.setText("8. Quà tặng Quyên góp: " + qua_tang_quyen_gop + " VND");
        detail9.setText("9. Giải trí: " + giai_tri + " VND");
        detail10.setText("10. Bảo hiểm: " + bao_hiem + " VND");
        detail11.setText("11. Đầu tư: " + dau_tu + " VND");
        detail12.setText("12. Các chi phí khác: " + cac_chi_phi_khac + " VND");
        tongChiThangNayTextView.setText("Tổng chi tháng này: "+ tong_chi_currentMonth+" VND");
        tongChiThangTruocTextView.setText("Tổng chi tháng trước: "+ tong_chi_lastMonth+" VND");


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

        PieChart pieChart = rootView.findViewById(R.id.pieChart);

        // Tạo danh sách các PieEntry (các phần của biểu đồ Pie)
        List<PieEntry> entries = new ArrayList<>();
        if (an_uong > 0) entries.add(new PieEntry(an_uong, "Ăn uống"));
        if (hoa_don_tien_ich > 0) entries.add(new PieEntry(hoa_don_tien_ich, "Hóa đơn Tiện ích"));
        if (mua_sam > 0) entries.add(new PieEntry(mua_sam, "Mua sắm"));
        if (gia_dinh > 0) entries.add(new PieEntry(gia_dinh, "Gia đình"));
        if (di_chuyen > 0) entries.add(new PieEntry(di_chuyen, "Di chuyển"));
        if (suc_khoe > 0) entries.add(new PieEntry(suc_khoe, "Sức khỏe"));
        if (giao_duc > 0) entries.add(new PieEntry(giao_duc, "Giáo dục"));
        if (qua_tang_quyen_gop > 0) entries.add(new PieEntry(qua_tang_quyen_gop, "Quà tặng Quyên góp"));
        if (giai_tri > 0) entries.add(new PieEntry(giai_tri, "Giải trí"));
        if (bao_hiem > 0) entries.add(new PieEntry(bao_hiem, "Bảo hiểm"));
        if (dau_tu > 0) entries.add(new PieEntry(dau_tu, "Đầu tư"));
        if (cac_chi_phi_khac > 0) entries.add(new PieEntry(cac_chi_phi_khac, "Các chi phí khác"));

        // Tạo PieDataSet từ danh sách các PieEntry
        PieDataSet dataSet = new PieDataSet(entries, "Labels");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Thiết lập màu cho các phần của biểu đồ
        dataSet.setValueTextSize(13f);
        // Tạo PieData từ PieDataSet
        PieData data1 = new PieData(dataSet);

        // Cấu hình biểu đồ PieChart
        pieChart.setData(data1);
        // Đặt kích thước của vòng tròn trong biểu đồ PieChart
        pieChart.setHoleRadius(0f); // Đặt bán kính của vòng tròn
        pieChart.setTransparentCircleRadius(0f); // Đặt bán kính của vòng tròn trong suốt bên ngoài
        pieChart.setEntryLabelColor(Color.TRANSPARENT); // Đặt màu chữ nền trong suốt
        pieChart.setEntryLabelTextSize(20f);
        pieChart.setCenterText("Chi %");
        pieChart.setUsePercentValues(true); // Sử dụng giá trị phần trăm thay vì giá trị thực
        pieChart.getDescription().setEnabled(false); // Tắt mô tả
        pieChart.invalidate(); // Cập nhật biểu đồ

    }
    private void reloadDataAndView(View rootView) {

        // Load lại dữ liệu từ Firestore
        loadDataFromFirestore(rootView);
    }
}