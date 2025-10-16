Project-specific development guidelines

Audience: Experienced Java/Spring developers working on this repo. This document captures the non-obvious build, configuration, and testing details unique to this project so you can get productive quickly.

Build and runtime configuration
- Toolchain
  - JDK: The project targets Java 25 (pom.xml: <java.version>25</java.version>). Install a JDK 25 distribution and set JAVA_HOME accordingly before using the Maven wrapper.
  - Build: Maven Wrapper is provided (mvnw.cmd on Windows). Prefer the wrapper over a local Maven install to ensure plugin compatibility.
- Building
  - Clean build: .\mvnw.cmd -q -DskipTests clean package
  - With tests: .\mvnw.cmd -q test
  - Run the app: .\mvnw.cmd spring-boot:run
- Spring Boot version and starters
  - Spring Boot 3.5.6.
  - Starters: web, thymeleaf, data-jpa. MySQL driver is declared with runtime scope.
- Database/autoconfiguration considerations
  - Because mysql-connector-j is on the classpath and spring-boot-starter-data-jpa is included, Spring will attempt JDBC auto-configuration if a DataSource is detected/configured. If you don’t provide spring.datasource.* properties, starting a full ApplicationContext (including @SpringBootTest) can fail with "Failed to determine a suitable driver class" or "Cannot determine embedded database driver".
  - Options during local dev if you don’t want a DB yet:
    - Provide a dev DB via application.properties or application-local.properties and activate the profile.
    - Or temporarily disable JDBC auto-config when running or testing without DB using: spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

Testing
- Frameworks
  - JUnit Jupiter via spring-boot-starter-test.
- Running tests
  - CLI: .\mvnw.cmd -q test
  - Specific test class: .\mvnw.cmd -q -Dtest=FullyQualifiedClassName test
  - Specific test method: .\mvnw.cmd -q -Dtest=FullyQualifiedClassName#methodName test
- Writing tests that don’t require a DB
  - Prefer focused tests that don’t spin the entire context when not necessary.
    - Pure unit tests: No Spring annotations. New classes under src\test\java with plain @Test methods.
    - Web slice: @WebMvcTest(YourController.class) with MockMvc for controller tests. This avoids DataSource unless your controller wires repositories directly (prefer services and mock them instead).
  - If you need @SpringBootTest but don’t have a DB configured, disable JDBC auto-config only for tests:
    - Option A: Inline property on the test class:
      @SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
      class FeedbackAppApplicationTests { ... }
    - Option B: Use a test profile and set src\test\resources\application-test.properties with:
      spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      Then annotate tests with @ActiveProfiles("test").
  - Repository tests (@DataJpaTest): add an embedded DB for tests (recommended). Add to pom.xml test scope:
      <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
      </dependency>
    Spring Boot will auto-configure H2 for @DataJpaTest.
- Example minimal tests
  - Pure unit test (no Spring):
      package com.feedback;
      import org.junit.jupiter.api.Test;
      import static org.junit.jupiter.api.Assertions.*;
      class SanityTest {
        @Test void addsNumbers() { assertEquals(4, 2 + 2); }
      }
  - Full-context test without DB (uses auto-config exclude):
      package com.feedback;
      import org.junit.jupiter.api.Test;
      import org.springframework.boot.test.context.SpringBootTest;
      @SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
      class FeedbackAppApplicationTests {
        @Test void contextLoads() {}
      }
- Notes about current repository tests
  - The default FeedbackAppApplicationTests uses @SpringBootTest and will attempt to start the full context. Without a configured DataSource, add the property exclude above or activate a test profile to avoid DB requirements.

Code style and project conventions
- Java and Spring style
  - Use constructor injection for components; avoid field injection.
  - Keep controllers thin; delegate domain logic to @Service classes.
  - DTOs vs Entities: Don’t expose JPA entities directly to the web layer.
- Package structure (suggested going forward)
  - com.feedback
    - config – Spring Boot configuration and profiles
    - controller – Web controllers
    - service – Business services
    - repository – Spring Data repositories
    - domain/model – Entities and domain models
    - dto – API DTOs
    - support – Utilities, mappers, etc.
- Configuration management
  - Place environment-specific overrides under src\main\resources as application-<profile>.properties and activate via SPRING_PROFILES_ACTIVE or --spring.profiles.active.
- Data bootstrap
  - A data.json is present at repo root (from the original Frontend Mentor challenge). If you decide to seed data, prefer using an ApplicationRunner reading from classpath (src\main\resources) rather than relying on a file path at runtime. Consider moving data.json under resources and loading via ObjectMapper.

Local development tips
- Fast feedback without DB
  - While building UI or non-persistence logic, run with the JDBC auto-config excluded to avoid coupling dev flow to MySQL availability.
- Database
  - When you are ready to wire persistence, add spring.datasource.url, username, password in application-local.properties, and add spring.jpa.hibernate.ddl-auto=update (or use migrations via Flyway/Liquibase).
- Thymeleaf
  - Caching is enabled by default in prod; during dev, disable to see template changes live: spring.thymeleaf.cache=false.

Verified commands summary (what we expect to work once JDK is installed)
- Setup: Install JDK 25; set JAVA_HOME; then run .\mvnw.cmd -q -DskipTests clean package
- Run unit tests only: .\mvnw.cmd -q -Dtest=SanityTest test
- Run all tests with DB auto-config excluded (for @SpringBootTest classes): .\mvnw.cmd -q -Dspring-boot.run.jvmArguments="-Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration" test

Housekeeping
- Do not commit design assets not needed for the build. The repository already includes .gitattributes; keep .gitignore entries from the starter to avoid leaking design files.
- Keep this document up to date when changing build plugins, Java version, or testing approach.