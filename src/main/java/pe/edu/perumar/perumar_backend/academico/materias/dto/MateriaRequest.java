package pe.edu.perumar.perumar_backend.academico.materias.dto;

import jakarta.validation.constraints.*;

public class MateriaRequest {
  @NotBlank(message = "codigo requerido")
  @Pattern(regexp = "^[A-Za-z0-9_-]{3,12}$", message = "codigo alfanumérico 3–12 (guion y guion_bajo permitidos)")
  private String codigo;

  @NotBlank(message = "nombre requerido")
  @Size(min = 3, max = 80, message = "nombre 3–80 caracteres")
  private String nombre;

  @Size(max = 300, message = "descripcion hasta 300 caracteres")
  private String descripcion;

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
