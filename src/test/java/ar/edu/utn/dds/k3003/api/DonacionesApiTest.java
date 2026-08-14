package ar.edu.utn.dds.k3003.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.app.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Verifica el contrato HTTP: que los endpoints no modificables sigan en su ruta y, sobre todo, que
 * cada tipo de error devuelva el codigo correcto. Antes toda excepcion terminaba en un 400, con lo
 * cual una caida de otro componente se le reportaba al cliente como culpa suya.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DonacionesApiTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private Fachada fachada;
  @Autowired private ObjectMapper objectMapper;

  private String categoriaId;
  private String subcategoriaId;
  private String productoId;

  @BeforeEach
  void setUp() throws Exception {
    fachada.limpiarDatos();

    categoriaId = idDe(post("/categorias"), """
        {"nombre":"Alimentos","descripcion":"comida"}""");
    subcategoriaId =
        idDe(
            post("/categorias"),
            """
            {"nombre":"Fideos","descripcion":"pastas","categoriaPadreID":"%s"}"""
                .formatted(categoriaId));
    productoId =
        idDe(
            post("/productos"),
            """
            {"nombre":"Fideos","descripcion":"medio kilo","categoriaID":"%s","subcategoriaID":"%s"}"""
                .formatted(categoriaId, subcategoriaId));
  }

  private String idDe(
      org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder,
      String body)
      throws Exception {
    MvcResult resultado =
        mockMvc
            .perform(builder.contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andReturn();
    return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asText();
  }

  private String donacion(int cantidad) {
    return """
        {"donadorID":"donador1","depositoID":"dep1","descripcion":"una donacion",
         "productoID":"%s","cantidad":%d}"""
        .formatted(productoId, cantidad);
  }

  // ------------------------------------------- endpoints no modificables

  @Test
  @DisplayName("Los tres endpoints no modificables de donaciones responden en su ruta exacta")
  void endpointsNoModificables() throws Exception {
    MvcResult creada =
        mockMvc
            .perform(post("/donaciones").contentType(MediaType.APPLICATION_JSON).content(donacion(5)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estado").value("INGRESADA"))
            .andReturn();

    String id = objectMapper.readTree(creada.getResponse().getContentAsString()).get("id").asText();

    mockMvc.perform(get("/donaciones")).andExpect(status().isOk());
    mockMvc
        .perform(get("/donaciones/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.cantidad").value(5));
  }

  // ------------------------------------------------- codigos de error

  @Test
  @DisplayName("Un recurso inexistente devuelve 404, no 500 ni 400")
  void recursoInexistenteEs404() throws Exception {
    mockMvc
        .perform(get("/donaciones/{id}", "no-existe"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"));

    mockMvc.perform(get("/productos/{id}", "no-existe")).andExpect(status().isNotFound());
    mockMvc.perform(get("/categorias/{id}", "no-existe")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Una cantidad invalida devuelve 400")
  void cantidadInvalidaEs400() throws Exception {
    mockMvc
        .perform(post("/donaciones").contentType(MediaType.APPLICATION_JSON).content(donacion(0)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  @DisplayName("Dar de baja algo en uso devuelve 409")
  void bajaDeRecursoEnUsoEs409() throws Exception {
    mockMvc
        .perform(post("/donaciones").contentType(MediaType.APPLICATION_JSON).content(donacion(5)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(delete("/productos/{id}", productoId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("RECURSO_EN_USO"));

    mockMvc
        .perform(delete("/categorias/{id}", categoriaId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("RECURSO_EN_USO"));
  }

  // --------------------------------------------------- funcionalidad nueva

  @Test
  @DisplayName("El historial de estados se expone por HTTP")
  void historialPorHttp() throws Exception {
    MvcResult creada =
        mockMvc
            .perform(post("/donaciones").contentType(MediaType.APPLICATION_JSON).content(donacion(5)))
            .andReturn();
    String id = objectMapper.readTree(creada.getResponse().getContentAsString()).get("id").asText();

    mockMvc.perform(patch("/donaciones/{id}/estado", id).param("estado", "ACEPTADA"));

    mockMvc
        .perform(get("/donaciones/{id}/historial", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].estado").value("INGRESADA"))
        .andExpect(jsonPath("$[1].estado").value("ACEPTADA"))
        .andExpect(jsonPath("$[1].fechaCambio").exists());
  }

  @Test
  @DisplayName("Una transicion de estado invalida se rechaza con 400")
  void transicionInvalida() throws Exception {
    MvcResult creada =
        mockMvc
            .perform(post("/donaciones").contentType(MediaType.APPLICATION_JSON).content(donacion(5)))
            .andReturn();
    String id = objectMapper.readTree(creada.getResponse().getContentAsString()).get("id").asText();

    // INGRESADA -> CONQUEJA no esta permitido: hay que pasar por ACEPTADA.
    mockMvc
        .perform(patch("/donaciones/{id}/estado", id).param("estado", "CONQUEJA"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("CONQUEJA solo se alcanza por el endpoint que registra la queja")
  void conQuejaRequiereRegistrarLaQueja() throws Exception {
    MvcResult creada =
        mockMvc
            .perform(post("/donaciones").contentType(MediaType.APPLICATION_JSON).content(donacion(5)))
            .andReturn();
    String id = objectMapper.readTree(creada.getResponse().getContentAsString()).get("id").asText();

    mockMvc
        .perform(patch("/donaciones/{id}/estado", id).param("estado", "ACEPTADA"))
        .andExpect(status().isOk());
    mockMvc
        .perform(patch("/donaciones/{id}/estado", id).param("estado", "CONQUEJA"))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(
            post("/donaciones/{id}/quejas", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcion\":\"producto defectuoso\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("CONQUEJA"));
  }

  @Test
  @DisplayName("Los endpoints de alta no aceptan IDs definidos por el cliente")
  void altaRechazaIdDelCliente() throws Exception {
    mockMvc
        .perform(
            post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"id":"99","nombre":"Arroz","descripcion":"un kilo de arroz",
                     "categoriaID":"%s","subcategoriaID":"%s"}"""
                        .formatted(categoriaId, subcategoriaId)))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"id":"99","nombre":"Muebles","descripcion":"mobiliario"}"""))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            post("/identificadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"id":"99","tipo":"QR","descripcion":"codigo interno"}"""))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            post("/donaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(donacion(5).replaceFirst("\\{", "{\"id\":\"99\",")))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("La busqueda consolidada devuelve donaciones en orden cronologico")
  void busquedaConsolidadaYOrdenada() throws Exception {
    String primero = idDe(post("/donaciones"), donacion(2));
    String segundo = idDe(post("/donaciones"), donacion(3));

    mockMvc
        .perform(
            get("/donaciones")
                .param("donadorID", "donador1")
                .param("fecha", LocalDate.now().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(primero))
        .andExpect(jsonPath("$[1].id").value(segundo));
  }

  @Test
  @DisplayName("Las rutas redundantes de busqueda y queja ya no forman parte del contrato")
  void rutasRedundantesEliminadas() throws Exception {
    mockMvc.perform(get("/donaciones/search")).andExpect(status().isNotFound());
    mockMvc
        .perform(
            post("/donaciones/1/queja")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcion\":\"queja\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Se puede crear un producto sin identificador y la subcategoria viaja en la respuesta")
  void productoSinIdentificador() throws Exception {
    mockMvc
        .perform(get("/productos/{id}", productoId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.identificadorID").doesNotExist())
        .andExpect(jsonPath("$.subcategoriaID").value(subcategoriaId));
  }

  @Test
  @DisplayName("Una categoria hoja puede clasificar directamente un producto")
  void productoEnCategoriaSinSubcategorias() throws Exception {
    String categoriaHoja =
        idDe(post("/categorias"), "{\"nombre\":\"Mobiliario\",\"descripcion\":\"muebles\"}");

    mockMvc
        .perform(
            post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nombre":"Mesa","descripcion":"mesa de comedor","categoriaID":"%s"}"""
                        .formatted(categoriaHoja)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.categoriaID").value(categoriaHoja))
        .andExpect(jsonPath("$.subcategoriaID").doesNotExist());
  }

  @Test
  @DisplayName("Una categoria con subcategorias exige seleccionar una")
  void productoEnCategoriaPadreRequiereSubcategoria() throws Exception {
    mockMvc
        .perform(
            post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nombre":"Arroz","descripcion":"un kilo de arroz","categoriaID":"%s"}"""
                        .formatted(categoriaId)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("El listado conserva la subcategoria en la representacion del producto")
  void listadoDeProductosEsConsistente() throws Exception {
    mockMvc
        .perform(get("/productos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].subcategoriaID").value(subcategoriaId));
  }

  @Test
  @DisplayName("Las subcategorias de una categoria se pueden listar")
  void listarSubcategorias() throws Exception {
    mockMvc
        .perform(get("/categorias/{id}/subcategorias", categoriaId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].esSubcategoria").value(true))
        .andExpect(jsonPath("$[0].categoriaPadreID").value(categoriaId));
  }

  @Test
  @DisplayName("Modificar una categoria no acepta cambios de jerarquia implicitos")
  void modificarCategoriaNoMueveJerarquia() throws Exception {
    mockMvc
        .perform(
            put("/categorias/{id}", categoriaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nombre":"Alimentos","descripcion":"comida","categoriaPadreID":"99"}"""))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Swagger muestra IDs generados, codigos reales y solo las rutas canonicas")
  void swaggerReflejaElContrato() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.components.schemas.DonacionDTO.properties.id.readOnly").value(true))
        .andExpect(
            jsonPath("$.components.schemas.DonacionDTO.properties.estado.readOnly").value(true))
        .andExpect(jsonPath("$.components.schemas.ProductoRequest.properties.id").doesNotExist())
        .andExpect(jsonPath("$.paths['/productos'].post.responses['201']").exists())
        .andExpect(jsonPath("$.paths['/donaciones/search']").doesNotExist())
        .andExpect(jsonPath("$.paths['/donaciones/{id}/queja']").doesNotExist());
  }

  @Test
  @DisplayName("El ABM de productos responde a PUT y DELETE")
  void abmDeProductos() throws Exception {
    mockMvc
        .perform(
            put("/productos/{id}", productoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nombre":"Fideos integrales","descripcion":"medio kilo integral",
                     "categoriaID":"%s","subcategoriaID":"%s"}"""
                        .formatted(categoriaId, subcategoriaId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Fideos integrales"));

    mockMvc.perform(delete("/productos/{id}", productoId)).andExpect(status().isNoContent());
    mockMvc.perform(get("/productos/{id}", productoId)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("El endpoint de estado informa los conteos y el modo de cada integracion")
  void estadoDelModulo() throws Exception {
    mockMvc
        .perform(get("/admin/estado"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productos").value(1))
        .andExpect(jsonPath("$.categorias").value(2))
        .andExpect(jsonPath("$.integraciones.donadoresYEntidades").exists())
        .andExpect(jsonPath("$.integracionesReales").exists());
  }
}
