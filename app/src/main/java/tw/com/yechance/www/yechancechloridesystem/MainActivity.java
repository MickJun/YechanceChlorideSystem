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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    //Bluetooth
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final ArrayList<String> BT_Devicelist = new ArrayList<>();
    private final ArrayList<String> BT_Addrlist = new ArrayList<>();
    private int BT_Select_Point = 0;
    public BluetoothHeadset mBluetoothHeadset;
    public BluetoothSocket BTSocket;


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
                    BT_Devicelist.add(deviceName); //this adds an element to the list.
                    BT_Addrlist.add(deviceHardwareAddress);
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

        if(mBluetoothAdapter.isDiscovering())mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice connDevices = mBluetoothAdapter.getRemoteDevice(BT_Addrlist.get(BT_Select_Point));
        try {
            BTSocket = connDevices.createRfcommSocketToServiceRecord(MY_UUID);
            BTSocket.connect();
            readThread mReadThread = new readThread();
            mReadThread.start();
            if(BTSocket.isConnected()){
                btn_Main_Connect.setText("中斷連線");
                Intent intent = new Intent();
                intent.setClass(this,PageMainMenu.class);
                startActivity(intent);
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Button btn_Main_Scan;
    public Button btn_Main_Connect;
    public ButtonListener BtnListener = new ButtonListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        if(btn_Main_Scan == null) {
            btn_Main_Scan = this.findViewById(R.id.main_btn_scan);
            btn_Main_Scan.setOnClickListener(BtnListener);
        }
        if(btn_Main_Connect == null) {
            btn_Main_Connect = this.findViewById(R.id.main_btn_connect);
            btn_Main_Connect.setOnClickListener(BtnListener);
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
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
        //if(handler != null)handler.removeMessages(0);
        if(BTSocket != null && BTSocket.isConnected())
        {
            try {
                BTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    class ButtonListener implements View.OnClickListener{

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.main_btn_scan:
                    BT_Scan();
                    break;
                case R.id.main_btn_connect:
                    BT_Connecting();
                    break;
            }
        }
    }

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



}

