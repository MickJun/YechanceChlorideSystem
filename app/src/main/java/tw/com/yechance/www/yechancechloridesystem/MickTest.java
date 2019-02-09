package tw.com.yechance.www.yechancechloridesystem;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class MickTest extends Application {

    // your fields here
    private MainActivity MymainActivity = new MainActivity();

    public void setMainActivity(MainActivity mainActivity) {
        this.MymainActivity = mainActivity;
    }

    public MainActivity getMainActivity() {
        return MymainActivity;
    }

    private BluetoothSocket globalBlueSocket ;

    public void setGlobalBlueSocket(BluetoothSocket globalBlueSocket){
        this.globalBlueSocket = globalBlueSocket;
    }
    public BluetoothSocket getGlobalBlueSocket(){
        return globalBlueSocket;
    }




}
