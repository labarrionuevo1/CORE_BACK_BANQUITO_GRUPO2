package com.banquito.core.accounts.model;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.customers.model.Cliente;
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
@Table(name = "CUENTA", uniqueConstraints = @UniqueConstraint(name = "UQ_CUENTA_NUMERO", columnNames = "NUMERO_CUENTA"))
public class Cuenta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NUMERO_CUENTA", nullable = false, length = 20)
    private String numeroCuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUCURSAL_ID", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBTIPO_CUENTA_ID", nullable = false)
    private SubtipoCuenta subtipoCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoCuentaEnum estado = EstadoCuentaEnum.ACTIVA;

    @Column(name = "SALDO_CONTABLE", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoContable = BigDecimal.ZERO;

    @Column(name = "SALDO_DISPONIBLE", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoDisponible = BigDecimal.ZERO;

    @Column(name = "PERMITE_SOBREGIRO", nullable = false)
    private Boolean permiteSobregiro = false;

    @Column(name = "LIMITE_SOBREGIRO", nullable = false, precision = 19, scale = 4)
    private BigDecimal limiteSobregiro = BigDecimal.ZERO;

    @Column(name = "ES_FAVORITA_PAGOS", nullable = false)
    private Boolean esFavoritaPagos = false;

    @CreationTimestamp
    @Column(name = "FECHA_APERTURA", nullable = false, updatable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "FECHA_ULTIMO_MOVIMIENTO")
    private LocalDateTime fechaUltimoMovimiento;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public Cuenta() {
    }

    public Cuenta(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta that = (Cuenta) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cuenta{" + "id=" + id + ", numeroCuenta=" + numeroCuenta + ", estado=" + estado + '}';
    }

}
