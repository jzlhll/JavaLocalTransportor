package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.arch.IConnect;
import com.allan.localnetworktransport.arch.IInfoCallback;
import com.allan.localnetworktransport.arch.IReceiver;
import com.allan.localnetworktransport.bean.Consts;
import com.allan.localnetworktransport.util.ThreadCreator;

import java.io.*;
import java.net.NoRouteToHostException;
import java.net.Socket;

public class Receiver implements IReceiver, IConnect {
    private IInfoCallback callback;

    private String mReceiveFile;
    private Socket mClientSocket;
    private final Object waitForStartReceiverLock = new Object();

    private boolean mIsThreadStarting = false;

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
    public void setInfoCallback(IInfoCallback callback) {
        this.callback = callback;
    }

    @Override
    public void connect(String ip, int port, String helloWords) {
        ThreadCreator.newThread(()->{
            try {
                System.out.println("接受线程开始！");
                byte[] bytes = new byte[Consts.PAGE_SIZE];
                int len = Integer.MAX_VALUE;
                try (Socket client = new Socket(ip, port);
                     DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                     BufferedInputStream bis = new BufferedInputStream(client.getInputStream())) {
                    mClientSocket = client;
                    System.out.println("client is connected!!!");
                    dos.writeUTF(helloWords);

                    synchronized (waitForStartReceiverLock) {
                        waitForStartReceiverLock.wait();
                    }

                    dos.writeUTF(Consts.NET_START_FILE_TRANSPORT);
                    dos.flush();
                    dos.close();

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
                if (e instanceof NoRouteToHostException) {
                    callback.onInfo("IP或者port不对。重新填写");
                } else {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("接受线程结束！");
        }).start();
    }

    @Override
    public void receiveFile(String saveFilePath) {
        mReceiveFile = saveFilePath;

        if (mClientSocket == null || mClientSocket.isClosed()) {
            callback.onInfo("尚未建立链接，请点击连接。");
            return;
        }

        if (mReceiveFile == null) {
            callback.onInfo("没有存储文件路径。");
            return;
        }

        synchronized (waitForStartReceiverLock) {
            waitForStartReceiverLock.notify();
        }
    }
}
