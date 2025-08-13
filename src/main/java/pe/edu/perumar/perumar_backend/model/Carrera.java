package pe.edu.perumar.perumar_backend.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Carrera {

    private String codigo;                // PK
    private String nombre;
    private String descripcion;
    private ModalidadCarrera modalidad;   // enum
    private List<String> materias = new ArrayList<>();
    private String estado;                // ACTIVO | INACTIVO
    private Instant createdAt;
    private Instant updatedAt;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public ModalidadCarrera getModalidad() { return modalidad; }
    public void setModalidad(ModalidadCarrera modalidad) { this.modalidad = modalidad; }

    public List<String> getMaterias() { return materias; }
    public void setMaterias(List<String> materias) { this.materias = materias; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
