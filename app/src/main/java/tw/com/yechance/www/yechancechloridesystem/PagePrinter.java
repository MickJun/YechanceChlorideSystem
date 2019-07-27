package tw.com.yechance.www.yechancechloridesystem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PagePrinter extends AppCompatActivity  implements View.OnClickListener, View.OnTouchListener{



    private IWoyouService woyouService;

    private Button btn_Print_Print;
    private Button btn_Print_Return;

    private TextView  txt_title;
    private TextView  txt_date;
    private TextView  txt_temperature;
    private TextView  txt_typing;
    private TextView  txt_1;
    private TextView  txt_2;

    private String BackupTxT_Date;
    private String BackupTxT_Temp;

    private String str_print_1,str_print_2,str_print_3,str_print_4;
    private String Str_title ="";
//    public void Update_Status(String str_Temp1, String str_Temp2,String str_Temp3,String str_Temp4) {
//        str_print_1 = str_Temp1;
//        str_print_2 = str_Temp2;
//        str_print_3 = str_Temp3;
//        str_print_4 = str_Temp4;
//    }

    private final Handler handler = new Handler();
    private int Touch_Down_count = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        hideBottomUIMenu();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_printer);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo2_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }
        hideBottomUIMenu();

        if(btn_Print_Print == null) {
            btn_Print_Print = this.findViewById(R.id.print_btn_print);
            //btn_Print_Print.setOnClickListener(this);
            btn_Print_Print.setOnTouchListener(this);
        }
        if(btn_Print_Return == null) {
            btn_Print_Return = this.findViewById(R.id.print_btn_return);
            btn_Print_Return.setOnClickListener(this);
        }
        if(txt_title == null){
            txt_title = this.findViewById(R.id.print_txt_title);
        }
        if(txt_date == null){
            txt_date = this.findViewById(R.id.print_txt_date);
            txt_date.setOnClickListener(this);
        }
        if(txt_temperature == null){
            txt_temperature = this.findViewById(R.id.print_txt_temp);
            txt_temperature.setOnClickListener(this);
        }
        if(txt_typing == null){
            txt_typing = this.findViewById(R.id.print_txt_typing);
        }
        if(txt_1 == null){
            txt_1 = this.findViewById(R.id.print_txt_1);
        }
        if(txt_2 == null){
            txt_2 = this.findViewById(R.id.print_txt_2);
        }

        //handler.postDelayed(this.runnable,500);



//        bundle.putString("title", "Data");
//        bundle.putString("date", "Data");
//        bundle.putString("temperature", "Data");
//        bundle.putString("typing", "Data");
//        bundle.putString("txt_1", "From");
//        bundle.putString("txt_2", "Load");
        //取的intent中的bundle物件
        Bundle bundle =this.getIntent().getExtras();
        txt_title.setText(bundle.getString("title"));
        Str_title = bundle.getString("title");
        txt_date.setText(bundle.getString("date"));
        BackupTxT_Date = txt_date.getText().toString();
        txt_temperature.setText(bundle.getString("temperature"));
        BackupTxT_Temp = txt_temperature.getText().toString();
        txt_typing.setText(bundle.getString("typing"));
        txt_1.setText(bundle.getString("txt_1"));
        if(Str_title.equals(getResources().getText(R.string.str_measurement_water_chloride))){
            txt_2.setText("");
        }else{
            txt_2.setText(bundle.getString("txt_2"));
        }



        if(Str_title.equals(getResources().getText(R.string.str_measurement_concrete_chloride))){
            txt_2.setTextSize(20);
        }

        Intent intent_P = new Intent();
        intent_P.setPackage("woyou.aidlservice.jiuiv5");
        intent_P.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent_P);//启动打印服务
        bindService(intent_P, connService, Context.BIND_AUTO_CREATE);

        //20170724 add
        txt_date.setText("");
        txt_temperature.setText("");
    }
    @Override
    public void onDestroy() {

        // Don't forget to unregister the ACTION_FOUND receiver.
        Intent intent_P = new Intent();
        intent_P.setPackage("woyou.aidlservice.jiuiv5");
        intent_P.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        stopService(intent_P);
        unbindService(connService);
        if(handler != null)handler.removeMessages(0);
        super.onDestroy();
    }


    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.print_btn_print:
