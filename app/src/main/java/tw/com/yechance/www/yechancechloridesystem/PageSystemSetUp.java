package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PageSystemSetUp extends AppCompatActivity implements View.OnClickListener{

    private Button btn_ave_return;
    private Button btn_ave_ave;
    private Button btn_ave_clean;

    private ListView ave_ListView ;
    private TextView ave_TextView1 ;
    private TextView ave_TextView2 ;
    private TextView ave_TextView3 ;

    private int select_TV_Num = 1;

    private String[][] File_Save_Array;
    private String[] File_Read_Row_Array;

    private int List_Select_Point = 0, List_Select_Point1 = 0,List_Select_Point2 = 0,List_Select_Point3 = 0, First_Select_Point = 0,Last_Select_Point = 0;
    private String Select_Function_Name = "";
    private DecimalFormat df = new DecimalFormat("##0.0000");

    private DecimalFormat df1 = new DecimalFormat("##0.0");

    private final ArrayList<String> LD_Datalist = new ArrayList<>();

    //取得內部儲存體擺放檔案的目錄
    //預設擺放目錄為 /data/data/[package.name]/file
    File dir = null;
    String datafilename = "data.txt";
    String settingfilename = "setting.txt";
//    String Sdata[][] = {{"細粒料氯離子含量測定","2002/02/02 22:22:22","2","22","222","2222"},
//            {"混凝土氯離子含量測定","2001/01/01 11:11:11","1","11","111","1111"},
//            {"細粒料氯離子含量測定","2002/02/02 22:22:22","2","22","222","2222"},
//            {"水溶液氯離子含量測定","2003/03/03 33:33:33","3","33","333","3333"}
//    };

    String Settingdata[][] = {{"temp0.1","0.1%","temp0.5","0.5%"},   //0.05% , 0.1% , 0.5%
            {"26","-1208","26","-659"}
    };
    String Readingdata[][] = {{"細粒料氯離子含量測定","2002年02月02日 22:22:22","2","22","222","2222"},
            {"混凝土氯離子含量測定","2001年01月01日 11:11:11","1","11","111","1111"},
            {"細粒料氯離子含量測定","2002年02月02日 22:22:22","2","22","222","2222"},
            {"水溶液氯離子含量測定","2003年03月03日 33:33:33","3","33","333","3333"}
    };


    File exDataFile = null;

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_system_set_up);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();
        if(btn_ave_return == null) {
            btn_ave_return = this.findViewById(R.id.ave_btn_return);
            btn_ave_return.setOnClickListener(this);
        }
        if(btn_ave_ave == null) {
            btn_ave_ave = this.findViewById(R.id.ave_btn_ave);
            btn_ave_ave.setOnClickListener(this);
        }
        if(btn_ave_clean == null) {
            btn_ave_clean = this.findViewById(R.id.ave_btn_clear);
            btn_ave_clean.setOnClickListener(this);
        }
        if(ave_TextView1 == null){
            ave_TextView1 = this.findViewById(R.id.ave_txt_select1);
            ave_TextView1.setOnClickListener(this);
        }
        if(ave_TextView2 == null){
            ave_TextView2 = this.findViewById(R.id.ave_txt_select2);
            ave_TextView2.setOnClickListener(this);
        }
        if(ave_TextView3 == null){
            ave_TextView3 = this.findViewById(R.id.ave_txt_select3);
            ave_TextView3.setOnClickListener(this);
        }
        if(ave_ListView == null){
            ave_ListView = this.findViewById(R.id.ave_list_txt);
        }

        dir = this.getExternalFilesDir(null);
        //開啟或建立該目錄底下的檔案
        exDataFile = new File(dir, datafilename);

        //將檔案存放在 getExternalFilesDir() 目錄
        if (isExtStorageWritable()){
        }
        //讀取 /data/data/com.myapp/test.txt 檔案內容
        File_Read_Row_Array = readFromFiletoArray(exDataFile);
        File_Save_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Save_Array[0][0].equals("") || File_Save_Array[0][0].equals("temp0.1")){
            writeToFile(exDataFile, Readingdata,Readingdata.length,6);
            File_Read_Row_Array = readFromFiletoArray(exDataFile);
            File_Save_Array = DataArrayfomat(File_Read_Row_Array);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LD_Datalist);
        ave_ListView.setAdapter(adapter);
        ave_ListView.setOnItemClickListener(onClickListView);       //指定事件 Method
        //ave_TextView1.setText(LD_Datalist.get(0));

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ave_btn_ave:

                if(Last_Select_Point == 0){break;}


                String  txt_meas_title          = File_Save_Array[Last_Select_Point][0];


                String  txt_meas_date           = File_Save_Array[List_Select_Point1][1];
                String  txt_meas_temperature    = getResources().getText(R.string.str_temperature).toString() + File_Save_Array[List_Select_Point1][2] + getResources().getText(R.string.str_unit_tmpeC).toString() ;

                String  txt_meas_0              = File_Save_Array[List_Select_Point1][3];
                String  txt_meas_1              = File_Save_Array[List_Select_Point1][4];
                String  txt_meas_2              = File_Save_Array[List_Select_Point1][5];

                double P1_m0 = 0,P1_m1 = 0,P1_m2 = 0;
                double P2_m0 = 0,P2_m1 = 0,P2_m2 = 0;
                double P3_m0 = 0,P3_m1 = 0,P3_m2 = 0;
                double PAll_m0 = 0,PAll_m1 = 0,PAll_m2 = 0;
                int Data_count = 0;

                if(List_Select_Point1 != 0)
                {
                    if(!txt_meas_0.equals("")){P1_m0              = Double.parseDouble(txt_meas_0);}
                    P1_m1              = Double.parseDouble(txt_meas_1);
                    P1_m2              = Double.parseDouble(txt_meas_2);
                    Data_count ++;
                }
                if(List_Select_Point2 != 0)
                {
                    txt_meas_0              = File_Save_Array[List_Select_Point2][3];
                    txt_meas_1              = File_Save_Array[List_Select_Point2][4];
                    txt_meas_2              = File_Save_Array[List_Select_Point2][5];

                    if(!txt_meas_0.equals("")){P2_m0              = Double.parseDouble(txt_meas_0);}
                    P2_m1              = Double.parseDouble(txt_meas_1);
                    P2_m2              = Double.parseDouble(txt_meas_2);
                    Data_count ++;
                }
                if(List_Select_Point3 != 0)
                {
                    txt_meas_0              = File_Save_Array[List_Select_Point3][3];
                    txt_meas_1              = File_Save_Array[List_Select_Point3][4];
                    txt_meas_2              = File_Save_Array[List_Select_Point3][5];

                    if(!txt_meas_0.equals("")){P3_m0              = Double.parseDouble(txt_meas_0);}
                    P3_m1              = Double.parseDouble(txt_meas_1);
                    P3_m2              = Double.parseDouble(txt_meas_2);
                    Data_count ++;
                }

                PAll_m0 = (P1_m0 + P2_m0 + P3_m0)/Data_count;
                PAll_m1 = (P1_m1 + P2_m1 + P3_m1)/Data_count;
                PAll_m2 = (P1_m2 + P2_m2 + P3_m2)/Data_count;

                String Strm0 =  df1.format(PAll_m0);
                String Strm1 =  df.format(PAll_m1);
                String Strm2 =  df.format(PAll_m2);

                if(txt_meas_title.equals("混凝土氯離子含量測定"))
                {
                    txt_meas_0   = getResources().getText(R.string.str_water_unit).toString() + Strm0 + getResources().getText(R.string.str_unit_kgm3).toString();
                    txt_meas_1 = getResources().getText(R.string.str_water_cl).toString() + Strm1 + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_2 = getResources().getText(R.string.str_CC_summarize).toString() + Strm2 + getResources().getText(R.string.str_unit_kgm3).toString(); //2019/7/15 Mick
                    //2019/7/15 Mick  txt_meas_2 = getResources().getText(R.string.str_CC_summarize).toString() + File_Save_Array[List_Select_Point][5] + getResources().getText(R.string.str_unit_percentage).toString();

                }
                else if(txt_meas_title.equals("細粒料氯離子含量測定"))
                {
                    txt_meas_0  = getResources().getText(R.string.str_water_rate).toString() + Strm0 + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_1 = getResources().getText(R.string.str_water_cl).toString() + Strm1 + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_2 = getResources().getText(R.string.str_FAC_summarize).toString() + Strm2 + getResources().getText(R.string.str_unit_percentage).toString();

                }
                else if(txt_meas_title.equals("水溶液氯離子含量測定"))
                {
                    txt_meas_1 = getResources().getText(R.string.str_water_cl).toString() + Strm1 + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_2 = getResources().getText(R.string.str_FAC_summarize).toString() + Strm2 + getResources().getText(R.string.str_unit_percentage).toString();
                }


                Intent intent_S = new Intent();
                intent_S.setClass(this, PagePrinter.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("title", txt_meas_title);
                bundle.putString("date", txt_meas_date);
                bundle.putString("temperature", txt_meas_temperature);
                bundle.putString("typing",txt_meas_0) ;
                bundle.putString("txt_1", txt_meas_1);
                bundle.putString("txt_2", txt_meas_2);
                //將Bundle物件assign給intent
                intent_S.putExtras(bundle);

                startActivity(intent_S);
                break;
            case R.id.ave_btn_clear:
                select_TV_Num = 1;
                ave_TextView1.setText(this.getResources().getText(R.string.str_select_data));
                ave_TextView2.setText(this.getResources().getText(R.string.str_select_data));
                ave_TextView3.setText(this.getResources().getText(R.string.str_select_data));
                Select_Function_Name ="";
                Last_Select_Point = 0;
                List_Select_Point1 = 0;
                List_Select_Point2 = 0;
                List_Select_Point3 = 0;
                ave_TextView1.setTextColor(this.getResources().getColor(R.color.red));
                ave_TextView2.setTextColor(this.getResources().getColor(R.color.gray));
                ave_TextView3.setTextColor(this.getResources().getColor(R.color.gray));
                break;
            case R.id.ave_btn_return:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.ave_txt_select1:
                select_TV_Num = 1;
                ave_TextView1.setTextColor(this.getResources().getColor(R.color.red));
                ave_TextView2.setTextColor(this.getResources().getColor(R.color.gray));
                ave_TextView3.setTextColor(this.getResources().getColor(R.color.gray));
                break;
            case R.id.ave_txt_select2:
                if(List_Select_Point1 == 0){break;}
                select_TV_Num = 2;
                ave_TextView1.setTextColor(this.getResources().getColor(R.color.gray));
                ave_TextView2.setTextColor(this.getResources().getColor(R.color.red));
                ave_TextView3.setTextColor(this.getResources().getColor(R.color.gray));
                break;
            case R.id.ave_txt_select3:
                if(List_Select_Point2 == 0){break;}
                select_TV_Num = 3;
                ave_TextView1.setTextColor(this.getResources().getColor(R.color.gray));
                ave_TextView2.setTextColor(this.getResources().getColor(R.color.gray));
                ave_TextView3.setTextColor(this.getResources().getColor(R.color.red));
                break;
        }
    }

    private final AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
            //Toast.makeText(MainActivity.this, "點選第 " + (position + 1) + " 個 \n內容：" + BT_Addrlist.get(position).toString(), Toast.LENGTH_SHORT).show();

            //if(ave_TextView1.getText().equals(getResources().getText(R.string.str_select_data)) && ave_TextView2.getText().equals(getResources().getText(R.string.str_select_data)) && ave_TextView3.getText().equals(getResources().getText(R.string.str_select_data))){
            if(Last_Select_Point == 0){
                  List_Select_Point = position;
                  Select_Function_Name = File_Save_Array[position][0];
                  First_Select_Point = select_TV_Num;
            }
            if(Select_Function_Name.equals(File_Save_Array[position][0]) || First_Select_Point == select_TV_Num){
                if(select_TV_Num == 1){
                    ave_TextView1.setText(LD_Datalist.get(position));
                    List_Select_Point1 = position;
                    Last_Select_Point = position;
                }
                else if(select_TV_Num == 2){
                    ave_TextView2.setText(LD_Datalist.get(position));
                    List_Select_Point2 = position;
                    Last_Select_Point = position;
                }
                else if(select_TV_Num == 3){
                    ave_TextView3.setText(LD_Datalist.get(position));
                    List_Select_Point3 = position;
                    Last_Select_Point = position;
                }
                if(First_Select_Point != select_TV_Num)
                {
                    First_Select_Point = 10;
                }
            }
            else{
                //Toast.makeText(getApplicationContext(), "請選擇相同類型的資料",	Toast.LENGTH_SHORT).show();
            }


        }

    };



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



