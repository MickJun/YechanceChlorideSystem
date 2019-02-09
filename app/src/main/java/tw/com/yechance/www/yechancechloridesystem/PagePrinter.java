package tw.com.yechance.www.yechancechloridesystem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PagePrinter extends AppCompatActivity  implements View.OnClickListener{



    private IWoyouService woyouService;

    private Button btn_Print_Print;
    private Button btn_Print_Return;

    private TextView  txt_title;
    private TextView  txt_date;
    private TextView  txt_temperature;

    private TextView  txt_1;
    private TextView  txt_2;
    private TextView  txt_3;
    private TextView  txt_4;

    private String str_print_1,str_print_2,str_print_3,str_print_4;

//    public void Update_Status(String str_Temp1, String str_Temp2,String str_Temp3,String str_Temp4) {
//        str_print_1 = str_Temp1;
//        str_print_2 = str_Temp2;
//        str_print_3 = str_Temp3;
//        str_print_4 = str_Temp4;
//    }
//    private final Handler handler = new Handler();
//    private final Runnable runnable = new Runnable() {
//        public void run() {
//            txt_1.setText(str_print_1);
//            txt_2.setText(str_print_2);
//            txt_3.setText(str_print_3);
//            txt_4.setText(str_print_4);
//            handler.postDelayed(this,500);
//        }
//    };

            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_printer);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayShowHomeEnabled(true);
            this.getSupportActionBar().setLogo(R.drawable.yechance_logo_s);
            this.getSupportActionBar().setDisplayUseLogoEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_backcolor));
        }

        if(btn_Print_Print == null) {
            btn_Print_Print = this.findViewById(R.id.print_btn_print);
            btn_Print_Print.setOnClickListener(this);
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
        }
        if(txt_temperature == null){
            txt_temperature = this.findViewById(R.id.print_txt_temp);
        }
        if(txt_1 == null){
            txt_1 = this.findViewById(R.id.print_txt_1);
        }
        if(txt_2 == null){
            txt_2 = this.findViewById(R.id.print_txt_2);
        }
        if(txt_3 == null){
            txt_3 = this.findViewById(R.id.print_txt_3);
        }
        if(txt_4 == null){
            txt_4 = this.findViewById(R.id.print_txt_4);
        }

        //handler.postDelayed(this.runnable,500);

        //取的intent中的bundle物件
        Bundle bundle =this.getIntent().getExtras();
        str_print_1 = bundle.getString("txt_1");;
        str_print_2 = bundle.getString("txt_2");;
        str_print_3 = bundle.getString("txt_3");;
        str_print_4 = bundle.getString("txt_4");;
        txt_1.setText(str_print_1);
        txt_2.setText(str_print_2);
        txt_3.setText(str_print_3);
        txt_4.setText(str_print_4);

        Intent intent_P = new Intent();
        intent_P.setPackage("woyou.aidlservice.jiuiv5");
        intent_P.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent_P);//启动打印服务
        bindService(intent_P, connService, Context.BIND_AUTO_CREATE);

    }
    @Override
    public void onDestroy() {

        // Don't forget to unregister the ACTION_FOUND receiver.
        Intent intent_P = new Intent();
        intent_P.setPackage("woyou.aidlservice.jiuiv5");
        intent_P.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        stopService(intent_P);
        unbindService(connService);
        super.onDestroy();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print_btn_print:
                try{
                    woyouService.printTextWithFont("*****" + txt_title.getText().toString() + "*****\n","",36,callback);
                    woyouService.printTextWithFont(txt_date.getText().toString()  +"\n","",36,callback);
                    woyouService.printTextWithFont(txt_temperature.getText().toString()  +"\n","",36,callback);
                    woyouService.printTextWithFont(txt_1.getText().toString()  +"\n","",36,callback);
                    woyouService.printTextWithFont(txt_2.getText().toString()  +"\n","",36,callback);
                    woyouService.printTextWithFont(txt_3.getText().toString()  +"\n","",36,callback);
                    woyouService.printTextWithFont(txt_4.getText().toString() +"\n","",36,callback);
                    woyouService.printTextWithFont("*********************\n","",36,callback);
                    woyouService.printTextWithFont("signature\n","",36,callback);
                    woyouService.printTextWithFont("\n","",36,callback);
                    woyouService.printTextWithFont("\n","",36,callback);
                    woyouService.printTextWithFont("\n","",36,callback);
                    woyouService.printTextWithFont("*********************\n","",36,callback);
                    woyouService.printTextWithFont("\n","",36,callback);
                    woyouService.printTextWithFont("\n","",36,callback);
                }catch (RemoteException e) {
                    e.printStackTrace();
                }
            break;
            case R.id.print_btn_return:
                finish();
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

}
