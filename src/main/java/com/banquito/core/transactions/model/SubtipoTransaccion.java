package com.banquito.core.transactions.model;

import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "SUBTIPO_TRANSACCION", uniqueConstraints = @UniqueConstraint(name = "UQ_SUBTIPO_TRANSACCION_CODIGO", columnNames = "CODIGO"))
public class SubtipoTransaccion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "CODIGO", nullable = false, length = 30)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MOVIMIENTO_BASE", length = 15)
    private TipoMovimientoEnum tipoMovimientoBase;

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

    public SubtipoTransaccion() {
    }

    public SubtipoTransaccion(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubtipoTransaccion that = (SubtipoTransaccion) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SubtipoTransaccion{" + "id=" + id + ", codigo=" + codigo + ", estado=" + estado + '}';
    }

}
