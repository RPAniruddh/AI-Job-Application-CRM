package com.jobcrm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")

public class AppProperties {
	private final Jwt jwt = new Jwt();
	private final OpenAi openAi = new OpenAi();
	private Email email = new Email();

	public Jwt getJwt() {
		return jwt;
	}

	public OpenAi getOpenAi() {
		return openAi;
	}

	public Email getEmail() {
		return email;
	}
	
	public static class Email {
        private String from;
        private int dailySendLimit = 10;

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public int getDailySendLimit() { return dailySendLimit; }
        public void setDailySendLimit(int dailySendLimit) { this.dailySendLimit = dailySendLimit; }
    }

	public static class Jwt {
		private String secret;
		private long expirationMs;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public long getExpirationMs() {
			return expirationMs;
		}

		public void setExpirationMs(long expirationMs) {
			this.expirationMs = expirationMs;
		}
	}

	public static class OpenAi {
		private String apiKey;
		private String baseUrl;

		public String getApiKey() {
			return apiKey;
		}

		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}
	}
}
