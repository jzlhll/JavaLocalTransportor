package com.allan.localnetworktransport;

public interface IReceiver {
    boolean connect(String ip, int port);
    void sendSay(String info);
}
