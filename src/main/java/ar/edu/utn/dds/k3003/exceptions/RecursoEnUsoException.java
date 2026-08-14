package ar.edu.utn.dds.k3003.exceptions;

/** No se puede dar de baja algo que esta siendo referenciado por otra entidad. */
public class RecursoEnUsoException extends RuntimeException {
  public RecursoEnUsoException(String mensaje) {
    super(mensaje);
  }
}
