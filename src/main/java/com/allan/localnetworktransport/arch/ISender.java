package com.allan.localnetworktransport.arch;

import com.allan.localnetworktransport.bean.NamedAddr;

public interface ISender {
    void prepare(String sendFile);

    NamedAddr getPreparedNamedAddr();
}
