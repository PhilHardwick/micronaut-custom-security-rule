package custom.security.rule;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

import java.util.Map;

@Controller("/tenant")
public class TenantController {

    @Get("/{tenantId}")
    @Operation(
            extensions = {
                    @Extension(name = "extensionName",
                    properties = {
                            @ExtensionProperty(name = "name", value = "value")
                    })
            }
    )
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
