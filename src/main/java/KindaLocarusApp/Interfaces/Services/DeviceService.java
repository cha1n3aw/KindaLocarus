package KindaLocarusApp.Interfaces.Services;

import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**TODO implement user&machine interfaces*/
public interface DeviceService
{
    Response<List<Device>> getDevices(final List<String> imeies, final Boolean returnAll, final Boolean returnActive);

    /** TODO: update request, admin only (separated access levels needed) */
    /*
    ResponseEntity<Response<?>> deleteMachines(final List<Long> imeies);
    ResponseEntity<Response<?>> addMachines(final List<Machine> machines);
    ResponseEntity<Answer<?>> updateMachine(final Integer sn, final Reason reason, final Machine machine);
    */
}
