package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.DeviceRawDataService;
import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DeviceRawDataServiceImpl implements DeviceRawDataService
{
    public Response<?> devicesGetPos(final List<String> imeis, final Instant fromTime, final Instant toTime)
    {

        Response<Device> response = new Response<>();
        return response;
    }
    public Response<?> devicesGetTrack(final String imei, final Instant fromTime, final Instant toTime)
    {
        Response<Device> response = new Response<>();
        return response;
    }
}
