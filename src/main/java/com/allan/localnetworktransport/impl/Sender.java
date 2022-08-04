package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.IConnect;
import com.allan.localnetworktransport.ISender;
import com.allan.localnetworktransport.ISenderThreadPresenter;
import com.allan.localnetworktransport.NamedAddr;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender implements ISender, IConnect {
    private ServerSocket serverSocket;  //套接字
    private static final AtomicInteger sPORT = new AtomicInteger((int) (12000 + Math.random() * 100));
    private static final int sPortDelta = (int) (Math.random() * 15 + 1);

    private NamedAddr namedAddr;

    @Override
    public void init() {
        sPORT.addAndGet(sPortDelta);
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
            serverSocket.setSoTimeout(200);
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
    public NamedAddr prepare(String file) {
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

        new Thread(()->{
            while (true){
                //4，监听客户端
                try {
                    Socket socket = serverSocket.accept();
                    //5，创建服务处理线程
                    SenderThread socketThread = new SenderThread(socket, new ISenderThreadPresenter() {
                        @Override
                        public int onReceiveClientInfo(String clientReceiveInfo) {
                            if ("getFile".equals(clientReceiveInfo)) {
                                return ISenderThreadPresenter.FileSender;
                            }
                            return 0;
                        }

                        @Override
                        public String supplyFile() {
                            return file;
                        }
                    });
                    //6，启动线程
                    socketThread.start();
                } catch (IOException e) {
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
}
