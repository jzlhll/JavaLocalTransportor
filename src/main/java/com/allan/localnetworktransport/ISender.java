package com.allan.localnetworktransport;

public interface ISender {
    NamedAddr prepare(String file);

    NamedAddr getPreparedNamedAddr();

}
