package tw.com.yechance.www.yechancechloridesystem;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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


    private String  Get_title,Get_Str_tmperature, Get_Str_Sensor,Str_for_Temp;
    private int     Get_Int_Tmperature, Get_Int_Sensor;


    //取得內部儲存體擺放檔案的目錄
    //預設擺放目錄為 /data/data/[package.name]/file
    File dir = null;
    String datafilename = "data.txt";
    String settingfilename = "setting.txt";
//    String Sdata[][] = {{"temp0.1","0.1%","temp0.5","0.5%"},   //0.05% , 0.1% , 0.5%
////            {"26","-1208","26","-659"}
////    };
    String Settingdata[][] = {{"temp0.1","0.1%","temp0.5","0.5%"},   //0.05% , 0.1% , 0.5%
            {"26","-1208","26","-659"}
    };
    String Readingdata[][] = {{"細粒料氯離子含量測定","2002年02月02日 22:22:22","2","22","222","2222"},
            {"混凝土氯離子含量測定","2001年01月01日 11:11:11","1","11","111","1111"},
            {"細粒料氯離子含量測定","2002年02月02日 22:22:22","2","22","222","2222"},
            {"水溶液氯離子含量測定","2003年03月03日 33:33:33","3","33","333","3333"}
    };

    File exDataFile = null;

    private String[][] File_Save_Array;
    private String[] File_Read_Row_Array;

    private  int Calibration_count = 120,Calibration_Start_Flag = 0;

    private SoundPool soundPool;
    private int alertId;

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
            btn_calib_sure.setEnabled(false);
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


        dir = this.getExternalFilesDir(null);
        //開啟或建立該目錄底下的檔案
        exDataFile = new File(dir, settingfilename);
        //讀取 /data/data/com.myapp/test.txt 檔案內容
        File_Read_Row_Array = readFromFiletoArray(exDataFile);
        File_Save_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Save_Array[0][0].equals("") || !File_Save_Array[0][0].equals("temp0.1")){
            writeToFile(exDataFile, Settingdata,Settingdata.length,4);
            File_Read_Row_Array = readFromFiletoArray(exDataFile);
            File_Save_Array = DataArrayfomat(File_Read_Row_Array);
        }


        handler.postDelayed(this.runnable,1000);
        Calibration_count = 120;
        txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + Calibration_count + getResources().getText(R.string.str_unit_second).toString());

        //取的intent中的bundle物件
        Bundle bundle =this.getIntent().getExtras();
        Get_title = bundle.getString("title");
        if(Get_title.equals("C_0D1"))
        {
            txt_calib_title.setText(R.string.str_calibration0D1);
            txt_calib_title.setTextColor(getResources().getColor(R.color.red));
            txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + File_Save_Array[1][0] + getResources().getText(R.string.str_unit_tmpeC).toString() );
            txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + File_Save_Array[1][1]  );
        }
        else if(Get_title.equals("C_0D5"))
        {
            txt_calib_title.setText(R.string.str_calibration0D5);
            txt_calib_title.setTextColor(getResources().getColor(R.color.red));
            txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + File_Save_Array[1][2] + getResources().getText(R.string.str_unit_tmpeC).toString() );
            txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + File_Save_Array[1][3]
            );
        }


        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        alertId = soundPool.load(this,(R.raw.alarm1), 1);

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.calibration_btn_start:

                if(Calibration_Start_Flag == 0)
                {
                    Calibration_count = 120;
                    mainActivity.first_write();
                    Calibration_Start_Flag = 1;
                }
                else
                {
                    Calibration_count = 0;
                    Calibration_Start_Flag = 0;
                    txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + Calibration_count + getResources().getText(R.string.str_unit_second).toString());
                    btn_calib_start.setEnabled(false);
                    btn_calib_sure.setEnabled(true);
                }



                break;

            case R.id.calibration_btn_sure:
                if(Get_title.equals("C_0D1"))
                {
                    File_Save_Array[1][0] = Get_Str_tmperature;
                    txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + File_Save_Array[1][0] + getResources().getText(R.string.str_unit_tmpeC).toString() );
                    File_Save_Array[1][1] = Get_Str_Sensor;
                    txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + File_Save_Array[1][1] );
                }
                else if(Get_title.equals("C_0D5"))
                {
                    File_Save_Array[1][2] = Get_Str_tmperature;
                    txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + File_Save_Array[1][2] + getResources().getText(R.string.str_unit_tmpeC).toString() );
                    File_Save_Array[1][3] = Get_Str_Sensor;
                    txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + File_Save_Array[1][3] );
                }
                writeToFile(exDataFile, File_Save_Array,File_Save_Array.length,4);
                Toast.makeText(getApplicationContext(), "儲存完成",	Toast.LENGTH_SHORT).show();
                break;

            case R.id.calibration_btn_re_calibration:
                Calibration_Start_Flag = 0;
                Calibration_count = 120;
                txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + Calibration_count + getResources().getText(R.string.str_unit_second).toString());
                btn_calib_start.setEnabled(true);
                btn_calib_sure.setEnabled(false);
                //讀取 /data/data/com.myapp/test.txt 檔案內容
                File_Read_Row_Array = readFromFiletoArray(exDataFile);
                File_Save_Array = DataArrayfomat(File_Read_Row_Array);

                if(Get_title.equals("C_0D1"))
                {
                    txt_calib_title.setText(R.string.str_calibration0D1);
                    txt_calib_title.setTextColor(getResources().getColor(R.color.red));
                    txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + File_Save_Array[1][0] + getResources().getText(R.string.str_unit_tmpeC).toString() );
                    txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + File_Save_Array[1][1]  );
                }
                else if(Get_title.equals("C_0D5"))
                {
                    txt_calib_title.setText(R.string.str_calibration0D5);
                    txt_calib_title.setTextColor(getResources().getColor(R.color.red));
                    txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + File_Save_Array[1][2] + getResources().getText(R.string.str_unit_tmpeC).toString() );
                    txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + File_Save_Array[1][3]  );
                }

                break;
            case R.id.calibration_btn_return:
                finish();
                break;
        }
    }


    //writeToFile 方法如下
    private void writeToFile(File fout, String data[][], int Row_length,int Cell_length) {

        try{
            //建立FileOutputStream物件，路徑為SD卡中的output.txt
            FileOutputStream output = new FileOutputStream(fout);

            for (int i = 0; i < Row_length; i++) {
                if(!data[i][0].equals("")) {
                    for (int j = 0; j < Cell_length; j++) {
                        output.write(data[i][j].getBytes());
                        output.write(",".getBytes());
                    }
                    output.write("\r\n".getBytes());
                }
            }
            output.close();
        }catch(Exception e){
            e.printStackTrace();
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




    //readFromFile 方法如下
    private String[] readFromFiletoArray(File fin) {
        StringBuilder data3 = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fin), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data3.append(line + "\r\n");
            }
        } catch (Exception e) {
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        ArrayList<String> stringArray = new ArrayList<String>();

        String[] splitArray = data3.toString().split("\r\n");

        for (int i = 0; i < splitArray.length; i++) {

            stringArray.add(splitArray[i]);
        }
        return splitArray;
    }


    private String[][] DataArrayfomat(String[] inArray)
    {
        String[][] retrunArray = new String[inArray.length][6];
        for(int i=0; i<inArray.length;i++){
            String[] splitArray = inArray[i].split(",");
            for(int j=0; j<splitArray.length;j++){
                retrunArray[i][j] = splitArray[j];
            }
        }
        return retrunArray;
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            if(Calibration_Start_Flag > 0)
            {
                if(Calibration_count > 0){
                    Calibration_count--;
                }
                else
                {
                    btn_calib_start.setEnabled(false);
                    btn_calib_sure.setEnabled(true);
                    Calibration_Start_Flag = 0;
                    soundPool.play(alertId, 1.0F, 1.0F, 0, 0, 1.5F);
                }
                txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + Calibration_count + getResources().getText(R.string.str_unit_second).toString());

                Get_Int_Tmperature = mainActivity.readADC(1);
                Get_Str_tmperature = Integer.toString(Get_Int_Tmperature);
                txt_calib_temperature.setText(getResources().getText(R.string.str_temperature).toString() + Get_Str_tmperature + getResources().getText(R.string.str_unit_tmpeC).toString() );

                Get_Int_Sensor = mainActivity.readADC(0);
                Get_Str_Sensor = Integer.toString(Get_Int_Sensor);
                txt_calib_device_read.setText(getResources().getText(R.string.str_device_read).toString() + Get_Str_Sensor  );
            }
            else{ //Calibration_Start_Flag = 0

            }
            handler.postDelayed(this, 1000);
        }
    };

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
