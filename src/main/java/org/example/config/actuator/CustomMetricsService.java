package org.example.config.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class CustomMetricsService {

    private final MeterRegistry meterRegistry;

    @Autowired
    public CustomMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordCustomMetric() {
        meterRegistry.counter("custom_metric_counter").increment();
    }
}
