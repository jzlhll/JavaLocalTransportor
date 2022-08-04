package com.allan.localnetworktransport.arch;

public interface IReceiver {
    void connect(String ip, int port, String helloWords);
    void receiveFile(String saveFilePath);
}
