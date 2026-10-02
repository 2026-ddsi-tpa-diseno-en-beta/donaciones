package ar.edu.utn.dds.k3003.observability;
import org.junit.jupiter.api.Test;
import org.slf4j.*;
import static org.junit.jupiter.api.Assertions.*;

class DomainEventsTest {
  @Test void scopedDomainFieldsNeverLeakToTheNextRequest() {
    MDC.put("traceId", "previous"); MDC.put("event", "outer");
    try {
      DomainEvents.info(LoggerFactory.getLogger(getClass()), "donacion.registrada donacion={} cantidad={}", "id", 10);
      assertEquals("previous", MDC.get("traceId"));
      assertEquals("outer", MDC.get("event"));
      assertNull(MDC.get("donacionId"));
      assertNull(MDC.get("quantity"));
    } finally { MDC.clear(); }
  }
}
