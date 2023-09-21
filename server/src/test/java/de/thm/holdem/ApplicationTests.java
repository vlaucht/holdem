package de.thm.holdem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
class ApplicationTests {

	private final ApplicationContext applicationContext;

	ApplicationTests(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	void contextLoads() {
	}

	@Test
	void Should_ConnectToDatabase() throws SQLException {
		DataSource ds = applicationContext.getBean(DataSource.class);

		assertNotNull(ds);
		assertNotNull(ds.getConnection());
	}

}
