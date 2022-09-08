package com.dst.smd207api.Interfaces.Services;

import com.dst.smd207api.Models.Response;

import java.time.Instant;
import java.util.List;

public interface DeviceRawDataService
{
    Response<?> devicesGetPos(final List<Long> imeis, final String mode, final Instant fromTime, final Instant toTime);
    Response<?> devicesGetTrack(final Long imei, final String mode, final Instant fromTime, final Instant toTime);
}
