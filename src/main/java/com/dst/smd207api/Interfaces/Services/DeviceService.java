package com.dst.smd207api.Interfaces.Services;

import com.dst.smd207api.Models.Device;
import com.dst.smd207api.Models.Response;

import java.util.List;

public interface DeviceService
{
    Response<?> devicesGet(final List<String> imeis, final List<String> fields);
    Response<?> devicesAdd(final List<Device> newDevices);
    Response<?> devicesEdit(final List<Device> updatedDevices);
    Response<?> devicesDelete(final List<String> imeisToDelete);
}
