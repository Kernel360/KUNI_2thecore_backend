package com.example._thecore_back.hub.domain;

import java.util.List;

public interface GpsLogWriter {

    void saveAll(List<GpsLogEntity> logs);

}
