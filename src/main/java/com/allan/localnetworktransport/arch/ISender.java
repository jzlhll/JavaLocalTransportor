package com.allan.localnetworktransport.arch;

import com.allan.localnetworktransport.bean.NamedAddr;

public interface ISender {
    NamedAddr prepare();

    NamedAddr getPreparedNamedAddr();

    void setInfoCallback(IInfoCallback callback);
    void setFile(String sendFilePathFile);
}
