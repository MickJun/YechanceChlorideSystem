package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class PageMainMenu extends AppCompatActivity implements View.OnClickListener{



    private Button btn_menu_calibration;
    private Button btn_menu_measurement;
    private Button btn_menu_load_delete_data;
    private Button btn_menu_system_set_up;
    private Button btn_menu_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main_menu);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }

        if(btn_menu_calibration == null) {
            btn_menu_calibration = this.findViewById(R.id.menu_btn_calibration);
            btn_menu_calibration.setOnClickListener(this);
        }
        if(btn_menu_measurement == null) {
            btn_menu_measurement = this.findViewById(R.id.menu_btn_measurement);
            btn_menu_measurement.setOnClickListener(this);
        }
        if(btn_menu_load_delete_data == null) {
            btn_menu_load_delete_data = this.findViewById(R.id.menu_btn_load_delete_data);
            btn_menu_load_delete_data.setOnClickListener(this);
        }
        if(btn_menu_system_set_up == null) {
            btn_menu_system_set_up = this.findViewById(R.id.menu_btn_system_set_up);
            btn_menu_system_set_up.setOnClickListener(this);
        }
        if(btn_menu_return == null) {
            btn_menu_return = this.findViewById(R.id.menu_btn_return);
            btn_menu_return.setOnClickListener(this);
        }

    }

    @Override
    protected void onDestroy() {

        //((MainActivity)this.getApplicationContext()).onDestroy();
        super.onDestroy();
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.menu_btn_calibration:
                Intent intent_C = new Intent();
                intent_C.setClass(PageMainMenu.this, PageCalibration.class);
                startActivity(intent_C);
                break;
            case R.id.menu_btn_measurement:
                Intent intent_M = new Intent();
                intent_M.setClass(PageMainMenu.this, PageMeasurement.class);
                startActivity(intent_M);
                break;
            case R.id.menu_btn_load_delete_data:
                Intent intent_L = new Intent();
                intent_L.setClass(PageMainMenu.this, PageLoadDeleteData.class);
                startActivity(intent_L);
                break;
            case R.id.menu_btn_system_set_up:
                Intent intent_S = new Intent();
                //intent_S.setClass(PageMainMenu.this, PageSystemSetUp.class);
                intent_S.setClass(PageMainMenu.this, PagePrinter.class);
                startActivity(intent_S);
                break;
            case R.id.menu_btn_return:
                PageMainMenu.this.finish();
                break;
        }
    }
}
