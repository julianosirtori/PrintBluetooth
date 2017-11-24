package br.com.rpinfo.printbluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import br.com.rpinfo.printbluetooth.bluetooth.EscCommand;
import br.com.rpinfo.printbluetooth.bluetooth.Exception.PrinterBluetoothException;
import br.com.rpinfo.printbluetooth.bluetooth.ManagerBluetooth;
import br.com.rpinfo.printbluetooth.bluetooth.PrintController;

public class MainActivity extends AppCompatActivity {

    ManagerBluetooth managerBluetooth;
    List<BluetoothDevice> bluetoothDevices;
    PrintController printController;

    boolean conectado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();

        ((Button) findViewById(R.id.btnConectar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conectado) {
                    desconectar();
                } else {
                    conectar();
                }
            }
        });

        ((Button) findViewById(R.id.btnImprimirTexto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EscCommand escCommand = new EscCommand();
                escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                escCommand.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                escCommand.addText("Print text\n");
                escCommand.addText("Welcome to use Gprinter!\n");
                escCommand.addText(((TextView) findViewById(R.id.edtTexto)).getText().toString());
                escCommand.addText("\n");
                escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                try {
                    printController.sendPrint(escCommand.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        ((Button) findViewById(R.id.btnBitmapDefault)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EscCommand escCommand = new EscCommand();
                escCommand.addRastBitImage(BitmapFactory.decodeResource(getResources(),
                        R.drawable.etiqueta), 100, 1);

                Vector<Byte> datas = escCommand.getCommand();
                Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
                byte[] bytes = ArrayUtils.toPrimitive(Bytes);

                try {
                    printController.sendPrint(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void conectar() {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle("Escolha a Impressora:");
        b.setItems(getNameDispositivosPareados(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initPrintController(bluetoothDevices.get(i));
                conectado = true;
                ((Button) findViewById(R.id.btnConectar)).setText("Desconectar");
                dialogInterface.dismiss();
            }
        });
        b.show();
    }

    private void desconectar() {
        try {
            conectado = false;
            printController.stop();
            ((Button) findViewById(R.id.btnConectar)).setText("Conectar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initPrintController(BluetoothDevice bluetoothDevice) {
        try {
            printController = new PrintController(managerBluetooth.getSocket(bluetoothDevice));
            conectado = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getNameDispositivosPareados() {
        String[] finalUnidades = new String[bluetoothDevices.size()];
        for (int i = 0; i < bluetoothDevices.size(); i++) {
            finalUnidades[i] = bluetoothDevices.get(i).getName();
        }
        return finalUnidades;
    }

    private void initBluetooth() {
        try {
            managerBluetooth = new ManagerBluetooth(this);
            managerBluetooth.ativaBluetooth();
            bluetoothDevices = managerBluetooth.getDispositivosPareados();
        } catch (PrinterBluetoothException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
