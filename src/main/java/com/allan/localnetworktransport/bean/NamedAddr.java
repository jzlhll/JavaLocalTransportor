package com.allan.localnetworktransport.bean;

public final class NamedAddr {
    public String ip;
    public int port;
    public String name;

    @Override
    public String toString() {
        return "{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }

    public String simpleInfoString() {
        return name + ", ip= " + ip + ", port= " + port;
    }
}
