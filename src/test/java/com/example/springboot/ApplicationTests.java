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
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private ApplicationContext context;

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

	@Test
	void webServerIsNettyWebServer() {
		assertThat(context).isNotNull();
		ReactiveWebServerApplicationContext reactiveWebServerApplicationContext = (ReactiveWebServerApplicationContext) context;
		WebServer webServer = reactiveWebServerApplicationContext.getWebServer();
		assertThat(webServer).isNotNull();
		assertThat(webServer).isInstanceOf(NettyWebServer.class);
	}
}
