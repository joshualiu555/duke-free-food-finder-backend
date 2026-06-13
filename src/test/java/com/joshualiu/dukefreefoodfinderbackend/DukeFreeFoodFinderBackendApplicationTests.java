package com.joshualiu.dukefreefoodfinderbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.mail.host=localhost",
        "spring.mail.port=25",
        // only for testing
        "jwt.secret=5pFaPwXnW8daLuHZOO9LGdoQxvHMGYcSNx09nZcENns",
        "jwt.expiration=86400000",
        "aws.s3.bucket=test-bucket",
        "aws.s3.region=us-east-1"

})
class DukeFreeFoodFinderBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}