//                try{
//                    woyouService.printTextWithFont("**" + txt_title.getText().toString() + "**\n","",30,callback);
//                    woyouService.printTextWithFont(txt_date.getText().toString()  +"\n","",30,callback);
//                    woyouService.printTextWithFont(txt_temperature.getText().toString()  +"\n","",30,callback);
//                    if(!Str_title.equals(getResources().getText(R.string.str_measurement_water_chloride))){
//                        woyouService.printTextWithFont(txt_typing.getText().toString()  +"\n","",30,callback);
//                    }
//                    woyouService.printTextWithFont(txt_1.getText().toString()  +"\n","",30,callback);
//                    if(Str_title.equals(getResources().getText(R.string.str_measurement_concrete_chloride))){
//                        woyouService.printTextWithFont(txt_2.getText().toString()  +"\n","",23,callback);
//                    }else{
//                        if(!Str_title.equals(getResources().getText(R.string.str_measurement_water_chloride))){
//                            woyouService.printTextWithFont(txt_2.getText().toString()  +"\n","",30,callback);
//                        }
//                    }
//                    woyouService.printTextWithFont("*********************\n","",36,callback);
//                    woyouService.printTextWithFont("會驗人員簽名\n","",30,callback);
//                    woyouService.printTextWithFont("\n","",30,callback);
//                    woyouService.printTextWithFont("\n","",30,callback);
//                    woyouService.printTextWithFont("\n","",30,callback);
//                    woyouService.printTextWithFont("*********************\n","",36,callback);
//                    woyouService.printTextWithFont("\n","",30,callback);
//                    woyouService.printTextWithFont("\n","",30,callback);
//                }catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            break;
            case R.id.print_btn_return:
                finish();
                break;
            case R.id.print_txt_date:
                if(txt_date.getText().equals("")){
                    //BackupTxT_Date
                    txt_date.setText(BackupTxT_Date);
                }
                else{
                    txt_date.setText("");
                }
                break;

            case R.id.print_txt_temp:
                if(txt_temperature.getText().equals("")){
                    //BackupTxT_Temp
                    txt_temperature.setText(BackupTxT_Temp);
                }
                else{
                    txt_temperature.setText("");
                }

                break;
        }
    }

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    ICallback callback = new ICallback.Stub() {

        @Override
        public void onRunResult(boolean success) throws RemoteException {
        }

        @Override
        public void onReturnString(final String value) throws RemoteException {
        }

        @Override
        public void onRaiseException(int code, final String msg)
                throws RemoteException {
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

    public void Print_data(int Now_date){
        try{
            woyouService.printTextWithFont("**" + txt_title.getText().toString() + "**\n","",30,callback);
            if(Now_date == 1)
            {
//                Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
//                String str = formatter.format(curDate);
                //woyouService.printTextWithFont("       "  +"\n","",30,callback);
            }
            else
            {
                if(!txt_date.getText().equals(""))woyouService.printTextWithFont(txt_date.getText().toString()  +"\n","",30,callback);
            }
            if(!txt_temperature.getText().equals(""))woyouService.printTextWithFont(txt_temperature.getText().toString()  +"\n","",30,callback);
            if(!Str_title.equals(getResources().getText(R.string.str_measurement_water_chloride))){
                woyouService.printTextWithFont(txt_typing.getText().toString()  +"\n","",30,callback);
            }
            woyouService.printTextWithFont(txt_1.getText().toString()  +"\n","",30,callback);
            if(Str_title.equals(getResources().getText(R.string.str_measurement_concrete_chloride))){
                String[] strTemp=txt_2.getText().toString().split("：");
                woyouService.printTextWithFont(strTemp[0] + "：" +"\n","",30,callback);
                woyouService.printTextWithFont("        " + strTemp[1] +"\n","",30,callback);


            }else{
                if(!Str_title.equals(getResources().getText(R.string.str_measurement_water_chloride))){
                    woyouService.printTextWithFont(txt_2.getText().toString()  +"\n","",30,callback);
                }
            }
            woyouService.printTextWithFont("*********************\n","",36,callback);
            //20190602 woyouService.printTextWithFont("會驗人員簽名\n","",30,callback);
            woyouService.printTextWithFont("\n","",30,callback);
            woyouService.printTextWithFont("\n","",30,callback);
            woyouService.printTextWithFont("\n","",30,callback);
            woyouService.printTextWithFont("*********************\n","",36,callback);
            woyouService.printTextWithFont("\n","",30,callback);
            woyouService.printTextWithFont("\n","",30,callback);
        }catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            switch (v.getId()) {
                case R.id.print_btn_print:
                    Touch_Down_count = 1;
                    handler.postDelayed(runnable,1000);
                    break;
            }
            Log.d("test", v.getId()+ " button ---> press");
        }

        //Release
        if(event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("test", v.getId() + " button ---> release");
            switch (v.getId()) {
                case R.id.print_btn_print:
                    if(Touch_Down_count > 0 && Touch_Down_count < 3){   //20190727 6>>3
                        Print_data(1);
                        Touch_Down_count = 0;
                    }
                    break;
            }
        }
        return false;
    }

//    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            if(Touch_Down_count > 0 && Touch_Down_count < 10)
            {
                Touch_Down_count++;
                if(Touch_Down_count == 3) //20190727 6>>3
                {
                    Print_data(0);
                    Touch_Down_count = 0;
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

}

