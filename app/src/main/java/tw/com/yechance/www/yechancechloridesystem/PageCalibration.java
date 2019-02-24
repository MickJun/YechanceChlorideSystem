package tw.com.yechance.www.yechancechloridesystem;

import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class PageCalibration extends AppCompatActivity  implements View.OnClickListener{

    public MainActivity mainActivity;

    private Button btn_calib_start;
    private Button btn_calib_Return;
    private Button btn_calib_sure;
    private Button btn_calib_Re_calib;


    private TextView txt_calib_title;
    private TextView  txt_calib_timer;
    private TextView  txt_calib_temperature;
    private TextView  txt_calib_device_read;


    private String  Get_title,Get_Str_tmpeture, Get_Str_Sensor,Str_for_Temp;
    private int     Get_Int_Tmpeture, Get_Int_Sensor;

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_calibration);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();


        if(btn_calib_start == null) {
            btn_calib_start = this.findViewById(R.id.calibration_btn_start);
            btn_calib_start.setOnClickListener(this);
        }
        if(btn_calib_Return == null) {
            btn_calib_Return = this.findViewById(R.id.calibration_btn_return);
            btn_calib_Return.setOnClickListener(this);
        }
        if(btn_calib_sure == null) {
            btn_calib_sure = this.findViewById(R.id.calibration_btn_sure);
            btn_calib_sure.setOnClickListener(this);
        }
        if(btn_calib_Re_calib == null) {
            btn_calib_Re_calib = this.findViewById(R.id.calibration_btn_re_calibration);
            btn_calib_Re_calib.setOnClickListener(this);
        }


        if(txt_calib_title == null){
            txt_calib_title = this.findViewById(R.id.calibration_txt_title);
        }
        if(txt_calib_timer == null){
            txt_calib_timer = this.findViewById(R.id.calibration_txt_timer);
        }
        if(txt_calib_temperature == null){
            txt_calib_temperature = this.findViewById(R.id.calibration_txt_temp);
        }
        if(txt_calib_device_read == null){
            txt_calib_device_read = this.findViewById(R.id.calibration_device_read);
        }

        mainActivity = ((MickTest) getApplication()).getMainActivity();


        Get_Int_Tmpeture = mainActivity.readADC(1);
        Get_Str_tmpeture = Integer.toString(Get_Int_Tmpeture);
        txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + Get_Str_tmpeture + getResources().getText(R.string.str_unit_tmpeC).toString() );
        //取的intent中的bundle物件
        Bundle bundle =this.getIntent().getExtras();
        Get_title = bundle.getString("title");
        if(Get_title.equals("C_0D1"))
        {
            txt_calib_title.setText(R.string.str_calibration0D1);
            txt_calib_title.setTextColor(getResources().getColor(R.color.red));
        }
        else if(Get_title.equals("C_0D5"))
        {
            txt_calib_title.setText(R.string.str_calibration0D5);
            txt_calib_title.setTextColor(getResources().getColor(R.color.red));
        }
        txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + "120" + getResources().getText(R.string.str_unit_second).toString());

        Get_Int_Sensor = mainActivity.readADC(0);
        Get_Str_Sensor = Integer.toString(Get_Int_Sensor);
        txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + Get_Str_Sensor + getResources().getText(R.string.str_unit_percentage).toString() );

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.calibration_btn_start:


//                int Inn1 = mainActivity.readMessage(0);
//                txt_calib_1.setText(Integer.toString(Inn1));
//
//                Inn1 = mainActivity.readMessage(1);
//                txt_calib_2.setText(Integer.toString(Inn1));
//
//                //取得內部儲存體擺放檔案的目錄
//                //預設擺放目錄為 /data/data/[package.name]/file
//                File dir = this.getExternalFilesDir(null);
//                String filename = "test.txt";
//
//                //開啟或建立該目錄底下檔名為 "test.txt" 的檔案
//                File inFile = new File(dir, filename);
//
//                //讀取 /data/data/com.myapp/test.txt 檔案內容
//                String data = readFromFile(inFile);
//                txt_calib_3.setText(data);
//                //將檔案存放在 getExternalFilesDir() 目錄
//                if (isExtStorageWritable()){
//                    File outFile = new File(dir, filename);
//                    writeToFile(outFile, "Hello! calibration");
//                }
//                txt_calib_4.setText("Hello! calibration");


                break;

            case R.id.calibration_btn_sure:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                finish();
                break;

            case R.id.calibration_btn_re_calibration:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.calibration_btn_return:
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
