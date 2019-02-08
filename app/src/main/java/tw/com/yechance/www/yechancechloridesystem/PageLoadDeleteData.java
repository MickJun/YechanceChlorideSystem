package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

public class PageLoadDeleteData extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_load_delete_data);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.main_btn_scan:
                break;
            case R.id.main_btn_connect:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
//                finish();
                break;
        }
    }
}
