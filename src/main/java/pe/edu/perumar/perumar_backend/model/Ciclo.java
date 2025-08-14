package pe.edu.perumar.perumar_backend.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ciclo {

    private String id; // uuid
    private String codigoCarrera;
    private String nombreCiclo; // autogenerado YYYY-<corr>-<codCarrera>
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<String> materias;
    private String promocion;
    private Map<String, String> ubicacion; // {departamento, provincia, distrito, capitania}
    private String estado;
    private Instant createdAt;
    private Instant updatedAt;

    public Ciclo() {
        // Constructor vacío requerido por frameworks
    }

    /** 
     * Método de fábrica para crear un Ciclo con valores por defecto.
     * Genera UUID, asigna estado ACTIVO y fechas de creación/actualización.
     */
    public static Ciclo nuevo(String codigoCarrera) {
        Ciclo c = new Ciclo();
        c.id = UUID.randomUUID().toString();
        c.codigoCarrera = codigoCarrera;
        c.estado = "ACTIVO";
        c.createdAt = Instant.now();
        c.updatedAt = Instant.now();
        return c;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigoCarrera() { return codigoCarrera; }
    public void setCodigoCarrera(String codigoCarrera) { this.codigoCarrera = codigoCarrera; }

    public String getNombreCiclo() { return nombreCiclo; }
    public void setNombreCiclo(String nombreCiclo) { this.nombreCiclo = nombreCiclo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public List<String> getMaterias() { return materias; }
    public void setMaterias(List<String> materias) { this.materias = materias; }

    public String getPromocion() { return promocion; }
    public void setPromocion(String promocion) { this.promocion = promocion; }

    public Map<String, String> getUbicacion() { return ubicacion; }
    public void setUbicacion(Map<String, String> ubicacion) { this.ubicacion = ubicacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
