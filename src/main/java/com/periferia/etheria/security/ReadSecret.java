package com.periferia.etheria.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.periferia.etheria.constants.Constants;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class ReadSecret {
	private ReadSecret() {}

	public static String getSecret(String secretName) {
		try {
			Region region = Region.US_EAST_1;
			try (SecretsManagerClient client = SecretsManagerClient.builder()
					.region(region)
					.credentialsProvider(DefaultCredentialsProvider.builder().build())
					.build()) {

				GetSecretValueRequest request = GetSecretValueRequest.builder()
						.secretId(Constants.ARN_SECRET)
						.build();

				GetSecretValueResponse response = client.getSecretValue(request);
				String secretString = response.secretString();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(secretString);

				return jsonNode.get(secretName).asText();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
