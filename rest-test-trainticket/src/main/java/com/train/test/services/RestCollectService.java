package com.train.test.services;

public interface RestCollectService {

    void getCpuMemoryLogInReceiver(long requestTime);
    void getResourceData();
    String stopCollectResourceData();
}
