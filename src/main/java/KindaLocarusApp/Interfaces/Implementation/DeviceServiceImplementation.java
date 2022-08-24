package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeviceServiceImplementation implements DeviceService
{
    @Autowired
    public DeviceServiceImplementation()
    {

    }

    public ResponseEntity<Response<?>> getDevices(final List<String> imeies, final boolean returnAll, final boolean returnActive)
    {
        Response<List<Device>> successAns = new Response<>();
        successAns.setResponseStatus(Integer.valueOf(HttpStatus.OK.value()));
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