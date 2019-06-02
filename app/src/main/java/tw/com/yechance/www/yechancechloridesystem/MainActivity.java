package tw.com.yechance.www.yechancechloridesystem;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //Bluetooth
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final ArrayList<String> BT_Devicelist = new ArrayList<>();
    private final ArrayList<String> BT_Addrlist = new ArrayList<>();
    private int BT_Select_Point = 0;
    public BluetoothHeadset mBluetoothHeadset;
    public BluetoothSocket BTSocket;

    //取得內部儲存體擺放檔案的目錄
    //預設擺放目錄為 /data/data/[package.name]/file
    File dir = null;
    String datafilename = "data.txt";
    String settingfilename = "setting.txt";
    String Settingdata[][] = {{"temp0.1","0.1%","temp0.5","0.5%"},   //0.05% , 0.1% , 0.5%
            {"26","-1208","26","-659"}
    };
    String Readingdata[][] = {{"細粒料氯離子含量測定","2002年02月02日 22:22:22","2","22","222","2222"},
            {"混凝土氯離子含量測定","2001年01月01日 11:11:11","1","11","111","1111"},
            {"細粒料氯離子含量測定","2002年02月02日 22:22:22","2","22","222","2222"},
            {"水溶液氯離子含量測定","2003年03月03日 33:33:33","3","33","333","3333"}
    };

    File exDataFile = null;

    File exSettingFile = null;

    private String[][] File_Data_Array,File_Setting_Array;
    private String[] File_Read_Row_Array;


    private ListView Main_ListView ;
    private TextView Main_TextView ;
    private int int_Init_flag = 0,stop_first_cunnect = 0;

    private int tempdate[] = {
            //73.2
            32900,31901,30918,29954,29008,28083,27177,26291,25427,24583,23761,22960,22180,21422,20685,19970,19276,18602,17949,17317,
            16705,16112,15539,14985,14449,13932,13432,12949,12484,12034,11601,11183,10780,10392,10018,9658,9310,8976,8654,8344,
            8045,7758,7481,7215,6958,6712,6474,6246,6026,5814,5611,5415,5226,5045,4870,4702,4540,4385,4235,4090,
            3952,3818,3689,3565,3446,3331,3221,3114,3011,2912,2817,2725,2637,2552,2470,2390,2314,2240,2169,2101,
            2035,1971,1910,1851,1793,1738,1685,1634,1584,1536,1490,1445,1402,1360,1320,1281,1244,1207,1172,1138,1106

            //73.5
//            32820,31821,30839,29876,28932,28007,27103,26218,25355,24513,23692,22892,22114,21358,20622,19908,19215,18544,17892,17261,
//            16650,16059,15487,14935,14400,13884,13386,12905,12440,11992,11560,11144,10742,10355,9982,9623,9277,8943,8622,8313,
//            8016,7729,7453,7188,6932,6687,6450,6222,6003,5792,5589,5394,5206,5025,4851,4684,4523,4368,4218,4075,
//            3936,3803,3675,3551,3433,3318,3208,3102,2999,2901,2806,2715,2626,2542,2460,2381,2305,2231,2161,2093,
//            2027,1963,1902,1843,1786,1731,1678,1627,1578,1530,1484,1439,1396,1355,1315,1276,1239,1203,1168,1134,1101

            //12.1
//            32865,32528,32183,31831,31471,31103,30728,30346,29957,29562,29161,28754,28341,27924,27502,27075,26645,26212,25775,25336,
//            24894,24451,24007,23562,23116,22671,22226,21782,21339,20898,20460,20024,19590,19160,18734,18311,17893,17479,17070,16666,
//            16267,15874,15487,15105,14730,14360,13997,13641,13290,12947,12610,12280,11956,11640,11330,11027,10730,10441,10158,9881,
//            9612,9349,9092,8841,8597,8360,8128,7902,7682,7469,7260,7058,6861,6669,6483,6302,6125,5954,5788,5626,
//            5469,5317,5169,5025,4885,4749,4617,4489,4365,4245,4128,4014,3904,3797,3693,3592,3494,3399,3307,3218,
//            3131
    };

    // Get the default adapter
    public final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };

    private final AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
            //Toast.makeText(MainActivity.this, "點選第 " + (position + 1) + " 個 \n內容：" + BT_Addrlist.get(position).toString(), Toast.LENGTH_SHORT).show();
            BT_Select_Point = position;
            Main_TextView.setText(BT_Devicelist.get(position));
        }

    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //mytextview.setText(mytextview.getText() + "\n" + deviceName + deviceHardwareAddress);
                BT_Devicelist.add(deviceName); //this adds an element to the list.
                BT_Addrlist.add(deviceHardwareAddress);


            }

        }
    };

    private void BT_Scan(){

        //Bluetooth

        int REQUEST_ENABLE_BT = 1; // need greater then 0
        Main_TextView.setText("找不到設備！");
        if (mBluetoothAdapter == null) {
            Main_TextView.setText("您的裝置沒有支援藍芽");
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                Main_TextView.setText("藍芽未開啟！");//藍芽沒開拉，幹
            }
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Main_TextView.setText( "取消搜尋"); //(Fragment_TextView.getText() + "\n" + "cancelDiscovery")
            }

            //Querying paired devices
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            mBluetoothAdapter.startDiscovery();

            BT_Devicelist.clear();
            BT_Addrlist.clear();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    if(deviceName != "InnerPrinter")
                    {
                        BT_Devicelist.add(deviceName); //this adds an element to the list.
                        BT_Addrlist.add(deviceHardwareAddress);
                    }
                }
                //android.R.layout.simple_list_item_1 為內建樣式，還有其他樣式可自行研究
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, BT_Devicelist);
                Main_ListView.setAdapter(adapter);
                Main_ListView.setOnItemClickListener(onClickListView);       //指定事件 Method
                //Fragment1_TextView.setText("pair bluetooth is over");
                if(BT_Devicelist.size() ==0){
                    stop_first_cunnect = 1;
                    return;
                }
                else
                {
                    stop_first_cunnect = 0;
                }
                Main_TextView.setText(BT_Devicelist.get(0));
                btn_Main_Connect.setEnabled(true);
            }

            mBluetoothAdapter.cancelDiscovery();

            // Register for broadcasts when a device is discovered.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);

            // Establish connection to the proxy.
            mBluetoothAdapter.getProfileProxy(MainActivity.this, mProfileListener, BluetoothProfile.HEADSET);
            btn_Main_Connect.setEnabled(true);
        }
    }


    private void BT_Connecting(){
        String str_temp = "";
        if(mBluetoothAdapter.isDiscovering())mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice connDevices = mBluetoothAdapter.getRemoteDevice(BT_Addrlist.get(BT_Select_Point));//"00:11:22:33:44:55"
        //BluetoothDevice connDevices = mBluetoothAdapter.getRemoteDevice("98:D3:31:FD:86:0A");//"00:11:22:33:44:55"
        try {
            BTSocket = connDevices.createRfcommSocketToServiceRecord(MY_UUID);
            BTSocket.connect();
            readThread mReadThread = new readThread();
            mReadThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(BTSocket.isConnected()){
            btn_Main_Connect.setText("中斷連線");
            //Toast.makeText(getApplicationContext(), "連線成功 " ,	Toast.LENGTH_SHORT).show();
            str_temp = str_temp + "藍芽連線成功";
            btn_Main_start.setEnabled(true);
        }
        else{
            //Toast.makeText(getApplicationContext(), "連線失敗 " ,	Toast.LENGTH_SHORT).show();
            str_temp = str_temp + "藍芽連線失敗";
        }
        Toast.makeText(getApplicationContext(), str_temp,	Toast.LENGTH_SHORT).show();


    }

    private Button btn_Main_End;
    private Button btn_Main_Connect;
    private Button btn_Main_start;
    //public ButtonListener BtnListener = new ButtonListener();

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();

        if(btn_Main_End == null) {
            btn_Main_End = this.findViewById(R.id.main_btn_end);
            btn_Main_End.setOnClickListener(this);
        }
        if(btn_Main_Connect == null) {
            btn_Main_Connect = this.findViewById(R.id.main_btn_connect);
            btn_Main_Connect.setOnClickListener(this);
            btn_Main_Connect.setEnabled(false);
        }
        if(btn_Main_start == null) {
            btn_Main_start = this.findViewById(R.id.main_btn_start);
            btn_Main_start.setOnClickListener(this);
            btn_Main_start.setEnabled(false);
        }
        if(Main_TextView == null){
            Main_TextView = this.findViewById(R.id.main_txt_select);
        }
        if(Main_ListView == null){
            Main_ListView = this.findViewById(R.id.main_list_bt);
        }

        dir = this.getExternalFilesDir(null);
        //開啟或建立該目錄底下的檔案
        exDataFile = new File(dir, datafilename);

        File_Read_Row_Array = readFromFiletoArray(exDataFile);
        File_Data_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Data_Array[0][0].equals("") || File_Data_Array[0][0].equals("temp0.1")){
            writeToFile(exDataFile, Readingdata,Readingdata.length,6);
            File_Read_Row_Array = readFromFiletoArray(exDataFile);
            File_Data_Array = DataArrayfomat(File_Read_Row_Array);
        }

        exSettingFile = new File(dir, settingfilename);
        File_Read_Row_Array = readFromFiletoArray(exSettingFile);
        File_Setting_Array = DataArrayfomat(File_Read_Row_Array);
        if(File_Setting_Array[0][0].equals("") || !File_Setting_Array[0][0].equals("temp0.1") ){
            writeToFile(exDataFile, Settingdata,Settingdata.length,4);
            File_Read_Row_Array = readFromFiletoArray(exDataFile);
            File_Setting_Array = DataArrayfomat(File_Read_Row_Array);
        }


        first_write();
        handler.postDelayed(this.runnable,200);

        BT_Scan();
        ((MickTest) getApplication()).setMainActivity(this);
        int_Init_flag = 1;
    }

