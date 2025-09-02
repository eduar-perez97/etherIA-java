package com.periferia.etheria.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        mockClient = mock(SecretsManagerClient.class);

        SecretsManagerClientBuilder mockBuilder = mock(SecretsManagerClientBuilder.class);
        when(mockBuilder.region(any(Region.class))).thenReturn(mockBuilder);
        when(mockBuilder.credentialsProvider(any(DefaultCredentialsProvider.class))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockClient);

        clientStaticMock.when(SecretsManagerClient::builder).thenReturn(mockBuilder);
    }

    @Test
    void testGetSecret_Success() throws Exception {
        String secretJson = new ObjectMapper().createObjectNode()
                .put("JWT_SECRET", "fakeSecret123")
                .toString();

        GetSecretValueResponse mockResponse = GetSecretValueResponse.builder()
                .secretString(secretJson)
                .build();

        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(mockResponse);

        String result = ReadSecret.getSecret("JWT_SECRET");

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

        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(mockResponse);

        String result = ReadSecret.getSecret("JWT_SECRET");

        assertNull(result);
    }

    @Test
    void testGetSecret_Exception() {
        when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenThrow(new RuntimeException("AWS error"));

        String result = ReadSecret.getSecret("JWT_SECRET");

        assertNull(result);
    }
}
