package mousepaint.taoqiyang.com.ignore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mousepaint.taoqiyang.com.ignore.beans.MaintainRecord;
import mousepaint.taoqiyang.com.ignore.beans.Monitor;
import mousepaint.taoqiyang.com.ignore.util.LDThreadM;

public class MaintainRecordActivity extends AppCompatActivity {
    public static final String KEY_MONITOR_ID = "monitorID";

    public static Intent intentMaintainRecord(Context context, Monitor monitor){
        Intent intent = new Intent(context, MaintainRecordActivity.class);
        intent.putExtra(KEY_MONITOR_ID, monitor.id);
        return intent;
    }

    public RecyclerView recyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_record);
        long monitorID = getIntent().getLongExtra(KEY_MONITOR_ID, -1);
        if(monitorID <= 0){
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        DividerLine decoration = new DividerLine();
//        decoration.setColor(Color.BLACK);
//        decoration.setSize(getResources().getDimensionPixelSize(R.dimen.util_theme_dimens_height_divider));
//        recyclerView.addItemDecoration(decoration);

        getMaintainRecords(monitorID);
    }

    private void getMaintainRecords(final long monitorID) {
        final ProgressDialog dialog = ProgressDialog.show(this, "请稍候", "正在加载数据...", true);
        LDThreadM.getShortPool().execute(new Runnable() {
            @Override
            public void run() {

                final List<MaintainRecord> maintainRecords = new ArrayList<>();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, 4);
                for(int i = 0 ; i < 20; i++){
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    maintainRecords.add(new MaintainRecord((long) i, monitorID, "设备维护日志,调试状态正常，视频模糊不清，清洁镜头后正常。", calendar.getTime()));
                }

                //模拟请求
                SystemClock.sleep(1000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isFinishing() || recyclerView == null){
                            return;
                        }
                        dialog.dismiss();
                        if(maintainRecords.isEmpty()){
                            Toast.makeText(MaintainRecordActivity.this, "请求数据为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        MaintainRecordListAdapter adapter = new MaintainRecordListAdapter(maintainRecords);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }
}
