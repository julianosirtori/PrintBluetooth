package br.com.rpinfo.auximpressorabluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.rpinfo.auximpressorabluetooth.Exception.PrinterBluetoothException;

/**
 * Criado por Juliano Sirtori em 23/11/2017.
 */

public class ManagerBluetooth {

    public final static int CODE_ACTIVE_BLUETOOTH = 1;
    //uuid da impressora bluetooth
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(enableIntent, ManagerBluetooth.CODE_ACTIVE_BLUETOOTH);
            } else {
                context.startActivity(enableIntent);
            }

            return false;
        }
        return true;
    }

    /**
     * Retorna Dispositivos pareados
     */
    public List<BluetoothDevice> getDispositivosPareados() {
        if (btfAdapter.isEnabled()) {
            return new ArrayList<BluetoothDevice>(btfAdapter.getBondedDevices());
        }
        return null;
    }

    public BluetoothSocket getSocket(BluetoothDevice device) {
        BluetoothSocket socket = null;
        if (device != null) {
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }


}
