package custom.security.rule;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

@Singleton
public class PermissionSecurityRule implements SecurityRule {
    @Override
    public SecurityRuleResult check(HttpRequest request, @Nullable RouteMatch routeMatch, @Nullable Map<String, Object> claims) {
        if (routeMatch instanceof MethodBasedRouteMatch) {
            MethodBasedRouteMatch methodBasedRouteMatch = (MethodBasedRouteMatch) routeMatch;
            if (methodBasedRouteMatch.hasAnnotation(RequiredPermission.class)) {
                AnnotationValue<RequiredPermission> requiredPermissionAnnotation = methodBasedRouteMatch.getAnnotation(RequiredPermission.class);
                // Get parameters from annotation on method
                Optional<String> resourceIdName = requiredPermissionAnnotation.stringValue("resourceIdName");
                Optional<String> permission = requiredPermissionAnnotation.stringValue("permission");
                if (permission.isPresent() && resourceIdName.isPresent() && claims != null) {
                    // Use name of parameter to get the value passed in as an argument to the method
                    String resourceId = methodBasedRouteMatch.getVariableValues().get(resourceIdName.get()).toString();
                    // Get claim from jwt using the resource ID
                    Object permissionForResource = ((Map) claims.get("https://your-domain.com/claims")).get(resourceId);
                    if (permissionForResource != null && permissionForResource.equals(permission.get())) {
                        // if the permission exists and it's equal, allow access
                        return SecurityRuleResult.ALLOWED;
                    }
                }
            }
        }
        return SecurityRuleResult.UNKNOWN;
    }
}
