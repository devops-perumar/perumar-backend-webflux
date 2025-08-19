package pe.edu.perumar.perumar_backend.academico.materias.dto;

import jakarta.validation.constraints.*;

public class MateriaUpdateRequest {
  @NotBlank(message = "nombre requerido")
  @Size(min = 3, max = 80, message = "nombre 3–80 caracteres")
  private String nombre;

  @Size(max = 300, message = "descripcion hasta 300 caracteres")
  private String descripcion;

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
