package tw.com.yechance.www.yechancechloridesystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
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
    public BluetoothSocket PTSocket;


    private ListView Main_ListView ;
    private TextView Main_TextView ;

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
        Main_TextView.setText("藍芽沒開拉，幹！");
        if (mBluetoothAdapter == null) {
            Main_TextView.setText("您的裝置沒有支援藍芽");
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                Main_TextView.setText("藍芽沒開拉，幹！");
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
                Main_TextView.setText(BT_Devicelist.get(0));
                btn_Main_Connect.setEnabled(true);
            }

            mBluetoothAdapter.cancelDiscovery();

            // Register for broadcasts when a device is discovered.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);

            // Establish connection to the proxy.
            mBluetoothAdapter.getProfileProxy(MainActivity.this, mProfileListener, BluetoothProfile.HEADSET);

        }
    }


    private void BT_Connecting(){
        String str_temp = "";
        if(mBluetoothAdapter.isDiscovering())mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice connDevices = mBluetoothAdapter.getRemoteDevice(BT_Addrlist.get(BT_Select_Point));//"00:11:22:33:44:55"
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
            str_temp = str_temp + "BT連線成功";
        }
        else{
            //Toast.makeText(getApplicationContext(), "連線失敗 " ,	Toast.LENGTH_SHORT).show();
            str_temp = str_temp + "BT連線失敗";
        }
//        //printer
//        BluetoothDevice connPrinter = mBluetoothAdapter.getRemoteDevice("00:11:22:33:44:55");
//        try {
//            PTSocket = connPrinter.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//            PTSocket.connect();
//            PTreadThread mPTReadThread = new PTreadThread();
//            mPTReadThread.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(PTSocket.isConnected()){
//            str_temp = str_temp + "PT連線成功";
//            byte[] Output_Final = {0x1d,0x56,0x42,0x00};
//            try {
//                OutputStream os = PTSocket.getOutputStream();
//                os.write(Output_Final);
//                os.flush();
//                //show("客户端:发送信息成功");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//        else{
//            str_temp = str_temp + "PT連線失敗";
//        }
        Toast.makeText(getApplicationContext(), str_temp,	Toast.LENGTH_SHORT).show();


    }

    private Button btn_Main_Scan;
    private Button btn_Main_Connect;
    //public ButtonListener BtnListener = new ButtonListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        if(btn_Main_Scan == null) {
            btn_Main_Scan = this.findViewById(R.id.main_btn_scan);
            btn_Main_Scan.setOnClickListener(this);
        }
        if(btn_Main_Connect == null) {
            btn_Main_Connect = this.findViewById(R.id.main_btn_connect);
            btn_Main_Connect.setOnClickListener(this);
        }
        if(Main_TextView == null){
            Main_TextView = this.findViewById(R.id.main_txt_select);
        }
        if(Main_ListView == null){
            Main_ListView = this.findViewById(R.id.main_list_bt);
        }


    }

    @Override
    public void onDestroy() {

        // Don't forget to unregister the ACTION_FOUND receiver.
        //if(handler != null)handler.removeMessages(0);
        if(BTSocket != null && BTSocket.isConnected())
        {
            try {
                BTSocket.close();
                if(BTSocket != null && BTSocket.isConnected() ){
                    PTSocket.close();
                }
                mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
                unregisterReceiver(mReceiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregisterReceiver(mReceiver);
    }


    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.main_btn_scan:
                BT_Scan();
                break;
            case R.id.main_btn_connect:
                if(BTSocket != null && BTSocket.isConnected() )
                {
                    try {
                        BTSocket.close();
                        if(BTSocket != null && BTSocket.isConnected() ){
                            PTSocket.close();
                        }
                        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
                        btn_Main_Connect.setText("連線");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    BT_Connecting();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PageMainMenu.class);
                    startActivity(intent);
                    //MainActivity.this.finish();
                }

                break;
        }
    }
//    class ButtonListener implements View.OnClickListener{
//
//
//    }

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
                            System.arraycopy(buffer,0,buf_data,0,bytes-1);
//                            for (int i = 0; i < bytes; i++) {
//                                buf_data[i] = buffer[i];
//                            }
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

    public class PTreadThread extends Thread {

        public void run() {
            int bytes;
            InputStream is = null;
            try {
                is = PTSocket.getInputStream();
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
                            System.arraycopy(buffer,0,buf_data,0,bytes-1);
//                            for (int i = 0; i < bytes; i++) {
//                                buf_data[i] = buffer[i];
//                            }
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



}

