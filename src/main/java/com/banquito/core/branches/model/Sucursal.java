package com.banquito.core.branches.model;

import com.banquito.core.branches.enums.EstadoSucursalEnum;
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
@Table(name = "SUCURSAL", uniqueConstraints = @UniqueConstraint(name = "UQ_SUCURSAL_CODIGO", columnNames = "CODIGO_SUCURSAL"))
public class Sucursal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "CODIGO_SUCURSAL", nullable = false, length = 10)
    private String codigoSucursal;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "CIUDAD", nullable = false, length = 80)
    private String ciudad;

    @Column(name = "DIRECCION", length = 300)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoSucursalEnum estado = EstadoSucursalEnum.ACTIVA;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public Sucursal() {
    }

    public Sucursal(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sucursal that = (Sucursal) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sucursal{" + "id=" + id + ", estado=" + estado + '}';
    }

}
