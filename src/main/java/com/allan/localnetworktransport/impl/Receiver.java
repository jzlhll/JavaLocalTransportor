package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.arch.IConnect;
import com.allan.localnetworktransport.arch.IReceiver;
import com.allan.localnetworktransport.bean.Consts;

import java.io.*;
import java.net.Socket;

public class Receiver implements IReceiver, IConnect {
    private String mReceiveFile;
    private Socket mClientSocket;
    private final Object waitForStartReceiverLock = new Object();

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        if (mClientSocket != null && !mClientSocket.isClosed()) {
            try {
                mClientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void connect(String ip, int port, String helloWords) {
        new Thread(()->{
            try {
                byte[] bytes = new byte[Consts.PAGE_SIZE];
                int len = Integer.MAX_VALUE;
                try (Socket client = new Socket(ip, port);
                     DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                     BufferedInputStream bis = new BufferedInputStream(client.getInputStream())) {
                    mClientSocket = client;
                    System.out.println("client is connected!!!");
                    dos.writeUTF(helloWords);
                    dos.close();

                    waitForStartReceiverLock.wait();

                    FileOutputStream fos = new FileOutputStream(mReceiveFile);
                    while (len > 0) {
                        len = bis.read(bytes);
                        fos.write(bytes, 0, len);
                    }

                    fos.close();

                    System.out.println("all saved file!!!");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void receiveFile(String saveFilePath) {
        mReceiveFile = saveFilePath;
        waitForStartReceiverLock.notify();
    }
}
