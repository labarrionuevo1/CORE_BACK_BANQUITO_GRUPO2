package com.banquito.core.transactions.model;

import com.banquito.core.institutional.model.CuentaInstitucional;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.transactions.enums.EstadoTransaccionEnum;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TRANSACCION_INSTITUCIONAL", uniqueConstraints = @UniqueConstraint(name = "UQ_TX_INST_IDEMPOTENCIA", columnNames = {"CUENTA_INSTITUCIONAL_ID", "UUID_TRANSACCION", "FECHA_NEGOCIO"}))
public class TransaccionInstitucional {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUENTA_INSTITUCIONAL_ID", nullable = false)
    private CuentaInstitucional cuentaInstitucional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBTIPO_TRANSACCION_ID", nullable = false)
    private SubtipoTransaccion subtipoTransaccion;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "UUID_TRANSACCION", nullable = false, columnDefinition = "CHAR(36)")
    private UUID uuidTransaccion;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "UUID_GRUPO_OPERACION", columnDefinition = "CHAR(36)")
    private UUID uuidGrupoOperacion;

    @Column(name = "FECHA_NEGOCIO", nullable = false)
    private LocalDate fechaNegocio;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MOVIMIENTO", nullable = false, length = 15)
    private TipoMovimientoEnum tipoMovimiento;

    @Column(name = "MONTO", nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(name = "SALDO_RESULTANTE", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoResultante;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoTransaccionEnum estado = EstadoTransaccionEnum.COMPLETADA;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL_ORIGEN", nullable = false, length = 20)
    private CanalOrigenEnum canalOrigen = CanalOrigenEnum.CORE;

    @Column(name = "REFERENCIA_EXTERNA", length = 100)
    private String referenciaExterna;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_CORE_ID")
    private UsuarioCore usuarioCore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREDENCIAL_WEB_ID")
    private CredencialWeb credencialWeb;

    @CreationTimestamp
    @Column(name = "FECHA_TRANSACCION", nullable = false, updatable = false)
    private LocalDateTime fechaTransaccion;

    public TransaccionInstitucional() {
    }

    public TransaccionInstitucional(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionInstitucional that = (TransaccionInstitucional) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransaccionInstitucional{" + "id=" + id + ", estado=" + estado + ", tipoMovimiento=" + tipoMovimiento + ", canalOrigen=" + canalOrigen + '}';
    }
}