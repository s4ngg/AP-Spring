package co.kr.allpick;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:allpick-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.data.redis.host=localhost",
		"jwt.secret=allpick-secret-key-must-be-32-characters-long",
		"jwt.expiration-time=86400000",
		"spring.cloud.aws.credentials.access-key=test",
		"spring.cloud.aws.credentials.secret-key=test",
		"cloud.aws.s3.image-bucket=test",
		"coolsms.api-key=test",
		"coolsms.api-secret=test",
		"coolsms.sender=010-0000-0000",
		"business.api.key=test"
})
class AllPickApplicationTests {

	@Test
	void contextLoads() {
	}

}
