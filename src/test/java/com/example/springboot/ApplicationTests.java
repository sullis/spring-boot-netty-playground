package com.example.springboot;

import io.github.nettyplus.leakdetector.junit.NettyLeakDetectorExtension;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.OpenSsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.reactor.netty.NettyWebServer;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(NettyLeakDetectorExtension.class)
class ApplicationTests {

	@LocalServerPort
	private int port;

	private WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		webTestClient = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();
	}

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
		webTestClient.get().uri("/")
				.exchange()
				.expectBody(String.class)
				.value(body -> assertThat(body).contains("Hello world"));
	}

	@Test
	void webServerIsNettyWebServer() {
		assertThat(context).isNotNull();
		var reactiveWebServerCtx = (ReactiveWebServerApplicationContext) context;
		WebServer webServer = reactiveWebServerCtx.getWebServer();
		assertThat(webServer).isNotNull();
		assertThat(webServer).isInstanceOf(NettyWebServer.class);
	}
}
