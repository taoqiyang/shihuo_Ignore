package mousepaint.taoqiyang.com.ignore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AuthorActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private String[] allPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllPermission();
    }

    private static final int RC_ALL_PERM = 124;
    private static final int RC_SETTINGS_SCREEN = 125;

    @AfterPermissionGranted(RC_ALL_PERM)
    public void requestAllPermission() {
        if (EasyPermissions.hasPermissions(this, allPermissions)) {
            go2Welcome();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.module_base_string_need_permission),
                    RC_ALL_PERM, allPermissions);
        }
    }

    private void go2Welcome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        showSettingDailog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RC_SETTINGS_SCREEN) {
            return;
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else if (!EasyPermissions.hasPermissions(this, allPermissions)) {
            showSettingDailog();
        }
    }

    private void showSettingDailog() {
        new AppSettingsDialog.Builder(this)
                .setTitle(getString(R.string.module_base_string_title_permission_short))
                .setRationale(getString(R.string.module_base_string_rationale_ask_again))
                .setPositiveButton(getString(R.string.module_base_string_go2_setting))
                .setNegativeButton(getString(R.string.module_base_string_exit))
                .setRequestCode(RC_SETTINGS_SCREEN)
                .build()
                .show();
    }
}
