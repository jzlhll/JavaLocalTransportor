package com.allan.localnetworktransport.impl;

import com.allan.localnetworktransport.IConnect;
import com.allan.localnetworktransport.IReceiver;

import java.io.IOException;
import java.net.Socket;

public class Receiver implements IReceiver, IConnect {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean connect(String ip, int port) {
        try {
            Socket client = new Socket(ip, port);
            System.out.println("client is opened");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //输出流        BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
        //写出数据        byte[] b = new byte[1024 * 8];
        int len;
        return false;
    }

    @Override
    public void sendSay(String info) {

    }
}
