package com.banquito.core.parameters.model;

import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "FERIADO")
public class Feriado {
    @Id
    @Column(name = "FECHA_FERIADO", nullable = false)
    private LocalDate fechaFeriado;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ES_FIN_SEMANA", nullable = false)
    private Boolean esFinSemana = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoCatalogoEnum estado = EstadoCatalogoEnum.ACTIVO;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public Feriado() {
    }

    public Feriado(LocalDate fechaFeriado) {
        this.fechaFeriado = fechaFeriado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feriado that = (Feriado) o;
        if (fechaFeriado == null || that.fechaFeriado == null) return false;
        return Objects.equals(fechaFeriado, that.fechaFeriado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fechaFeriado);
    }

    @Override
    public String toString() {
        return "Feriado{" + "fechaFeriado=" + fechaFeriado + ", estado=" + estado + '}';
    }

}
