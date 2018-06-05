package mousepaint.taoqiyang.com.ignore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptor;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mousepaint.taoqiyang.com.ignore.beans.Monitor;
import mousepaint.taoqiyang.com.ignore.util.DividerLine;
import mousepaint.taoqiyang.com.ignore.util.LDThreadM;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TencentLocationListener, TencentMap.OnMarkerClickListener, TencentMap.OnInfoWindowClickListener, TencentMap.OnMapClickListener {

    private MapView mapView;
    private TencentMap map;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
//    private View llTitle;

    private View viewLocate;

    private TencentLocationRequest locationRequest;
    private TencentLocationManager locationManager;
    private Marker locationMarker;
    private Marker lastShowingInfoMarker;

    private List<Marker> monitorMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21 && getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

//        llTitle = findViewById(R.id.llTitle);
        mapView = (MapView) findViewById(R.id.mapView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        findViewById(R.id.ivZoomFull).setOnClickListener(this);
        findViewById(R.id.ivZoomIn).setOnClickListener(this);
        findViewById(R.id.ivZoomOut).setOnClickListener(this);
        viewLocate = findViewById(R.id.ivLocate);
        viewLocate.setOnClickListener(this);


        bottomSheetBehavior = BottomSheetBehavior.from(recyclerView);
        bottomSheetBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().density * 100));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState != BottomSheetBehavior.STATE_DRAGGING) {
                    return;
                }
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                if (layoutParams.topMargin == 0) {
                    layoutParams.topMargin = (int) (getResources().getDisplayMetrics().density * 35);
                    recyclerView.setLayoutParams(layoutParams);
                    bottomSheetBehavior.setBottomSheetCallback(null);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerLine decoration = new DividerLine();
        decoration.setColor(Color.BLACK);
        decoration.setSize(getResources().getDimensionPixelSize(R.dimen.util_theme_dimens_height_divider));
        recyclerView.addItemDecoration(decoration);

        initMapView();

        getMonitorData();
    }

    private void initMapView() {
        UiSettings uiSettings = mapView.getUiSettings();
        uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_LEFT_BOTTOM);
        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScaleControlsEnabled(true);

        map = mapView.getMap();
        map.setCenter(new LatLng(39.0, 116.0));
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnMapClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivZoomFull:
                map.setZoom(10);
                break;

            case R.id.ivZoomIn:
                map.zoomIn();
                break;

            case R.id.ivZoomOut:
                map.zoomOut();
                break;

            case R.id.ivLocate:
                requestLocate();
                break;
        }
    }

    private void requestLocate() {
        if (!viewLocate.isEnabled()) {
            return;
        }
        viewLocate.setEnabled(false);
        if (locationRequest == null) {
            locationManager = TencentLocationManager.getInstance(getApplicationContext());
            locationRequest = TencentLocationRequest.create();
        }

        int result = locationManager.requestLocationUpdates(locationRequest, this);
        if (result != 0) {
            Toast.makeText(this, "启动定位服务失败code:" + result, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "定位中,请稍候..", Toast.LENGTH_SHORT).show();
        }
    }

    public void getMonitorData() {
        final ProgressDialog dialog = ProgressDialog.show(this, "请稍候", "正在加载数据...", true);

        LDThreadM.getShortPool().execute(new Runnable() {
            @Override
            public void run() {
                //子线程请求数据
                final List<Monitor> monitors = new ArrayList<>();
                double lat = 39.0;
                double lng = 116.0;
                Random random = new Random();
                for (int i = 0; i < 20; i++) {
                    lat += random.nextDouble() / 10 - .05;
                    lng += random.nextDouble() / 10 - .05;
                    monitors.add(new Monitor((long) i + 1, "站点" + i, (i + 1) % 3 == 0 ? -1 : 0, "站点位置信息" + i, lat, lng));
                }

                //模拟请求
                SystemClock.sleep(1000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing() || recyclerView == null) {
                            return;
                        }
                        dialog.dismiss();
                        if (monitors.isEmpty()) {
                            Toast.makeText(MainActivity.this, "请求数据为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        MonitorListAdapter adapterWrapper = new MonitorListAdapter(recyclerView, monitors);
                        recyclerView.setAdapter(adapterWrapper);
                        adapterWrapper.setOnItemClickListener(new MonitorListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Monitor monitor, int position, View view) {
                                clickMonitorItem(monitor);
                            }
                        });

                        //add map overlays
                        monitorMarkers = new ArrayList<>(monitors.size());
                        BitmapDescriptor bitmapDescriptorNormal = BitmapDescriptorFactory.fromResource(R.drawable.icon_monitor_bubble);
                        BitmapDescriptor bitmapDescriptorRed = BitmapDescriptorFactory.fromResource(R.drawable.icon_monitor_bubble_red);
                        MarkerOptions option = new MarkerOptions();
                        double left, top, right, bottom;
                        left = bottom = 300;
                        top = right = 0;
                        for (int i = 0; i < monitors.size(); i++) {
                            Monitor monitor = monitors.get(i);
                            left = Math.min(left, monitor.lat);
                            bottom = Math.min(bottom, monitor.lng);
                            top = Math.max(top, monitor.lng);
                            right = Math.max(right, monitor.lat);
                            option.title("站点:" + monitor.name).snippet("地址:" + monitor.address + "\n状态:" + monitor.getStateText())
                                    .icon(monitor.state == -1 ? bitmapDescriptorRed : bitmapDescriptorNormal)
                                    .position(new LatLng(monitor.lat, monitor.lng));
                            Marker marker = map.addMarker(option);
                            marker.hideInfoWindow();
                            monitorMarkers.add(marker);
                            marker.setTag(monitor);
                        }
                        //把地图缩放到刚好包含所有marker的级别
                        map.zoomToSpan(new LatLng(left - .01, top + .01), new LatLng(right + .01, bottom - .01));
                    }
                });
            }
        });
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        locationManager.removeUpdates(this);
        if (!viewLocate.isEnabled()) {
            viewLocate.setEnabled(true);
        }
        if (TencentLocation.ERROR_OK != error) {
            Toast.makeText(this, "定位失败:" + reason, Toast.LENGTH_SHORT).show();
            return;
        }

        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude())), 500, null);
        if (locationMarker == null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDensity = 200;
            opts.inScaled = true;
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_location, opts);
            locationMarker = map.addMarker(
                    new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                            .position(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude()))
                            .title("当前位置:")
                            .snippet(tencentLocation.getName()));
        } else {
            locationMarker.setPosition(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude()));
            locationMarker.setSnippet(tencentLocation.getName());
        }
        if (lastShowingInfoMarker != null) {
            lastShowingInfoMarker.hideInfoWindow();
            lastShowingInfoMarker = null;
        }
        locationMarker.showInfoWindow();
        lastShowingInfoMarker = locationMarker;
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        Object tag = marker.getTag();
//        if (tag == null || !(tag instanceof Monitor)) {
//            return false;
//        }
//        clickMonitorItem((Monitor) tag);
//        return true;
        if (lastShowingInfoMarker != null) {
            lastShowingInfoMarker.hideInfoWindow();
            lastShowingInfoMarker = null;
        }
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
            lastShowingInfoMarker = marker;
        }
        return true;
    }

    private void clickMonitorItem(final Monitor monitor) {
        if (monitor == null) {
            return;
        }
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(monitor.lat, monitor.lng)), 500, null);
        if (monitor.state == -1) {
            //设备状态异常，不让操作
            new AlertDialog.Builder(this)
                    .setTitle(monitor.name)
                    .setMessage("设备状态异常, 无法操作!")
                    .setCancelable(false)
                    .setNegativeButton("确定", null)
                    .show();
            return;
        }
        String[] options = {"实时监控", "回放录像", "重启监控设备", "维护记录"};
        new AlertDialog.Builder(this)
                .setTitle(monitor.name)
                .setCancelable(true)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                            case 1:
                                startActivity(ViewMonitorActivity.intentViewMonitor(MainActivity.this, monitor));
                                break;
                            case 2:
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(monitor.name)
                                        .setMessage(String.format("确定要重启监控设备[%s]?", monitor.name))
                                        .setNegativeButton("取消", null)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "请稍候", "正在重启监控设备...", true);
                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(MainActivity.this, "监控设备重启成功", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, 2000);
                                            }
                                        })
                                        .show();
                                break;
                            case 3:
                                startActivity(MaintainRecordActivity.intentMaintainRecord(MainActivity.this, monitor));
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (lastShowingInfoMarker != null) {
            lastShowingInfoMarker.hideInfoWindow();
            lastShowingInfoMarker = null;
        }
        marker.hideInfoWindow();
        Object tag = marker.getTag();
        if (tag == null || !(tag instanceof Monitor)) {
            return;
        }
        clickMonitorItem((Monitor) tag);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (lastShowingInfoMarker != null) {
            lastShowingInfoMarker.hideInfoWindow();
            lastShowingInfoMarker = null;
        }
    }
}
