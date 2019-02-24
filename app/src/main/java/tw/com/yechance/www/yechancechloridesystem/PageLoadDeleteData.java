package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

public class PageLoadDeleteData extends AppCompatActivity implements View.OnClickListener{

    private Button btn_data_return;
    private Button btn_data_load;
    private Button btn_data_delete;

    private ListView data_ListView ;
    private TextView data_TextView ;

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_load_delete_data);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();
        if(btn_data_return == null) {
            btn_data_return = this.findViewById(R.id.data_btn_return);
            btn_data_return.setOnClickListener(this);
        }
        if(btn_data_load == null) {
            btn_data_load = this.findViewById(R.id.data_btn_load);
            btn_data_load.setOnClickListener(this);
        }
        if(btn_data_delete == null) {
            btn_data_delete = this.findViewById(R.id.data_btn_delete);
            btn_data_delete.setOnClickListener(this);
        }
        if(data_TextView == null){
            data_TextView = this.findViewById(R.id.data_txt_select);
        }
        if(data_ListView == null){
            data_ListView = this.findViewById(R.id.data_list_txt);
        }

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.data_btn_load:
                Intent intent_S = new Intent();
                //intent_S.setClass(PageMainMenu.this, PageSystemSetUp.class);
                intent_S.setClass(PageLoadDeleteData.this, PagePrinter.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("txt_1", "Data");
                bundle.putString("txt_2", "From");
                bundle.putString("txt_3", "Load");
                bundle.putString("txt_4", "Page");
                //將Bundle物件assign給intent
                intent_S.putExtras(bundle);

                startActivity(intent_S);
                break;
            case R.id.data_btn_delete:
                break;
            case R.id.data_btn_return:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                finish();
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
