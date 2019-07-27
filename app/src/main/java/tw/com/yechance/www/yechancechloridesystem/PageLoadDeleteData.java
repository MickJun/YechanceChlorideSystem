package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class PageLoadDeleteData extends AppCompatActivity implements View.OnClickListener{

    private Button btn_data_return;
    private Button btn_data_load;
    private Button btn_data_delete;

    private ListView data_ListView ;
    private TextView data_TextView ;

    private String[][] File_Save_Array;
    private String[] File_Read_Row_Array;

    private int List_Select_Point = 0;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(PageLoadDeleteData.this, android.R.layout.simple_list_item_1, LD_Datalist);
        data_ListView.setAdapter(adapter);
        data_ListView.setOnItemClickListener(onClickListView);       //指定事件 Method
        data_TextView.setText(LD_Datalist.get(0));
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.data_btn_load:


                String  txt_meas_title          = File_Save_Array[List_Select_Point][0];
                String  txt_meas_date           = File_Save_Array[List_Select_Point][1];
                String  txt_meas_temperature    = getResources().getText(R.string.str_temperature).toString() + File_Save_Array[List_Select_Point][2] + getResources().getText(R.string.str_unit_tmpeC).toString() ;

                String  txt_meas_typing         = File_Save_Array[List_Select_Point][3];
                String  txt_meas_1              = File_Save_Array[List_Select_Point][4];
                String  txt_meas_2              = File_Save_Array[List_Select_Point][5];


                if(txt_meas_title.equals("混凝土氯離子含量測定"))
                {
                    txt_meas_typing   = getResources().getText(R.string.str_water_unit).toString() + File_Save_Array[List_Select_Point][3] + getResources().getText(R.string.str_unit_kgm3).toString();
                    txt_meas_1 = getResources().getText(R.string.str_water_cl).toString() + File_Save_Array[List_Select_Point][4] + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_2 = getResources().getText(R.string.str_CC_summarize).toString() + File_Save_Array[List_Select_Point][5] + getResources().getText(R.string.str_unit_kgm3).toString(); //2019/7/15 Mick
                    //2019/7/15 Mick  txt_meas_2 = getResources().getText(R.string.str_CC_summarize).toString() + File_Save_Array[List_Select_Point][5] + getResources().getText(R.string.str_unit_percentage).toString();


                }
                else if(txt_meas_title.equals("細粒料氯離子含量測定"))
                {
                    txt_meas_typing         = getResources().getText(R.string.str_water_rate).toString() + File_Save_Array[List_Select_Point][3] + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_1 = getResources().getText(R.string.str_water_cl).toString() + File_Save_Array[List_Select_Point][4] + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_2 = getResources().getText(R.string.str_FAC_summarize).toString() + File_Save_Array[List_Select_Point][5] + getResources().getText(R.string.str_unit_percentage).toString();

                }
                else if(txt_meas_title.equals("水溶液氯離子含量測定"))
                {
                    txt_meas_1 = getResources().getText(R.string.str_water_cl).toString() + File_Save_Array[List_Select_Point][4] + getResources().getText(R.string.str_unit_percentage).toString();
                    txt_meas_2 = getResources().getText(R.string.str_FAC_summarize).toString() + File_Save_Array[List_Select_Point][5] + getResources().getText(R.string.str_unit_percentage).toString();
                }


                Intent intent_S = new Intent();
                intent_S.setClass(PageLoadDeleteData.this, PagePrinter.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("title", txt_meas_title);
                bundle.putString("date", txt_meas_date);
                bundle.putString("temperature", txt_meas_temperature);
                bundle.putString("typing",txt_meas_typing) ;
                bundle.putString("txt_1", txt_meas_1);
                bundle.putString("txt_2", txt_meas_2);
                //將Bundle物件assign給intent
                intent_S.putExtras(bundle);

                startActivity(intent_S);
                break;
            case R.id.data_btn_delete:
                DeleteDataRow(List_Select_Point);
                break;
            case R.id.data_btn_return:
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, PageMainMenu.class);
//                startActivity(intent);
                finish();
                break;
        }
    }

    private final AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
            //Toast.makeText(MainActivity.this, "點選第 " + (position + 1) + " 個 \n內容：" + BT_Addrlist.get(position).toString(), Toast.LENGTH_SHORT).show();
            List_Select_Point = position;
            data_TextView.setText(LD_Datalist.get(position));
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
            LD_Datalist.add(retrunArray[i][0] + "：" + "\r\n" + retrunArray[i][1] );
        }
        if(retrunArray[0][0].equals("")){
            LD_Datalist.clear();
        }
        return retrunArray;
    }

    private void DeleteDataRow(int selectPoint)
    {
        //將檔案存放在 getExternalFilesDir() 目錄
        File_Save_Array[selectPoint][0] = "";
        if (isExtStorageWritable()){
            writeToFile(exDataFile, File_Save_Array,File_Save_Array.length,6);
        }
        File_Read_Row_Array = readFromFiletoArray(exDataFile);
        File_Save_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Save_Array[0][0].equals("")){
            btn_data_load.setEnabled(false);
            btn_data_delete.setEnabled(false);
            data_ListView.setAdapter(null);
            data_TextView.setText("");
        }
        else
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(PageLoadDeleteData.this, android.R.layout.simple_list_item_1, LD_Datalist);
            data_ListView.setAdapter(adapter);
            data_ListView.setOnItemClickListener(onClickListView);       //指定事件 Method
            data_TextView.setText(LD_Datalist.get(0));
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
