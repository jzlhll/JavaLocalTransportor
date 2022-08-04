package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.ISenderThreadPresenter;

import java.io.*;
import java.net.Socket;

public class SenderThread extends Thread {
    private final Socket socket;
    private final ISenderThreadPresenter presenter;

    private InputStream inputStream;
    private DataInputStream dataInputStream;

    private boolean worked;

    //构造器
    public SenderThread(Socket socket, ISenderThreadPresenter p) {
        this.socket = socket;
        presenter = p;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataInputStream = new DataInputStream(inputStream);
    }

    private void destroy() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        if (dataInputStream != null) {
            dataInputStream.close();
            dataInputStream = null;
        }

        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }

    private void runInner() throws IOException {
        worked = true;
        //1, 打开输入流，准备读取数据

        while (worked) {
            String str = dataInputStream.readUTF();
            //对于服务端而言，我们先等待客户端进入消息。如果我们认可这个事情，则继续下去
            if (presenter.onReceiveClientInfo(str) == ISenderThreadPresenter.FileSender) {
                //5，打开输出流，准备写入数据
                OutputStream outputStream = socket.getOutputStream();
                FileInputStream f = new FileInputStream(presenter.supplyFile());
                byte[] bytes = new byte[2048];
                while (worked) {
                    int len = f.read(bytes);
                    outputStream.write(bytes, 0, len);
                }

                outputStream.close();
                f.close();
            }
        }

        //socket.shutdownOutput();
        //socket.shutdownInput();
        destroy();
    }

    @Override
    public void run() {
        try {
            runInner();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void abort() {
        worked = false;
    }
}
