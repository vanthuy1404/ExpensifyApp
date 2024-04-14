package com.example.expensify;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ChiFragment extends Fragment {

    private FirebaseAuth auth;

    public String userid;
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
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Expensify")
                            .setContentText("Cập nhật dữ liệu thành công")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSound(null);
                    notificationManager.notify(1, builder.build());
                } else {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
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
        List<String> anuongList = Arrays.asList("Ăn uống", "Food & Beverage", "Nourriture et Boissons");
        List<String> hoadontienichList = Arrays.asList("Hóa đơn & Tiện ích","Factures et Services Publics", "Bills & Utilities");
        List<String> muaHangList = Arrays.asList("Mua sắm","Achats", "Shopping");
        List<String> giaDinhList = Arrays.asList("Gia đình","Famille", "Family");
        List<String> diChuyenList = Arrays.asList("Di chuyển","Transport", "Transportation");
        List<String> sucKhoeList = Arrays.asList("Sức khỏe","Santé", "Health");
        List<String> giaoDucList = Arrays.asList("Giáo dục","Éducation", "Education");
        List<String> quaTangVaQuyenGopList = Arrays.asList("Quà tặng & Quyên góp","Cadeaux et Dons", "Gifts & Donations");
        List<String> giaiTriList = Arrays.asList("Giải trí","Divertissement", "Entertainment");
        List<String> baoHiemList = Arrays.asList("Bảo hiểm","Assurance", "Insurance");
        List<String> dauTuList = Arrays.asList("Đầu tư","Investissements", "Investments");
        List<String> cacKhoanChiKhacList = Arrays.asList("Các chi phí khác","Autres Dépenses", "Other Expenses");



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
                        String user_id = document.getString("user_id");
                        String categoryRef = document.getString("category_id");
                        int amount = document.getLong("amount").intValue();
                        String categoryDetail = document.getString("category_detail");

                        if (user_id.equals(new String("user/" + userid))) {
                            if (month == currentMonth) {
                                if (categoryRef != null && categoryRef.equals("category/0sZQzPZx64wLdM4aauqZ")) {
                                    tong_chi_currentMonth += amount;
                                    if (anuongList.contains(categoryDetail)) {
                                        an_uong += amount;
                                    } else if (hoadontienichList.contains(categoryDetail)) {
                                        hoa_don_tien_ich += amount;
                                    } else if (muaHangList.contains(categoryDetail)) {
                                        mua_sam += amount;
                                    } else if (giaDinhList.contains(categoryDetail)) {
                                        gia_dinh += amount;
                                    } else if (diChuyenList.contains(categoryDetail)) {
                                        di_chuyen += amount;
                                    } else if (sucKhoeList.contains(categoryDetail)) {
                                        suc_khoe += amount;
                                    } else if (giaoDucList.contains(categoryDetail)) {
                                        giao_duc += amount;
                                    } else if (quaTangVaQuyenGopList.contains(categoryDetail)) {
                                        qua_tang_quyen_gop += amount;
                                    } else if (giaiTriList.contains(categoryDetail)) {
                                        giai_tri += amount;
                                    } else if (baoHiemList.contains(categoryDetail)) {
                                        bao_hiem += amount;
                                    } else if (dauTuList.contains(categoryDetail)) {
                                        dau_tu += amount;
                                    } else if (cacKhoanChiKhacList.contains(categoryDetail)) {
                                        cac_chi_phi_khac += amount;
                                    }
                                }
                            }
                            if (month == (currentMonth - 1)) {
                                if (categoryRef != null && categoryRef.equals("category/0sZQzPZx64wLdM4aauqZ")) {
                                    tong_chi_lastMonth += amount;
                                }
                            }
                        }
                    }
                    // Di chuyển lệnh cập nhật UI và đánh dấu dữ liệu đã tải vào ngoài vòng lặp
                    updateUI(rootView);
                    isDataLoaded = true;
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


        // Sử dụng giá trị từ strings.xml và thiết lập text cho các TextView
        detail1.setText(getString(R.string.foodAndDrink) + ": " + an_uong + " VND");
        detail2.setText(getString(R.string.billAndUtilities) + ": " + hoa_don_tien_ich + " VND");
        detail3.setText(getString(R.string.shopping) + ": " + mua_sam + " VND");
        detail4.setText(getString(R.string.family) + ": " + gia_dinh + " VND");
        detail5.setText(getString(R.string.transportations) + ": " + di_chuyen + " VND");
        detail6.setText(getString(R.string.health) + ": " + suc_khoe + " VND");
        detail7.setText(getString(R.string.education) + ": " + giao_duc + " VND");
        detail8.setText(getString(R.string.giftAndDonation) + ": " + qua_tang_quyen_gop + " VND");
        detail9.setText(getString(R.string.entertainment) + ": " + giai_tri + " VND");
        detail10.setText(getString(R.string.insurance) + ": " + bao_hiem + " VND");
        detail11.setText(getString(R.string.investment) + ": " + dau_tu + " VND");
        detail12.setText(getString(R.string.otherExpenses) + ": " + cac_chi_phi_khac + " VND");
        tongChiThangNayTextView.setText(getString(R.string.totalOutcome_currentMonth) + ": " + tong_chi_currentMonth + " VND");
        tongChiThangTruocTextView.setText(getString(R.string.totalOutcome_lastMonth) + ": " + tong_chi_lastMonth + " VND");



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