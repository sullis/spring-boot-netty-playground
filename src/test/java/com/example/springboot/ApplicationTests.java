package com.example.springboot;

import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.OpenSsl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ApplicationTests {

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
	void contextLoads() {
	}

}
