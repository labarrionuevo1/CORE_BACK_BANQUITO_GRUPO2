package com.banquito.core.institutional.model;

import com.banquito.core.institutional.enums.EstadoCuentaInstitucionalEnum;
import com.banquito.core.institutional.enums.TipoCuentaInstitucionalEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "CUENTA_INSTITUCIONAL", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_CUENTA_INST_NUMERO", columnNames = "NUMERO_CUENTA"),
        @UniqueConstraint(name = "UQ_CUENTA_INST_CODIGO", columnNames = "CODIGO")
})
public class CuentaInstitucional {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NUMERO_CUENTA", nullable = false, length = 20)
    private String numeroCuenta;

    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CUENTA", nullable = false, length = 20)
    private TipoCuentaInstitucionalEnum tipoCuenta;

    @Column(name = "SALDO_CONTABLE", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoContable = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoCuentaInstitucionalEnum estado = EstadoCuentaInstitucionalEnum.ACTIVA;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public CuentaInstitucional() {
    }

    public CuentaInstitucional(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuentaInstitucional that = (CuentaInstitucional) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CuentaInstitucional{" + "id=" + id + ", codigo=" + codigo + ", numeroCuenta=" + numeroCuenta + ", estado=" + estado + '}';
    }

}
