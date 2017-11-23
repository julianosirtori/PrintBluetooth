package br.com.rpinfo.printbluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.rpinfo.printbluetooth.bluetooth.Exception.PrinterBluetoothException;

/**
 * Criado por Juliano Sirtori em 23/11/2017.
 */

public class ManagerBluetooth {

    public final static int CODE_ACTIVE_BLUETOOTH = 1;
    //uuid da impressora bluetooth
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;


    private Context context;
    private BluetoothAdapter btfAdapter;

    public ManagerBluetooth(Context context) throws PrinterBluetoothException {
        this.context = context;
        btfAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btfAdapter == null) {
            throw new PrinterBluetoothException("Dispositivo não possui BluetoothAdapter");
        }
    }

    /**
     * Verefica e ativa o bluetooth,
     */
    public boolean ativaBluetooth() {
        if (!btfAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            return false;
        }
        return true;
    }

    /**
     * Retorna Dispositivos pareados
     */
    public List<BluetoothDevice> getDispositivosPareados() {
        if (!btfAdapter.isEnabled()) {
            return new ArrayList<BluetoothDevice>(btfAdapter.getBondedDevices());
        }
        return null;
    }

    public BluetoothSocket getSocket(BluetoothDevice device) {
        BluetoothSocket socket = null;
        if (device != null) {
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }


}
