package org.kevin.k8s_api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;

@SpringBootApplication
public class App {
	public static void main(String[] args) throws IOException, ApiException {
		SpringApplication.run(App.class, args);
	}
	
	@Value("${KUBERNETES_SERVICE_HOST}")
	private String host;
	@Value("${KUBERNETES_SERVICE_PORT}")
	private String port;
	@Value("${token}")
	private String token;
	
	@Bean
	public ApiClient getClient() {
		try {
			ApiClient client = new ApiClient();
			client.setBasePath("https://" + host + ":" + port);
			client.setApiKeyPrefix("bearer");
			client.setApiKey(token);
			client.setVerifyingSsl(false);
			Configuration.setDefaultApiClient(client);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("初始化ApiClient错误");
			System.exit(1);
			return null;
		}
	}
}
