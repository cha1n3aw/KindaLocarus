package KindaLocarusApp.Interfaces.Services;

import KindaLocarusApp.Models.Response;

import java.time.Instant;
import java.util.List;

public interface DeviceRawDataService
{
    Response<?> devicesGetPos(final List<String> imeis, final Instant fromTime, final Instant toTime);
    Response<?> devicesGetTrack(final String imei, final Instant fromTime, final Instant toTime);
}
