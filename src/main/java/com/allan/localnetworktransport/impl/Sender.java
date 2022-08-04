package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.HelloApplication;
import com.allan.localnetworktransport.arch.IConnect;
import com.allan.localnetworktransport.arch.IInfoCallback;
import com.allan.localnetworktransport.arch.ISender;
import com.allan.localnetworktransport.bean.NamedAddr;
import com.allan.localnetworktransport.util.ThreadCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender implements ISender, IConnect {
    private ServerSocket serverSocket;  //套接字
    private static final AtomicInteger sPORT = new AtomicInteger((int) (12000 + Math.random() * 100));
    private static final int sPortDelta = (int) (Math.random() * 15 + 1);

    private NamedAddr namedAddr;
    private IInfoCallback mCallback;

    @Override
    public void init() {
        sPORT.addAndGet(sPortDelta);

        HelloApplication.sClosedProp.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue) {
                destroy();
            }
        });
    }

    @Override
    public void destroy() {
        ServerSocket s = serverSocket;
        if (s != null && !s.isClosed()) {
            try {
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        serverSocket = null;
    }

    private ServerSocket prepareSocket(int retryCount) {
        try {
            serverSocket = new ServerSocket(sPORT.get());//创建socket
            serverSocket.setSoTimeout(120*1000); //2分钟超时
            return serverSocket;
        } catch (IOException e) {
            if (retryCount == 2) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            return prepareSocket(retryCount+1);
        }
    }

    private static NamedAddr getLocal(InetAddress localHost) throws UnknownHostException {
        boolean hasPassInto = localHost != null;
        localHost = hasPassInto ? localHost : InetAddress.getLocalHost();
        String hostAddress = localHost.getHostAddress();
        String hostName = localHost.getHostName();
        var d = new NamedAddr();
        d.ip = hostAddress;
        d.port = sPORT.get();
        d.name = hostName;
        return d;
    }

    @Override
    public NamedAddr prepare() {
        System.out.println("prepare() -- ");
        serverSocket = prepareSocket(0);

        var preparedLocalHost = serverSocket.getInetAddress();
        NamedAddr compared;
        try {
            compared = getLocal(null);
            System.out.println("prepare() -- compared: " + compared);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try {
            namedAddr = getLocal(preparedLocalHost);
            System.out.println("prepare() -- namedAddr: " + namedAddr);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        ThreadCreator.newThread(()->{
            while (true) {
                if (HelloApplication.sClosedProp.get()) {
                    break;
                }
                //4，监听客户端
                try {
                    mCallback.onInfo("开始等待客户端进入, " + compared);
                    Socket socket = serverSocket.accept();
                    //5，创建服务处理线程
                    SenderThread socketThread = new SenderThread(socket, () -> mSendFile, mCallback);
                    //6，启动线程
                    socketThread.start();
                } catch (IOException e) {
                    if (e instanceof SocketTimeoutException se) {
                        mCallback.onInfo("超时啦！没有客户端进来");
                    }
                    throw new RuntimeException(e);
                }
            }
        }).start();
        return compared;
    }

    @Override
    public NamedAddr getPreparedNamedAddr() {
        return namedAddr;
    }

    @Override
    public void setInfoCallback(IInfoCallback callback) {
        mCallback = callback;
    }

    private String mSendFile;
    @Override
    public void setFile(String sendFilePathFile) {
        mSendFile = sendFilePathFile;
    }
}
