package ar.edu.utn.dds.k3003.messaging;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.dds.k3003.model.ReadModelOutbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TestTransaction;

@DataJpaTest(
    properties = {
      "donatrack.read-model.publisher.enabled=true",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@org.springframework.test.context.ActiveProfiles("outbox-test")
@ContextConfiguration(classes = OutboxTest.Config.class)
class OutboxTest {
  @Configuration
  @Profile("outbox-test")
  @EnableAutoConfiguration
  @EntityScan(basePackageClasses = ReadModelOutbox.class)
  @Import(ReadModelEventPublisher.class)
  static class Config {
    @Bean
    ObjectMapper mapper() {
      return new ObjectMapper().findAndRegisterModules();
    }
  }

  @Autowired EntityManager em;
  @Autowired ReadModelEventPublisher publisher;
  @Autowired ObjectMapper mapper;

  @Test
  void rollbackRemovesPendingEventsWithTheDomainTransaction() {
    publisher.publicar("donador", "d", "UPSERT", Map.of("nombre", "Ana"));
    assertEquals(
        1L, em.createQuery("select count(e) from ReadModelOutbox e", Long.class).getSingleResult());
    TestTransaction.flagForRollback();
    TestTransaction.end();
    TestTransaction.start();
    assertEquals(
        0L, em.createQuery("select count(e) from ReadModelOutbox e", Long.class).getSingleResult());
  }

  @Test
  void committedEventKeepsStableIdentityPayloadAndMonotonicVersion() throws Exception {
    publisher.publicar("donador", "d", "UPSERT", Map.of("nombre", "Ana"));
    publisher.publicar("donador", "d", "DELETE", Map.of());
    em.flush();
    var rows =
        em.createQuery("from ReadModelOutbox order by id", ReadModelOutbox.class).getResultList();
    var first = mapper.readTree(rows.get(0).content);
    var last = mapper.readTree(rows.get(1).content);
    assertTrue(last.path("version").asLong() > first.path("version").asLong());
    assertEquals(rows.get(0).eventId, first.path("eventId").asText());
    assertEquals(1, first.path("schemaVersion").asInt());
    assertEquals("Ana", first.path("payload").path("nombre").asText());
    assertFalse(rows.get(0).sent);
  }
}
