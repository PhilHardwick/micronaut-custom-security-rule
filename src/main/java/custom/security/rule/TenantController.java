package custom.security.rule;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;

import java.util.Map;

@Controller("/tenant")
public class TenantController {

    @Get("/{tenantId}")
    @RequiredPermission(resourceIdName = "tenantId", permission = "READ_ONLY")
    public HttpStatus index(@PathVariable String tenantId) {
        return HttpStatus.OK;
    }

    @Post("/{tenantId}/resource")
    @RequiredPermission(resourceIdName = "tenantId", permission = "WRITE")
    public HttpStatus createResource(@PathVariable String tenantId, @Body Map<String, Object> body) {
        return HttpStatus.OK;
    }
}
