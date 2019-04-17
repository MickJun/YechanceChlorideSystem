package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

//    private String  Get_title,Get_Str_tmpeture,Str_for_Temp;
////    private int     Get_int_tmpeture;

    private String  Get_title = "",Get_Str_tmperature = "", Get_Str_Sensor = "",Str_for_Temp = "",Get_Str_Keyin = "",Get_Str_EndData = "0"; //end 不能為""
    private int     Get_Int_Tmperature, Get_Int_Sensor,Get_Int_Keyin;

    double Setting_Slope = 0, txt1_double = 0,Keyin_double = 0, txt2_double = 0, intercept = 0;
    private DecimalFormat df = new DecimalFormat("##0.0000");


    //取得內部儲存體擺放檔案的目錄
    //預設擺放目錄為 /data/data/[package.name]/file
    File dir = null;
    String datafilename = "data.txt";
    String settingfilename = "setting.txt";
    String Settingdata[][] = {{"temp0.1","0.1%","temp0.5","0.5%"},   //0.05% , 0.1% , 0.5%
            {"26","-1208","26","-659"}
    };

    String Readingdata[][] = {{"title","YYYY/MM/DD hh:mm:ss","Temp","Typing","txt1","txt2"},
            {"混凝土氯離子含量測定","2001/01/01 11:11:11","1","11","111","1111"},
            {"細粒料氯離子含量測定","2002/02/02 22:22:22","2","22","222","2222"},
            {"水溶液氯離子含量測定","2003/03/03 33:33:33","3","33","333","3333"}
    };

    File exDataFile = null;

    File exSettingFile = null;

    private String[][] File_Data_Array,File_Setting_Array;
    private String[] File_Read_Row_Array;

    private  int Measurement_count = 120,Measurement_Start_Flag = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

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
        btn_meas_Print.setEnabled(false);
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

        dir = this.getExternalFilesDir(null);
        //開啟或建立該目錄底下的檔案
        exDataFile = new File(dir, datafilename);

        File_Read_Row_Array = readFromFiletoArray(exDataFile);
        File_Data_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Data_Array[0][0].equals("")){
            writeToFile(exDataFile, Readingdata,Readingdata.length,4);
            File_Read_Row_Array = readFromFiletoArray(exDataFile);
            File_Data_Array = DataArrayfomat(File_Read_Row_Array);
        }

        exSettingFile = new File(dir, settingfilename);
        File_Read_Row_Array = readFromFiletoArray(exSettingFile);
        File_Setting_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Setting_Array[0][0].equals("")){
            writeToFile(exDataFile, Settingdata,Settingdata.length,6);
            File_Read_Row_Array = readFromFiletoArray(exDataFile);
            File_Setting_Array = DataArrayfomat(File_Read_Row_Array);
        }





        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        txt_meas_date.setText(str);

        Get_Int_Tmperature = mainActivity.readADC(1);
        Get_Str_tmperature = Integer.toString(Get_Int_Tmperature);
        txt_meas_temperature.setText(getResources().getText(R.string.str_temperature).toString() + Get_Str_tmperature + getResources().getText(R.string.str_unit_tmpeC).toString() );

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
            txt_meas_2.setText(getResources().getText(R.string.str_CC_summarize).toString() + getResources().getText(R.string.str_unit_kgm3).toString());


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
            txt_meas_2.setText("");
            txt_meas_2.setVisibility(View.INVISIBLE);
        }

    }
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.measurement_btn_start:

                if(edit_meas_typing.getText().toString().equals("") && !Get_title.equals("M_WC"))
                {
                    Toast.makeText(getApplicationContext(), "請輸入數字",	Toast.LENGTH_SHORT).show();
                    break;
                }

                if(Measurement_Start_Flag == 0)
                {
                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    String str = formatter.format(curDate);
                    txt_meas_date.setText(str);
                    Measurement_count = 120;
                    Measurement_Start_Flag = 1;
                    btn_meas_start.setText(getResources().getText(R.string.str_ing_test).toString() );
                    handler.postDelayed(this.runnable, 1000);
                    if(!edit_meas_typing.getText().toString().equals("")) {
                        Keyin_double = Double.parseDouble(edit_meas_typing.getText().toString());
                        Get_Str_Keyin = Double.toString(Keyin_double);
                    }
                    else
                    {
                        Get_Str_Keyin = "";
                    }
                }
                else
                {
                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    String str = formatter.format(curDate);
                    txt_meas_date.setText(str);
                    Measurement_count = 0;
                    Measurement_Start_Flag = 0;
                    //txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + Calibration_count + getResources().getText(R.string.str_unit_second).toString());
                    btn_meas_start.setText(getResources().getText(R.string.str_end_test).toString() );
                    btn_meas_start.setEnabled(false);
                    btn_meas_Print.setEnabled(true);
                }

                break;
            case R.id.measurement_btn_print:
                //save
                if(Get_Str_EndData.equals("")){Get_Str_EndData = "0";} //最後一個參數必須要有值 因此 End不可為""
                //{"混凝土氯離子含量測定","2001/01/01 11:11:11","1","11","111","1111"},
                File_Data_Array[File_Data_Array.length -1][0] = txt_meas_title.getText().toString();
                File_Data_Array[File_Data_Array.length -1][1] = txt_meas_date.getText().toString();
                File_Data_Array[File_Data_Array.length -1][2] = Get_Str_tmperature;
                File_Data_Array[File_Data_Array.length -1][3] = Get_Str_Keyin;
                File_Data_Array[File_Data_Array.length -1][4] = Get_Str_Sensor;
                File_Data_Array[File_Data_Array.length -1][5] = Get_Str_EndData;

                writeToFile(exDataFile, File_Data_Array,File_Data_Array.length,6);


                //print
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
                if(Get_title.equals("M_WC")){
                    bundle.putString("txt_2","") ;
                }
                else{
                    bundle.putString("txt_2", txt_meas_2.getText().toString());
                }
                //將Bundle物件assign給intent
                intent_S.putExtras(bundle);

                startActivity(intent_S);
                break;
            case R.id.measurement_btn_retest:
                Measurement_count = 120;
                btn_meas_start.setText(getResources().getText(R.string.str_start_test).toString() );
                btn_meas_start.setEnabled(true);
                btn_meas_Print.setEnabled(false);
                break;
            case R.id.measurement_btn_return:
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
        String[][] retrunArray = new String[inArray.length + 1][6];  //inArray.length + 1 for save
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
            if(Measurement_Start_Flag > 0)
            {
                if(Measurement_count > 0){
                    Measurement_count--;
                }
                else
                {
                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    String str = formatter.format(curDate);
                    txt_meas_date.setText(str);
                    btn_meas_start.setText(getResources().getText(R.string.str_end_test).toString() );
                    btn_meas_start.setEnabled(false);
                    btn_meas_Print.setEnabled(true);
                    Measurement_Start_Flag = 0;
                }
                //txt_calib_timer.setText(getResources().getText(R.string.str_timer).toString() + Calibration_count + getResources().getText(R.string.str_unit_second).toString());

                Get_Int_Tmperature = mainActivity.readADC(1);
                Get_Str_tmperature = Integer.toString(Get_Int_Tmperature);
                txt_meas_temperature.setText(getResources().getText(R.string.str_temperature).toString() + Get_Str_tmperature + getResources().getText(R.string.str_unit_tmpeC).toString() );

                Get_Int_Sensor = mainActivity.readADC(0); // = CL
                //Setting_Slope = (Integer.parseInt(File_Setting_Array[1][3]) - Integer.parseInt(File_Setting_Array[1][1])) / 0.4f;

                //Get_Str_Sensor = Integer.toString(Get_Int_Sensor);
                //Integer.parseInt(File_Setting_Array[1][3]) 0.5% = E2
                //Integer.parseInt(File_Setting_Array[1][1]) 0.1% = E1
//                File_Setting_Array[1][1] = "-1208";
//                File_Setting_Array[1][3] = "-659";
//                Get_Int_Sensor= -713;
                Setting_Slope = (Integer.parseInt(File_Setting_Array[1][1]) - Integer.parseInt(File_Setting_Array[1][3])) / (Math.log10(1000)-Math.log10(5000));   //=(E1-E2)/(log(1000)-LOG(5000))
                intercept = Integer.parseInt(File_Setting_Array[1][1])  - (Setting_Slope * Math.log10(1000)); //E1 - m * log(1000)

                txt1_double = Math.pow(10,((Get_Int_Sensor - intercept) / Setting_Slope))/10000;

                //txt1_double = Get_Int_Sensor / Setting_Slope ;
                Get_Str_Sensor = df.format(txt1_double);
                txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + Get_Str_Sensor + getResources().getText(R.string.str_unit_percentage).toString());

                if(Get_title.equals("M_CC"))
                {
                    //Cc = Cw * W * (1/100)
//                    txt1_double = Get_Int_Sensor / Setting_Slope ;
//                    Get_Str_Sensor = df.format(txt1_double);
//                    txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + Get_Str_Sensor + getResources().getText(R.string.str_unit_percentage).toString());
                    txt2_double = Keyin_double * txt1_double / 100;
                    Get_Str_EndData = df.format(txt2_double);

                    txt_meas_2.setText(getResources().getText(R.string.str_CC_summarize).toString() + Get_Str_EndData + getResources().getText(R.string.str_unit_kgm3).toString());
                }
                else if(Get_title.equals("M_FAC"))
                {
//                    txt1_double = Get_Int_Sensor / Setting_Slope ;
//                    Get_Str_Sensor = df.format(txt1_double);
//                    txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + Get_Str_Sensor + getResources().getText(R.string.str_unit_percentage).toString());
                    double temp1 = 0, temp2 = 0,temp3 = 0;
                    temp1 = 500 * (1- (Keyin_double / 100));
                    temp2 = 1000 - temp1;
                    temp3 = temp2 / temp1;
                    txt2_double = temp3 * txt1_double ;
                    Get_Str_EndData = df.format(txt2_double);
                    txt_meas_2.setText(getResources().getText(R.string.str_FAC_summarize).toString() + Get_Str_EndData + getResources().getText(R.string.str_unit_percentage).toString());

                }
                else if(Get_title.equals("M_WC"))
                {
//                    txt1_double = Get_Int_Sensor / Setting_Slope ;
//                    Get_Str_Sensor = df.format(txt1_double);
//                    txt_meas_1.setText(getResources().getText(R.string.str_water_cl).toString() + Get_Str_Sensor + getResources().getText(R.string.str_unit_percentage).toString());
                    //txt_meas_2.setText(getResources().getText(R.string.str_FAC_summarize).toString() + getResources().getText(R.string.str_unit_percentage).toString());
                }

            }
            else{ //Measurement_Start_Flag = 0

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
