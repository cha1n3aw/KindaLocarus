package AnalOcarusApp.Interfaces.Services.API;

import AnalOcarusApp.Models.API.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**TODO implement user&machine interfaces*/
public interface DeviceService
{
    ResponseEntity<Response<?>> getDevices(final List<String> imeies, final boolean returnAll, final boolean returnActive);

    /** TODO: update request, admin only (separated access levels needed) */
    /*
    ResponseEntity<Response<?>> deleteMachines(final List<Long> imeies);
    ResponseEntity<Response<?>> addMachines(final List<Machine> machines);
    ResponseEntity<Answer<?>> updateMachine(final Integer sn, final Reason reason, final Machine machine);
    */
}
