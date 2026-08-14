package ar.edu.utn.dds.k3003.exceptions;

/**
 * Fallo al comunicarse con otro componente. Se distingue de un error del cliente: si Logistica
 * esta caida o Render la tiene dormida, la culpa no es de quien nos llamo.
 */
public class IntegracionException extends RuntimeException {
  private final String componente;

  public IntegracionException(String componente, String mensaje, Throwable causa) {
    super(mensaje, causa);
    this.componente = componente;
  }

  public String getComponente() {
    return componente;
  }
}
