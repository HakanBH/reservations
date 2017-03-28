package admin.feignClient;

import admin.model.FacilityFeign;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by Trayan_Muchev on 10/3/2016.
 */
@FeignClient("search-and-register-service")
public interface SearchAndRegisterClient {

    @RequestMapping(method = RequestMethod.GET, value = "/facilities")
    List<FacilityFeign> all(@RequestHeader("Authorization") String token);
}
