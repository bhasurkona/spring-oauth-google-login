package com.samsoft.oauth2.config;

import java.io.FileNotFoundException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@ComponentScan(basePackages = { "com.samsoft" })
@EnableAutoConfiguration
public class SpringBootWebApplication implements
		EmbeddedServletContainerCustomizer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebApplication.class, args);
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		if (container instanceof JettyEmbeddedServletContainerFactory) {
			customizeJetty((JettyEmbeddedServletContainerFactory) container);
		}
	}

	public void customizeJetty(
			JettyEmbeddedServletContainerFactory containerFactory) {
		containerFactory.addServerCustomizers(jettyServerCustomizer());
	}

	@Bean
	public JettyServerCustomizer jettyServerCustomizer() {
		return new JettyServerCustomizer() {

			@Override
			public void customize(Server server) {
				SslContextFactory sslContextFactory = new SslContextFactory();
				sslContextFactory.setKeyStorePassword("jetty6");
				try {
					sslContextFactory.setKeyStorePath(ResourceUtils.getFile(
							"classpath:jetty-ssl.keystore").getAbsolutePath());
				} catch (FileNotFoundException ex) {
					throw new IllegalStateException("Could not load keystore",
							ex);
				}
				SslSocketConnector sslConnector = new SslSocketConnector(
						sslContextFactory);
				sslConnector.setPort(443);
				sslConnector.setMaxIdleTime(60000);
				server.addConnector(sslConnector);
			}
		};
	}

	@Bean
	@ConditionalOnMissingBean(RequestContextListener.class)
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}
}
