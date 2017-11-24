package br.com.rpinfo.printbluetooth;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import br.com.rpinfo.auximpressorabluetooth.EscCommand;
import br.com.rpinfo.auximpressorabluetooth.Exception.PrinterBluetoothException;
import br.com.rpinfo.auximpressorabluetooth.ManagerBluetooth;
import br.com.rpinfo.auximpressorabluetooth.PrintController;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 2;
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

        ((Button) findViewById(R.id.btnCodeBar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EscCommand escCommand = new EscCommand();
                escCommand.addText("Print Code 128\n");
//                escCommand.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);//Defina o código de barras para reconhecer a posição do caractere abaixo do código de barra
                escCommand.addSetBarcodeHeight((byte) 60); //Defina a altura do código de barras para 60 pontos
                escCommand.addCODE128("12345678");  //Código de impressão 128 jardas - 8 caractereres tamanho maximo
                escCommand.addCODE39("1234567");  //Código de impressão 39 jardas - 7 caracteres tamanho maximo
                escCommand.addPrintAndLineFeed();
//                escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                try {
                    if (printController != null) {
                        printController.sendPrint(escCommand.getBytes());
                    } else {
                        Toast.makeText(MainActivity.this, "Conecte primeiro", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
                    if (printController != null) {
                        printController.sendPrint(escCommand.getBytes());
                    } else {
                        Toast.makeText(MainActivity.this, "Conecte primeiro", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        ((Button) findViewById(R.id.btnFoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        ((Button) findViewById(R.id.btnBitmapDefault)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EscCommand escCommand = new EscCommand();
                escCommand.addRastBitImage(BitmapFactory.decodeResource(getResources(),
                        R.drawable.etiqueta), 100, 1);

                try {
                    if (printController != null) {
                        printController.sendPrint(escCommand.getBytes());
                    } else {
                        Toast.makeText(MainActivity.this, "Conecte primeiro", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                EscCommand escCommand = new EscCommand();
                escCommand.addRastBitImage(imageBitmap, 100, 1);

                try {
                    if (printController != null) {
                        printController.sendPrint(escCommand.getBytes());
                    } else {
                        Toast.makeText(MainActivity.this, "Conecte primeiro", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == ManagerBluetooth.CODE_ACTIVE_BLUETOOTH) {
                initBluetooth();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            dispatchTakePictureIntent();
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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


}
