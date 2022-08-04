package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.arch.IInfoCallback;
import com.allan.localnetworktransport.bean.Consts;

import java.io.*;
import java.net.Socket;

public class SenderThread extends Thread {
    private final Socket socket;
    private final String sendFile;

    private InputStream inputStream;
    private DataInputStream dataInputStream;

    private boolean worked;

    private final IInfoCallback cb;
    //构造器
    public SenderThread(Socket socket, String sendFile, IInfoCallback cb) {
        this.socket = socket;
        this.cb = cb;
        this.sendFile = sendFile;
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
        System.out.println(Thread.currentThread().getName() + "开始。。。");
        //1, 打开输入流，准备读取数据
        while (worked) {
            String str = dataInputStream.readUTF();
            //对于服务端而言，我们先等待客户端进入消息。如果我们认可这个事情，则继续下去
            if (Consts.NET_COMING.equals(str)) {
                cb.onInfo("客户端返回" + str + "，链接成功啦！");
            } else if (Consts.NET_START_FILE_TRANSPORT.equals(str)) {
                if (sendFile == null || !new File(sendFile).exists()) {
                    cb.onInfo("服务端没有文件啦！请拉拽一个文件进来！");
                } else {
                    cb.onInfo("客户端要求发送文件...开始发送...");
                    //5，打开输出流，准备写入数据
                    OutputStream outputStream = socket.getOutputStream();
                    FileInputStream f = new FileInputStream(sendFile);
                    byte[] bytes = new byte[Consts.PAGE_SIZE];
                    while (worked) {
                        int len = f.read(bytes);
                        if (len > 0) {
                            outputStream.write(bytes, 0, len);
                        } else {
                            break;
                        }
                    }

                    outputStream.close();
                    f.close();

                    cb.onInfo("客户端要求发送文件...开始发送完成！");
                }
            }
        }

        //socket.shutdownOutput();
        //socket.shutdownInput();
        destroy();

        System.out.println(Thread.currentThread().getName() + "结束！");
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
