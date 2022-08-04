package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.HelloApplication;
import com.allan.localnetworktransport.arch.IConnect;
import com.allan.localnetworktransport.arch.IInfoCallback;
import com.allan.localnetworktransport.arch.IReceiver;
import com.allan.localnetworktransport.bean.Consts;
import com.allan.localnetworktransport.util.ThreadCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.*;
import java.net.*;

public class Receiver implements IReceiver, IConnect {
    private IInfoCallback callback;

    private String mReceiveFile;
    private Socket mClientSocket;
    private final Object waitForStartReceiverLock = new Object();

    @Override
    public void init() {
        HelloApplication.sClosedProp.addListener((observableValue, aBoolean, t1) -> {
            if (t1 != null && t1) {
                destroy();
            }
        });
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
    public void connect(String host, int port, String helloWords) {
        ThreadCreator.newThread(()->{
            try {
                System.out.println("接受线程开始！");
                byte[] bytes = new byte[Consts.PAGE_SIZE];
                int len = Integer.MAX_VALUE;
                Socket client = mClientSocket = new Socket();
                var address = host != null ? new InetSocketAddress(host, port) :
                        new InetSocketAddress(InetAddress.getByName(null), port);
                SocketAddress localAddr = null;
                try {
                    if (localAddr != null) //可以移除
                        client.bind(localAddr);
                    client.connect(address, 500);
                } catch (IOException | IllegalArgumentException | SecurityException e) {
                    try {
                        client.close();
                    } catch (IOException ce) {
                        e.addSuppressed(ce);
                    }
                    throw e;
                }

                System.out.println("client init！");
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                System.out.println("client init！dos");
                BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
                System.out.println("client init！bis");
                try {
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
                        System.out.println("read len " + len);
                        fos.write(bytes, 0, len);
                    }

                    fos.close();

                    System.out.println("all saved file!!!");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bis.close();
                dos.close();
                destroy();
            } catch (IOException e) {
                if (e instanceof NoRouteToHostException) {
                    callback.onInfo("IP或者port不对。重新填写");
                } else {
                    e.printStackTrace();
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
