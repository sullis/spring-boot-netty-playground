package com.example.springboot;

import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.OpenSsl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@EnabledOnOs(value = { OS.LINUX })
	void epollIsAvailableOnLinux() {
		Epoll.ensureAvailability();
	}

	@Test
	void boringSslIsAvailable() {
		OpenSsl.ensureAvailability();
		assertThat(OpenSsl.versionString()).isEqualTo("BoringSSL");
	}

	@Test
	void serverReturnsHelloWorld() {
		assertThat(restTemplate.getForObject("http://localhost:" + port + "/",
				String.class)).contains("Hello world");
	}

}
