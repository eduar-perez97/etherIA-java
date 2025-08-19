package com.periferia.etheria.security;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthEntraID {

	private final HttpClient client = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(5))
			.build();

	public UserDto authenticatorEntraID(String email) {

		try {
			String endpointEntraID = System.getenv("URL_ENTRAID");
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode rootNode = mapper.createObjectNode();
			ObjectNode userDtoNode = mapper.createObjectNode();
			userDtoNode.put("email", email);
			rootNode.set("UserDto", userDtoNode);

			String body = mapper.writeValueAsString(rootNode);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(endpointEntraID))
					.timeout(Duration.ofSeconds(180))
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200 || response.statusCode() == 404) {
				log.info("Respuesta recibida desde EntraID con código: {}", response.statusCode());
				JsonNode root = mapper.readTree(response.body());

				if (root.has("data") && !root.get("data").isNull()) {
					return mapper.treeToValue(root.get("data"), UserDto.class);
				} else {
					log.info("Usuario no encontrado en EntraID: {}", email);
					return null;
				}
			}
			else {
				log.error("Error no controlado desde EntraID con código: {}, body: {}", response.statusCode(), response.body());
				String detailError = mapper.readTree(response.body()).path("detail").asText(response.body());
				throw new UserException("Error con la petición a EntraID: " + detailError, response.statusCode(), response.body());
			}
		}
		catch (UserException e) {
			throw e;
		} catch (ConnectException e) {
			log.error("No se pudo conectar con EntraID: {}", e.getMessage());
			throw new UserException("No se pudo conectar con EntraID", 503, e.getMessage());
		} catch (IOException e) {
			log.error("Error de IO al comunicarse con EntraID: {}", e.getMessage());
			throw new UserException("Error de entrada/salida al comunicarse con EntraID", 500, e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new UserException("La operación fue interrumpida", 500, e.getMessage());
		} catch (Exception e) {
			log.error("Error inesperado: {}", e.getMessage());
			throw new UserException("Error inesperado", 500, e.getMessage());
		}
	}
}