package com.example.mainserver.collector.domain;

import java.util.List;

public interface GpsLogWriter {

    void saveAll(List<GpsLogEntity> logs);

}
