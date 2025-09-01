package com.periferia.etheria;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.periferia.etheria.security.ReadSecret;
import org.junit.jupiter.api.*;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserHandlerFullTest {

    private static MockedStatic<ReadSecret> readSecretMock;
    private UserHandler handler;
    private ObjectMapper mapper;

    @BeforeAll
    static void mockSecrets() {
        // Simula que siempre hay un secreto en pruebas
        readSecretMock = Mockito.mockStatic(ReadSecret.class);
        readSecretMock.when(() -> ReadSecret.getSecret("JWT_SECRET"))
                .thenReturn("fakeSecretForTests");
    }

    @AfterAll
    static void closeMock() {
        readSecretMock.close();
    }

    @BeforeEach
    void setUp() {
        handler = new UserHandler(); // ya no lanza NullPointerException
        mapper = new ObjectMapper();
    }

    private APIGatewayProxyRequestEvent buildRequest(String action, Object data, String token) throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(mapper.writeValueAsString(Map.of("action", action, "data", data)));
        if (token != null) {
            request.setHeaders(Map.of("Authorization", token));
        }
        return request;
    }

    @Test
    void testRegister_MissingFields() throws Exception {
        var request = buildRequest("register", Map.of("email", "test@test.com"), null);
        var response = handler.handleRequest(request, (Context) null);

        assertEquals(400, response.getStatusCode());
        assertNotNull(response);
    }

    @Test
    void testLogin_MissingFields() throws Exception {
        var request = buildRequest("login", Map.of("email", "test@test.com"), null);
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
        assertNotNull(response);
    }

    @Test
    void testQueryAgent_MissingFields() throws Exception {
        var request = buildRequest("queryAgent", Map.of("question", "hola"), "Bearer token");
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetRecords_MissingFields() throws Exception {
        var request = buildRequest("getRecords", Map.of(), "Bearer token");
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testInstruction_MissingFields() throws Exception {
        var request = buildRequest("instructions/general", Map.of("name", "inst1"), "Bearer token");
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testDeleteHistory_MissingFields() throws Exception {
        var request = buildRequest("deleteHistory", Map.of(), "Bearer token");
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testUpdateTitle_MissingFields() throws Exception {
        var request = buildRequest("updateTitle", Map.of("id", "1"), "Bearer token");
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testUpdateUser_MissingFields() throws Exception {
        var request = buildRequest("updateUser", Map.of("cc", "123"), ""); // token vacío
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testInvalidAction() throws Exception {
        var request = buildRequest("not_exist", Map.of(), null);
        var response = handler.handleRequest(request, null);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Acción no válida"));
    }

    @Test
    void testJsonParseError() {
        // Forzamos body inválido para provocar JsonProcessingException
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody("{invalid-json}");

        var response = handler.handleRequest(request, null);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Error interno"));
    }
}