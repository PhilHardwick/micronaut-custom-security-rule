package custom.security.rule;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class TenantControllerTest {

    @Inject
    EmbeddedServer embeddedServer;
    @Inject
    JwtTokenGenerator tokenGenerator;

    @Test
    public void testGettingWithCorrectResource() throws Exception {
        Optional<String> token = tokenGenerator.generateToken(Map.of("sub", "1", "https://your-domain.com/claims", Map.of("123", "READ_ONLY")));

        try(RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient.class, embeddedServer.getURL())) {
            assertEquals(HttpStatus.OK, client.toBlocking().exchange(HttpRequest.GET("/tenant/" + 123).bearerAuth(token.get())).status());
        }
    }

    @Test
    public void testGettingWithWrongPermission() throws Exception {
        Optional<String> token = tokenGenerator.generateToken(Map.of("sub", "1", "https://your-domain.com/claims", Map.of("123", "READ_ONLY")));

        try(RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient.class, embeddedServer.getURL())) {
            MutableHttpRequest<Map<String, String>> request = HttpRequest.POST("/tenant/" + 123 + "/resource", Map.of("name", "Phil"))
                    .bearerAuth(token.get());
            HttpClientResponseException httpClientResponseException = assertThrows(HttpClientResponseException.class, () -> {
                client.toBlocking().exchange(request);
            });
            assertEquals(HttpStatus.FORBIDDEN, httpClientResponseException.getStatus());
        }
    }

    @Test
    public void testGettingWithWrongResourceIdButRightPermission() throws Exception {
        Optional<String> token = tokenGenerator.generateToken(Map.of("sub", "1", "https://your-domain.com/claims", Map.of("234", "WRITE")));

        try(RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient.class, embeddedServer.getURL())) {
            MutableHttpRequest<Map<String, String>> request = HttpRequest.POST("/tenant/" + 123 + "/resource", Map.of("name", "Phil"))
                    .bearerAuth(token.get());
            HttpClientResponseException httpClientResponseException = assertThrows(HttpClientResponseException.class, () -> {
                client.toBlocking().exchange(request);
            });
            assertEquals(HttpStatus.FORBIDDEN, httpClientResponseException.getStatus());
        }
    }
}
