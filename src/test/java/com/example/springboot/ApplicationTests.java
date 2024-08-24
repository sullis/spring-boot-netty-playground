package com.example.springboot;

import io.github.nettyplus.leakdetector.junit.NettyLeakDetectorExtension;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.OpenSsl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import nl.altindag.log.LogCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(NettyLeakDetectorExtension.class)
class ApplicationTests {
	private static LogCaptor logCaptor;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ApplicationContext context;

	@BeforeAll
	public static void setupLogCaptor() {
		logCaptor = LogCaptor.forName("");
	}

	@BeforeEach
	public void clearLogsBeforeEach() {
		logCaptor.clearLogs();
	}

	@AfterEach
	public void clearLogsAfterEach() {
		logCaptor.clearLogs();
	}

	@AfterAll
	public static void tearDown() {
		logCaptor.close();
	}

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
		assertThat(logCaptor.getInfoLogs()).contains("foobar");
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
