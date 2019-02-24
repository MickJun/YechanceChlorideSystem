package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import woyou.aidlservice.jiuiv5.IWoyouService;

public class PageMeasurement extends AppCompatActivity implements View.OnClickListener{

    public MainActivity mainActivity;

    private Button btn_meas_start;
    private Button btn_meas_Return;
    private Button btn_meas_Print;
    private Button btn_meas_Retest;

    private TextView  txt_meas_title;
    private TextView  txt_meas_date;
    private TextView  txt_meas_temperature;

    private EditText  edit_meas_typing;
    private TextView  txt_meas_typing_title;
    private TextView  txt_meas_typing_unit;
    private TextView  txt_meas_1;
    private TextView  txt_meas_2;

    private String  Get_title,Get_Str_tmpeture,Str_for_Temp;
    private int     Get_int_tmpeture;

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_measurement);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();


        if(btn_meas_start == null) {
            btn_meas_start = this.findViewById(R.id.measurement_btn_start);
            btn_meas_start.setOnClickListener(this);
        }
        if(btn_meas_Return == null) {
            btn_meas_Return = this.findViewById(R.id.measurement_btn_return);
            btn_meas_Return.setOnClickListener(this);
        }
        if(btn_meas_Print == null) {
            btn_meas_Print = this.findViewById(R.id.measurement_btn_print);
            btn_meas_Print.setOnClickListener(this);
        }
        if(btn_meas_Retest == null) {
            btn_meas_Retest = this.findViewById(R.id.measurement_btn_retest);
            btn_meas_Retest.setOnClickListener(this);
        }
        if(txt_meas_title == null){
            txt_meas_title = this.findViewById(R.id.measurement_txt_title);
        }
        if(txt_meas_date == null){
            txt_meas_date = this.findViewById(R.id.measurement_txt_date);
        }
        if(txt_meas_temperature == null){
            txt_meas_temperature = this.findViewById(R.id.measurement_txt_temp);
        }
        if(edit_meas_typing == null){
            edit_meas_typing = this.findViewById(R.id.measurement_editText_typing);
        }
        if(txt_meas_typing_title == null){
            txt_meas_typing_title = this.findViewById(R.id.measurement_txt_typing_title);
        }
        if(txt_meas_typing_unit == null){
            txt_meas_typing_unit = this.findViewById(R.id.measurement_txt_typing_unit);
        }

        if(txt_meas_1 == null){
            txt_meas_1 = this.findViewById(R.id.measurement_txt_1);
        }
        if(txt_meas_2 == null){
            txt_meas_2 = this.findViewById(R.id.measurement_txt_2);
        }

        mainActivity = ((MickTest) getApplication()).getMainActivity();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        txt_meas_date.setText(str);

        Get_int_tmpeture = mainActivity.readADC(1);
        Get_Str_tmpeture = Integer.toString(Get_int_tmpeture);
        txt_meas_temperature.setText(getResources().getText(R.string.str_temperature).toString() + Get_Str_tmpeture + getResources().getText(R.string.str_unit_tmpeC).toString() );

        //取的intent中的bundle物件
        Bundle bundle =this.getIntent().getExtras();
        Get_title = bundle.getString("title");
        if(Get_title.equals("M_CC"))
        {
            txt_meas_title.setText(R.string.str_measurement_concrete_chloride);
            txt_meas_title.setTextColor(getResources().getColor(R.color.red));
            txt_meas_typing_title.setText(getResources().getText(R.string.str_water_unit));
            txt_meas_typing_unit.setText(getResources().getText(R.string.str_unit_kgm3));
            //Str_for_Temp = getResources().getText(R.string.str_water_cl).toString();
            txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + getResources().getText(R.string.str_unit_percentage).toString());
            txt_meas_2.setText(getResources().getText(R.string.str_CC_summarize).toString() + getResources().getText(R.string.str_unit_percentage).toString());


        }
        else if(Get_title.equals("M_FAC"))
        {
            txt_meas_title.setText(R.string.str_measurement_fine_aggregate_chloride);
            txt_meas_title.setTextColor(getResources().getColor(R.color.red));
            txt_meas_typing_title.setText(getResources().getText(R.string.str_water_rate));
            txt_meas_typing_unit.setText(getResources().getText(R.string.str_unit_percentage));
            txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + getResources().getText(R.string.str_unit_percentage).toString());
            txt_meas_2.setText(getResources().getText(R.string.str_FAC_summarize).toString() + getResources().getText(R.string.str_unit_percentage).toString());

        }
        else if(Get_title.equals("M_WC"))
        {
            txt_meas_title.setText(R.string.str_measurement_water_chloride);
            txt_meas_title.setTextColor(getResources().getColor(R.color.red));
            edit_meas_typing.setVisibility(View.INVISIBLE);
            txt_meas_typing_title.setVisibility(View.INVISIBLE);
            txt_meas_typing_unit.setVisibility(View.INVISIBLE);
            txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + getResources().getText(R.string.str_unit_percentage).toString());
            txt_meas_2.setText(getResources().getText(R.string.str_FAC_summarize).toString() + getResources().getText(R.string.str_unit_percentage).toString());
        }

    }
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.measurement_btn_start:

                int Inn1 = mainActivity.readADC(0);
                txt_meas_1.setText(Integer.toString(Inn1));

                Inn1 = mainActivity.readADC(1);
                txt_meas_2.setText(Integer.toString(Inn1));

                //取得內部儲存體擺放檔案的目錄
                //預設擺放目錄為 /data/data/[package.name]/file
                File dir = this.getExternalFilesDir(null);
                String filename = "test.txt";

                //開啟或建立該目錄底下檔名為 "test.txt" 的檔案
                File inFile = new File(dir, filename);

                //讀取 /data/data/com.myapp/test.txt 檔案內容
                String data = readFromFile(inFile);
                txt_meas_1.setText(data);
               //將檔案存放在 getExternalFilesDir() 目錄
                if (isExtStorageWritable()){
                    File outFile = new File(dir, filename);
                    writeToFile(outFile, "Hello! measurement");
                }
                break;
            case R.id.measurement_btn_print:
                Intent intent_S = new Intent();
                //intent_S.setClass(PageMainMenu.this, PageSystemSetUp.class);
                intent_S.setClass(PageMeasurement.this, PagePrinter.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("title", txt_meas_title.getText().toString());
                bundle.putString("date", txt_meas_date.getText().toString());
                bundle.putString("temperature", txt_meas_temperature.getText().toString());
                if(txt_meas_typing_title.getVisibility() == View.VISIBLE){
                    bundle.putString("typing", txt_meas_typing_title.getText().toString()+edit_meas_typing.getText().toString()+txt_meas_typing_unit.getText().toString()) ;
                }
                else{
                    bundle.putString("typing","") ;
                }
                bundle.putString("txt_1", txt_meas_1.getText().toString());
                bundle.putString("txt_2", txt_meas_2.getText().toString());
                //將Bundle物件assign給intent
                intent_S.putExtras(bundle);

                startActivity(intent_S);
                break;
            case R.id.measurement_btn_retest:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                break;
            case R.id.measurement_btn_return:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                finish();
                break;
        }
    }

    //writeToFile 方法如下
    private void writeToFile(File fout, String data) {
        FileOutputStream osw = null;
        try {
            osw = new FileOutputStream(fout);
            osw.write(data.getBytes());
            osw.flush();
        } catch (Exception e) {
        } finally {
            try {
                osw.close();
            } catch (Exception e) {
            }
        }
    }

    //檢查外部儲存體是否可以進行寫入
    public boolean isExtStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //檢查外部儲存體是否可以進行讀取
    public boolean isExtStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    //readFromFile 方法如下
    private String readFromFile(File fin) {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fin), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (Exception e) {
            ;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                ;
            }
        }
        return data.toString();
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