//        FileOutputStream osw = null;
//        try {
//            osw = new FileOutputStream(fout);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(osw);
//            BufferedWriter bw = new BufferedWriter(myOutWriter);
//
//            for (int i = 0; i < length; i++) {
//                bw.write(data[i]);
//                bw.newLine();
//            }
//
//            bw.close();
////            osw.write(data.getBytes());
//            //osw.write(data[0].getBytes());
//            //osw.write(data[1].getBytes());
//            //osw.write("\n\r".getBytes());
//            //osw.write(data[2].getBytes());
//            //osw.write("\n\r".getBytes());
////            for (int i = 0; i < length; i++) {
////                myOutWriter.append(data[i] + "\n");
////            }
////
////            myOutWriter.append(data);
////
////            myOutWriter.append("\n\r");
//            myOutWriter.close();
//            osw.flush();
//        } catch (Exception e) {
//        } finally {
//            try {
//                osw.close();
//            } catch (Exception e) {
//            }
//        }
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
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
        return data.toString();
    }

    private String[][] DataArrayfomat(String[] inArray)
    {
        LD_Datalist.clear();
        String[][] retrunArray = new String[inArray.length][6];
        for(int i=0; i<inArray.length;i++){
            String[] splitArray = inArray[i].split(",");
            for(int j=0; j<splitArray.length;j++){
                retrunArray[i][j] = splitArray[j];
            }
            LD_Datalist.add(retrunArray[i][0] + "："+ "\r\n" + retrunArray[i][1] );
        }
        if(retrunArray[0][0].equals("")){
            LD_Datalist.clear();
        }
        return retrunArray;
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
