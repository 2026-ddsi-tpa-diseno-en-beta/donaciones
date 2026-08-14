package ar.edu.utn.dds.k3003.model;

/**
 * Tipo de identificador de un producto, con la regla de validacion que le corresponde.
 *
 * <p>Es un enum propio y no el {@code TipoIdentificadorEnum} de la catedra: el modelo de dominio no
 * debe depender de los DTOs. La traduccion se hace en {@code IdentificadorMapper}.
 */
public enum TipoIdentificador {
  /** Valido si la descripcion del producto tiene 3 o mas palabras. */
  CODIGODEBARRAS {
    @Override
    public boolean esValidoPara(String nombreProducto, String descripcionProducto) {
      return cantidadDePalabras(descripcionProducto) >= 3;
    }
  },

  /** Valido si la cantidad de letras del nombre del producto es par. */
  QR {
    @Override
    public boolean esValidoPara(String nombreProducto, String descripcionProducto) {
      return cantidadDeLetras(nombreProducto) % 2 == 0;
    }
  };

  public abstract boolean esValidoPara(String nombreProducto, String descripcionProducto);

  protected static int cantidadDePalabras(String texto) {
    if (texto == null || texto.isBlank()) {
      return 0;
    }
    return texto.trim().split("\\s+").length;
  }

  /** Cuenta letras reales: ignora espacios, digitos y puntuacion, e incluye acentuadas. */
  protected static int cantidadDeLetras(String texto) {
    if (texto == null || texto.isBlank()) {
      return -1;
    }
    return (int) texto.chars().filter(Character::isLetter).count();
  }
}
