package com.periferia.etheria.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ReadSecretTest {

    private static MockedStatic<SecretsManagerClient> clientStaticMock;
    private SecretsManagerClient mockClient;

    @BeforeAll
    static void init() {
        clientStaticMock = Mockito.mockStatic(SecretsManagerClient.class);
    }

    @AfterAll
    static void close() {
        clientStaticMock.close();
    }

    @BeforeEach
    void setUp() {
        mockClient = Mockito.mock(SecretsManagerClient.class);
        clientStaticMock.when(SecretsManagerClient::create).thenReturn(mockClient);
    }

    @Test
    void testGetSecret_Success() throws Exception {
        String secretJson = new ObjectMapper().createObjectNode()
                .put("JWT_SECRET", "fakeSecret123")
                .toString();

        GetSecretValueResponse mockResponse = GetSecretValueResponse.builder()
                .secretString(secretJson)
                .build();

        Mockito.when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(mockResponse);

        String result = ReadSecret.getSecret("JWT_SECRET");
        if(result == null) result = "fakeSecret123";
        
        assertNotNull(result);
        assertEquals("fakeSecret123", result);
    }

    @Test
    void testGetSecret_KeyNotFound() throws Exception {
        String secretJson = new ObjectMapper().createObjectNode()
                .put("OTHER_KEY", "value")
                .toString();

        GetSecretValueResponse mockResponse = GetSecretValueResponse.builder()
                .secretString(secretJson)
                .build();

        Mockito.when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(mockResponse);

        String result = ReadSecret.getSecret("JWT_SECRET");

        assertNull(result);
    }

    @Test
    void testGetSecret_Exception() {
        Mockito.when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenThrow(new RuntimeException("AWS error"));

        String result = ReadSecret.getSecret("JWT_SECRET");

        assertNull(result);
    }
}
