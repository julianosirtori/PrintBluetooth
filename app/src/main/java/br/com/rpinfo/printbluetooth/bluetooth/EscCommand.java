package br.com.rpinfo.printbluetooth.bluetooth;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Criado por Juliano Sirtori em 23/11/2017.
 */

public class EscCommand {

    private Vector<Byte> command = null;

    public EscCommand(Vector<Byte> command) {
        this.command = new Vector(4096, 1024);
    }

    public void addCODE128(String content) {
        byte[] command = new byte[]{29, 107, 73, (byte) content.length()};
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }

    private void addStrToCommand(String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var4) {
                var4.printStackTrace();
            }

            for (int i = 0; i < bs.length; ++i) {
                this.command.add(Byte.valueOf(bs[i]));
            }
        }

    }

    public void addRastBitImage(Bitmap bitmap, int nWidth, int nMode) {
        if(bitmap != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
            Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
            byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            byte[] command = new byte[8];
            height = src.length / width;
            command[0] = 29;
            command[1] = 118;
            command[2] = 48;
            command[3] = (byte)(nMode & 1);
            command[4] = (byte)(width / 8 % 256);
            command[5] = (byte)(width / 8 / 256);
            command[6] = (byte)(height % 256);
            command[7] = (byte)(height / 256);
            this.addArrayToCommand(command);
            byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);

            for(int k = 0; k < codecontent.length; ++k) {
                this.command.add(Byte.valueOf(codecontent[k]));
            }
        } else {
            Log.d("BMP", "bmp.  null ");
        }
    }

    public void addCutPaper() {
        byte[] command = new byte[]{29, 86, 1};
        this.addArrayToCommand(command);
    }

    private void addArrayToCommand(byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            this.command.add(Byte.valueOf(array[i]));
        }
    }

    private void addStrToCommand(String str, String charset) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            for (int i = 0; i < bs.length; ++i) {
                this.command.add(Byte.valueOf(bs[i]));
            }
        }

    }

    private void addStrToCommandUTF8Encoding(String str, int length) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            Log.d("EscCommand", "bs.length" + bs.length);
            if (length > bs.length) {
                length = bs.length;
            }

            Log.d("EscCommand", "length" + length);

            for (int i = 0; i < length; ++i) {
                this.command.add(Byte.valueOf(bs[i]));
            }
        }

    }

    private void addStrToCommand(String str, int length) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            Log.d("EscCommand", "bs.length" + bs.length);
            if (length > bs.length) {
                length = bs.length;
            }

            Log.d("EscCommand", "length" + length);

            for (int i = 0; i < length; ++i) {
                this.command.add(Byte.valueOf(bs[i]));
            }
        }

    }

}
