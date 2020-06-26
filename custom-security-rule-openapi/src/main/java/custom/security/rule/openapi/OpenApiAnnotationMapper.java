package custom.security.rule.openapi;

import custom.security.rule.RequiredPermission;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.annotation.TypedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

import java.util.List;

public class OpenApiAnnotationMapper implements TypedAnnotationMapper<RequiredPermission> {
    @Override
    public Class<RequiredPermission> annotationType() {
        return RequiredPermission.class;
    }

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<RequiredPermission> annotation, VisitorContext visitorContext) {
        String resourceIdName = annotation.stringValue("resourceIdName").orElse(null);
        String permission = annotation.stringValue("permission").orElse(null);

        AnnotationValue<ExtensionProperty> extensionProp = AnnotationValue.builder(ExtensionProperty.class)
                .member("name", "AuthorisationDescription")
                .member("value", "Your JWT needs to have " + permission + " permissions for " + resourceIdName)
                .build();

        AnnotationValue<Extension> extension = AnnotationValue.builder(Extension.class)
                .member("name", "Security")
                .member("properties", new AnnotationValue[]{extensionProp})
                .build();

        AnnotationValue<Operation> operation = AnnotationValue.builder(Operation.class)
                .member("extensions", new AnnotationValue[]{extension})
                .build();
        return List.of(operation);
    }
}
