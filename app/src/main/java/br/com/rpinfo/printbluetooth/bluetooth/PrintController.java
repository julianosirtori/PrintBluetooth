package br.com.rpinfo.printbluetooth.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Criado por Juliano Sirtori em 23/11/2017.
 */

public class PrintController {

    public BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;

    private boolean running;

    public PrintController(BluetoothSocket socket) throws IOException {
        this.socket = socket;
        this.socket.connect();

        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
        this.running = true;

    }

    public boolean sendPrint(byte[] bytes) throws IOException {
        if (out != null) {
            out.write(bytes);
            return true;
        }
        return false;
    }

    public void stop() throws IOException {
        running = false;
        if (socket != null) {
            socket.close();
        }
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
    }
}
