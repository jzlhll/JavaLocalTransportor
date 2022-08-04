package com.allan.localnetworktransport.arch;

import com.allan.localnetworktransport.bean.NamedAddr;

public interface ISender {
    NamedAddr prepare(String file);

    NamedAddr getPreparedNamedAddr();

    void setInfoCallback(IInfoCallback callback);
}