//    @Override
//    protected void onPause()   //按下退出鍵 系統預設呼叫 onPause
//    {
//        finish();
//        super.onDestroy(); //這行以防系統以為我亂呼叫
//    }

//
//    @Override
//    protected void onStop()
//    {
////        super.onDestroy();
////        super.onStop();
////        finish();
//    }
    @Override
    public void onDestroy() {

        // Don't forget to unregister the ACTION_FOUND receiver.
        //if(handler != null)handler.removeMessages(0);
        if(BTSocket != null && BTSocket.isConnected())
        {
            unregisterReceiver(mReceiver);
            try {
                BTSocket.close();
                mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(handler != null)handler.removeMessages(0);
        super.onDestroy();

        //Kill myself
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private MickTest myApplication = (MickTest) getApplication();
    private byte[] output_Final = {0x0a,0x0f,0x0b};

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.main_btn_end:
                finish();
                break;
            case R.id.main_btn_start:

                BT_Scan();
                if (BTSocket == null) {
                    Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
                    break;
                }
                ((MickTest) getApplication()).setMainActivity(this);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PageMainMenu.class);
                startActivity(intent);

                break;
            case R.id.main_btn_connect:
                if(BTSocket != null && BTSocket.isConnected() )
                {
                    try {
                        BTSocket.close();
                        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
                        btn_Main_Connect.setText("連線");
                        btn_Main_start.setEnabled(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    BT_Connecting();
                    //MainActivity.this.finish();
                }

                break;
        }
    }

    public void sendMessage(byte[] sendByte) {
        if (BTSocket == null) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = BTSocket.getOutputStream();
            os.write(sendByte);
            os.flush();
            //show("客户端:发送信息成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    byte[] Get_buff = new byte[1024];
    String Read_Str = "", Read_One = "",Read_INT = "";
    public int readADC(int Index_I) {
        int tempX,tempY;
        switch (Index_I){
            case 0 :
                tempX = ADC_0;
                break;
            case 1 : //temperature
                tempX = 0;
                tempY = ADC_1;

                for(int v=0; v < 100;v++)
                {
                    if(tempY >= tempdate[v])
                    {
                        tempX = v;
                        v = 120;
                    }
                    if(v == 100)
                    {
                        tempX = 100;
                    }
                }

                break;
            case 2 :
                tempX = ADC_2;
                break;
            case 3 :
                tempX = ADC_3;
                break;
            default:
                tempX = 0;
                break;
        }
        return tempX;
    }
    public void first_write(){
        for(int X=0;X<Filter_Size;X++){
            ADC_Filter[X]=ADC_Old;
        }
        ADC_0 = ADC_Old;
    }
    private int ADC_0 = 0,ADC_1 = 0, ADC_2 = 0, ADC_3 =0, ADC_F_Count = 0,ADC_Temp = 0,Filter_Size = 5,ADC_Old = 0;
    //ADC_Filter
    private int[] ADC_Filter = new int[30]; // = Filter_Size

    public class readThread extends Thread {

        public void run() {
            int bytes;
            InputStream is = null;
            try {
                is = BTSocket.getInputStream();
                //show("客户端:获得输入流");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if(is != null){
                byte[] buffer = new byte[1024];
                while (true) {
                    try{
                        if ((bytes = is.read(buffer)) > 0) {
                            byte[] buf_data = new byte[bytes];
                            System.arraycopy(buffer,0,buf_data,0,bytes);
//                            for (int i = 0; i < bytes; i++) {
//                                buf_data[i] = buffer[i];
//                            }
                            Read_Str = Read_Str + new String(buf_data);

                            if(Read_Str.length() > 5)
                            {
                                String[] splitStringArray = Read_Str.split("\r\n");
                                //Read_Str = splitStringArray[0];

                                for (int i = 0; i < splitStringArray.length; i++) {
                                    if(!splitStringArray[i].equals("")) {
                                        Read_One = splitStringArray[i].substring(0, 1);
                                        Read_INT = splitStringArray[i].substring(1);
                                        if (Read_INT.equals("-")) {
                                            Read_INT = "0";
                                        }
                                        if (!Read_INT.equals("")) {
                                            if (Read_One.equals("A")) {
                                                ADC_Filter[ADC_F_Count]  = Integer.parseInt(Read_INT);
                                                ADC_Old = ADC_Filter[ADC_F_Count];
                                                if(ADC_F_Count >= (Filter_Size - 1))ADC_F_Count = 0;
                                                else{
                                                    ADC_F_Count++;
                                                }
                                                ADC_Temp = 0;
                                                for(int z=0;z<Filter_Size;z++){
                                                    ADC_Temp +=  ADC_Filter[z];
                                                }
                                                ADC_0 = ADC_Temp / Filter_Size;


                                            }
                                            if (Read_One.equals("B")) {
                                                ADC_1 = Integer.parseInt(Read_INT);
                                            }
                                            if (Read_One.equals("C")) {
                                                ADC_2 = Integer.parseInt(Read_INT);
                                            }
                                            if (Read_One.equals("D")) {
                                                ADC_3 = Integer.parseInt(Read_INT);
                                            }
                                            Read_INT = "";
                                        }
                                    }
                                }
                                Read_Str = "";
                            }

                        }
                    } catch (IOException e) {

                        try {
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }

        }
    }



    private final Handler handler = new Handler();
    byte[] ttTemp ={0x0};
    private final Runnable runnable = new Runnable() {
        public void run() {

            if(int_Init_flag > 0)
            {
                int_Init_flag++;
            }
            if(int_Init_flag >= 3){
                int_Init_flag = 0;
                if(stop_first_cunnect == 0)
                {
                    BT_Connecting();
                }
                else
                {
                    stop_first_cunnect = 0;
                }
                if (BTSocket != null) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PageMainMenu.class);
                    startActivity(intent);
                }
            }

            if(ttTemp[0] < 1) {
                ttTemp[0]++;
            }
            else
            {
                ttTemp[0] = 0;
            }
            if (BTSocket != null && BTSocket.isConnected()){sendMessage(ttTemp);}
            handler.postDelayed(this,450);
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
}