package com.banquito.core.customers.model;

import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.shared.enums.EstadoCatalogoEnum;
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
@Table(name = "SUBTIPO_CLIENTE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_SUBTIPO_CLIENTE_CODIGO", columnNames = "CODIGO"),
                @UniqueConstraint(name = "UQ_SUBTIPO_CLIENTE_NOMBRE", columnNames = "NOMBRE"),
                @UniqueConstraint(name = "UQ_SUBTIPO_CLIENTE_ID_TIPO", columnNames = {"ID", "TIPO_CLIENTE"})
        })
public class SubtipoCliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CLIENTE", nullable = false, length = 15)
    private TipoClienteEnum tipoCliente;

    @Column(name = "CODIGO", nullable = false, length = 20)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoCatalogoEnum estado = EstadoCatalogoEnum.ACTIVO;

    @Column(name = "OBSERVACIONES", length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public SubtipoCliente() {
    }

    public SubtipoCliente(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubtipoCliente that = (SubtipoCliente) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SubtipoCliente{" + "id=" + id + ", codigo=" + codigo + ", estado=" + estado + ", tipoCliente=" + tipoCliente + '}';
    }

}
