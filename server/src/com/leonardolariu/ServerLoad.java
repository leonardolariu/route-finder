package com.leonardolariu;

public class ServerLoad {
    public volatile int inProgress = 0;
    public volatile boolean closed = false;

    public ServerLoad() {
    }
}
