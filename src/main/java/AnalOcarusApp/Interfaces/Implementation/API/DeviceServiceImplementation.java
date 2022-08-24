package AnalOcarusApp.Interfaces.Implementation.API;

import AnalOcarusApp.Interfaces.Repositories.API.DevicesRepo;
import AnalOcarusApp.Interfaces.Services.API.DeviceService;
import AnalOcarusApp.Models.API.Device;
import AnalOcarusApp.Models.API.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

import static AnalOcarusApp.Constants.Constants.REQUEST_SUCCESS;

@Service
public class DeviceServiceImplementation implements DeviceService
{
    private final DevicesRepo deviceRepo;

    @Autowired
    public DeviceServiceImplementation(final DevicesRepo deviceRepo)
    {
        this.deviceRepo = deviceRepo;
    }

    public ResponseEntity<Response<?>> getDevices(final List<String> imeies, final boolean returnAll, final boolean returnActive)
    {
        Response<List<Device>> successAns = new Response<>();
        successAns.setResponseStatus(REQUEST_SUCCESS);
        if (returnAll)
        {

        }
        else
        {
            for (String imei : imeies)
            {
                //successAns.setResponseData(deviceRepo.findByImei(imeies, returnAll, returnActive));
            }
        }
        return new ResponseEntity<>(successAns, HttpStatus.OK);
    }
}