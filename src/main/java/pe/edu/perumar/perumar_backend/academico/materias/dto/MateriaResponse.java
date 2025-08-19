package pe.edu.perumar.perumar_backend.academico.materias.dto;

import java.time.Instant;

public class MateriaResponse {
  private String codigo;
  private String nombre;
  private String descripcion;
  private String estado;
  private Instant createdAt;
  private Instant updatedAt;

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
