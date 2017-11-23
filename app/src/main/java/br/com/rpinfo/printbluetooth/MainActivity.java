package br.com.rpinfo.printbluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import br.com.rpinfo.printbluetooth.bluetooth.Exception.PrinterBluetoothException;
import br.com.rpinfo.printbluetooth.bluetooth.ManagerBluetooth;

public class MainActivity extends AppCompatActivity {

    ManagerBluetooth managerBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            managerBluetooth = new ManagerBluetooth(this);
        } catch (PrinterBluetoothException e) {
            e.printStackTrace();
        }
        if(managerBluetooth.ativaBluetooth()){
            Toast.makeText(this, "Bluetooth esta ligado", Toast.LENGTH_SHORT).show();
        }

        ((Button) findViewById(R.id.btnConectar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                managerBluetooth.conectarDispositivo();
            }
        });

    }


}
