package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class PageMainMenu extends AppCompatActivity implements View.OnClickListener{

    public MainActivity mainActivity;
//
//    public void setMainActivity(MainActivity mainActivity) {
//        this.mainActivity = mainActivity;
//    }

//    public BluetoothSocket mBTSocket;
//
//    public void setBluetoothSocket(BluetoothSocket theBTSocket) {
//        this.mBTSocket = theBTSocket;
//    }

    private Button btn_menu_calibration0D1;
    private Button btn_menu_calibration0D5;
    private Button btn_menu_measurement_CC;
    private Button btn_menu_measurement_FAC;
    private Button btn_menu_measurement_WC;
    private Button btn_menu_load_delete_data;
    private Button btn_menu_return;

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main_menu);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();

        if(btn_menu_calibration0D1 == null) {
            btn_menu_calibration0D1 = this.findViewById(R.id.menu_btn_calibration_0D1);
            btn_menu_calibration0D1.setOnClickListener(this);
        }
        if(btn_menu_calibration0D5 == null) {
            btn_menu_calibration0D5 = this.findViewById(R.id.menu_btn_calibration_0D5);
            btn_menu_calibration0D5.setOnClickListener(this);
        }
        if(btn_menu_measurement_CC == null) {
            btn_menu_measurement_CC = this.findViewById(R.id.menu_btn_measurement_CC);
            btn_menu_measurement_CC.setOnClickListener(this);
        }
        if(btn_menu_measurement_FAC == null) {
            btn_menu_measurement_FAC = this.findViewById(R.id.menu_btn_measurement_FAC);
            btn_menu_measurement_FAC.setOnClickListener(this);
        }
        if(btn_menu_measurement_WC == null) {
            btn_menu_measurement_WC = this.findViewById(R.id.menu_btn_measurement_WC);
            btn_menu_measurement_WC.setOnClickListener(this);
        }
        if(btn_menu_load_delete_data == null) {
            btn_menu_load_delete_data = this.findViewById(R.id.menu_btn_load_delete_data);
            btn_menu_load_delete_data.setOnClickListener(this);
        }
        if(btn_menu_return == null) {
            btn_menu_return = this.findViewById(R.id.menu_btn_return);
            btn_menu_return.setOnClickListener(this);
        }
        mainActivity = ((MickTest) getApplication()).getMainActivity();
    }

    @Override
    protected void onDestroy() {

        //((MainActivity)this.getApplicationContext()).onDestroy();
        super.onDestroy();
    }
    private byte[] output_Final = {0x0a,0x0f,0x0b};
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.menu_btn_calibration_0D1:
                Intent intent_0D1 = new Intent();
                intent_0D1.setClass(PageMainMenu.this, PageCalibration.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle_0D1 = new Bundle();
                bundle_0D1.putString("title", "C_0D1");
                //將Bundle物件assign給intent
                intent_0D1.putExtras(bundle_0D1);
                startActivity(intent_0D1);
                break;
            case R.id.menu_btn_calibration_0D5:
                Intent intent_0D5 = new Intent();
                intent_0D5.setClass(PageMainMenu.this, PageCalibration.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle_0D5 = new Bundle();
                bundle_0D5.putString("title", "C_0D5");
                //將Bundle物件assign給intent
                intent_0D5.putExtras(bundle_0D5);
                startActivity(intent_0D5);
                break;
            case R.id.menu_btn_measurement_CC:
                Intent intent_CC = new Intent();
                intent_CC.setClass(PageMainMenu.this, PageMeasurement.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle_CC = new Bundle();
                bundle_CC.putString("title", "M_CC");
                //將Bundle物件assign給intent
                intent_CC.putExtras(bundle_CC);
                startActivity(intent_CC);
                break;
            case R.id.menu_btn_measurement_FAC:
                Intent intent_FAC = new Intent();
                //intent_S.setClass(PageMainMenu.this, PageSystemSetUp.class);
                intent_FAC.setClass(PageMainMenu.this, PageMeasurement.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle_FAC = new Bundle();
                bundle_FAC.putString("title", "M_FAC");
                //將Bundle物件assign給intent
                intent_FAC.putExtras(bundle_FAC);
                startActivity(intent_FAC);
                break;

            case R.id.menu_btn_measurement_WC:
                Intent intent_WC = new Intent();
                intent_WC.setClass(PageMainMenu.this, PageMeasurement.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle_WC = new Bundle();
                bundle_WC.putString("title", "M_WC");
                //將Bundle物件assign給intent
                intent_WC.putExtras(bundle_WC);
                startActivity(intent_WC);
                break;
            case R.id.menu_btn_load_delete_data:
                Intent intent_LDD = new Intent();
                intent_LDD.setClass(PageMainMenu.this, PageLoadDeleteData.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle_LDD = new Bundle();
                bundle_LDD.putString("title", "M_WC");
                //將Bundle物件assign給intent
                intent_LDD.putExtras(bundle_LDD);
                startActivity(intent_LDD);
                break;

            case R.id.menu_btn_return:
                PageMainMenu.this.finish();
                break;
        }
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隱藏虛擬按鍵，並且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }
}
