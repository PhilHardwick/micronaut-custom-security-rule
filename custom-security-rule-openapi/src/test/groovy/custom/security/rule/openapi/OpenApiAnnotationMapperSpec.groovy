package custom.security.rule.openapi

import io.micronaut.annotation.processing.test.AbstractTypeElementSpec
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.inject.BeanDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.extensions.Extension
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty

class OpenApiAnnotationMapperSpec extends AbstractTypeElementSpec {

    void "test RequiredPermission annotation is mapped to openapi annotations"() {
        when:
        BeanDefinition beanDefinition = buildBeanDefinition('test.HelloWorldController', '''
package test;
import io.micronaut.runtime.Micronaut;
import io.micronaut.http.annotation.*;
import io.micronaut.http.*;
import custom.security.rule.RequiredPermission;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}

interface HelloWorldApi {
 @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Get a message", description = "Returns a simple hello world.")
    @ApiResponse(responseCode = "200", description = "All good.")
    HttpResponse<String> helloWorld();
}
@Controller("/hello")
class HelloWorldController implements HelloWorldApi {
    @Override
    @RequiredPermission(resourceIdName = "tenantId", permission = "READ_ONLY")
    public HttpResponse<String> helloWorld() {
        return null;
    }
}
''')

        then:
        AnnotationValue<Operation> operation = beanDefinition.getRequiredMethod("helloWorld").getAnnotation(Operation.class)
        operation.stringValue("description").get() == "Returns a simple hello world."
        List<AnnotationValue<Extension>> extensions = operation.getAnnotations("extensions", Extension.class)
        extensions.size() == 1
        extensions.get(0).stringValue("name").get() == 'Security'
        def firstExtensionProperty = extensions.get(0).getAnnotations("properties", ExtensionProperty.class).get(0)
        firstExtensionProperty.stringValue("name").get() == 'AuthorisationDescription'
        firstExtensionProperty.stringValue("value").get() == "Your JWT needs to have READ_ONLY permissions for tenantId"
    }


}
