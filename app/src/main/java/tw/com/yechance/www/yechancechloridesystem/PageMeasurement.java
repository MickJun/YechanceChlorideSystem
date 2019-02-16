package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import woyou.aidlservice.jiuiv5.IWoyouService;

public class PageMeasurement extends AppCompatActivity implements View.OnClickListener{

    public MainActivity mainActivity;

    private Button btn_meas_start;
    private Button btn_meas_Return;

    private TextView  txt_meas_title;
    private TextView  txt_meas_date;
    private TextView  txt_meas_temperature;

    private TextView  txt_meas_1;
    private TextView  txt_meas_2;
    private TextView  txt_meas_3;
    private TextView  txt_meas_4;

    private String str_print_1,str_print_2,str_print_3,str_print_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_measurement);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }


        if(btn_meas_start == null) {
            btn_meas_start = this.findViewById(R.id.measurement_btn_start);
            btn_meas_start.setOnClickListener(this);
        }
        if(btn_meas_Return == null) {
            btn_meas_Return = this.findViewById(R.id.measurement_btn_return);
            btn_meas_Return.setOnClickListener(this);
        }
        if(txt_meas_title == null){
            txt_meas_title = this.findViewById(R.id.measurement_txt_title);
        }
        if(txt_meas_date == null){
            txt_meas_date = this.findViewById(R.id.measurement_txt_date);
        }
        if(txt_meas_temperature == null){
            txt_meas_temperature = this.findViewById(R.id.print_txt_temp);
        }
        if(txt_meas_1 == null){
            txt_meas_1 = this.findViewById(R.id.measurement_txt_1);
        }
        if(txt_meas_2 == null){
            txt_meas_2 = this.findViewById(R.id.measurement_txt_2);
        }
        if(txt_meas_3 == null){
            txt_meas_3 = this.findViewById(R.id.measurement_txt_3);
        }
        if(txt_meas_4 == null){
            txt_meas_4 = this.findViewById(R.id.measurement_txt_4);
        }

        mainActivity = ((MickTest) getApplication()).getMainActivity();
    }
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.measurement_btn_start:

                int Inn1 = mainActivity.readMessage(0);
                txt_meas_1.setText(Integer.toString(Inn1));

                Inn1 = mainActivity.readMessage(1);
                txt_meas_2.setText(Integer.toString(Inn1));

                //取得內部儲存體擺放檔案的目錄
                //預設擺放目錄為 /data/data/[package.name]/file
                File dir = this.getExternalFilesDir(null);
                String filename = "test.txt";

                //開啟或建立該目錄底下檔名為 "test.txt" 的檔案
                File inFile = new File(dir, filename);

                //讀取 /data/data/com.myapp/test.txt 檔案內容
                String data = readFromFile(inFile);
                txt_meas_3.setText(data);
               //將檔案存放在 getExternalFilesDir() 目錄
                if (isExtStorageWritable()){
                    File outFile = new File(dir, filename);
                    writeToFile(outFile, "Hello! measurement");
                }
                txt_meas_4.setText("Hello! measurement");


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



}
