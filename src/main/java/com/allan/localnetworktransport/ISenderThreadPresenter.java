package com.allan.localnetworktransport;

public interface ISenderThreadPresenter {
    int FileSender = 0x101;

    int onReceiveClientInfo(String clientReceiveInfo);
    String supplyFile();
}
