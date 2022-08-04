package com.allan.localnetworktransport.arch;

public interface IConnect {
    void init();
    void destroy();

    void setInfoCallback(IInfoCallback callback);
}
