package KindaLocarusApp.Interfaces.Services;

import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;

import java.time.Instant;
import java.util.List;

public interface DeviceService
{
    Response<?> devicesGet(final List<String> imeis, final List<String> fields);
    Response<?> devicesAdd(final List<Device> newDevices);
    Response<?> devicesEdit(final List<Device> updatedDevices);
    Response<?> devicesDelete(final List<String> imeisToDelete);
}
