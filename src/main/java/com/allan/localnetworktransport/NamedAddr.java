package com.allan.localnetworktransport;

public final class NamedAddr {
    public String ip;
    public int port;
    public String name;

    @Override
    public String toString() {
        return "NamedAddr{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }
}
