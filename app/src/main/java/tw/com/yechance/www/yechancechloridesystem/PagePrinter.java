package tw.com.yechance.www.yechancechloridesystem;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PagePrinter extends AppCompatActivity  implements View.OnClickListener{

    WebView mWebView;


    private IWoyouService woyouService;
    private IWoyouService woyouService2;

    private Button btn_Print_Print;
    private Button btn_Print_Return;
    @SuppressLint("SetJavaScriptEnabled")
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
                    woyouService.printTextWithFont("商⽶\n","",36,callback);
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
