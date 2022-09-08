package com.dst.smd207api.Interfaces.Services;

import com.dst.smd207api.Models.Device;
import com.dst.smd207api.Models.Response;

import java.time.Instant;
import java.util.List;

public interface DeviceService
{
    Response<?> devicesGet(final List<Long> imeis, final List<String> fields);
    Response<?> devicesProlongLicense(final List<Long> imeis, final Instant issueDate, final Instant expirationDate);
    Response<?> devicesAdd(final List<Device> newDevices);
    Response<?> devicesEdit(final List<Device> updatedDevices);
    Response<?> devicesDelete(final List<Long> imeisToDelete);
}
