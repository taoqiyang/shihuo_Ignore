package mousepaint.taoqiyang.com.ignore;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mousepaint.taoqiyang.com.ignore.beans.Monitor;

public class ViewMonitorActivity extends AppCompatActivity{
    private static final String KEY_MONITOR = "monitor";

    public static Intent intentViewMonitor(Context context, Monitor monitor) {
        Intent intent = new Intent(context, ViewMonitorActivity.class);
        intent.putExtra(KEY_MONITOR, monitor);
        return intent;
    }

    private MediaPlayer mediaPlayer;
    /**
     * 显示媒体
     */
    private SurfaceView surView;
    /**
     * 用来控制SurfaceView
     */
    private SurfaceHolder surHolder;

    private Monitor monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_monitor);
        monitor = getIntent().getParcelableExtra(KEY_MONITOR);
        setTitle(monitor.name);

        surView = (SurfaceView) findViewById(R.id.surfaceview);
        surHolder = surView.getHolder();

        surHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.video);
                mediaPlayer.setDisplay(surHolder);
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
