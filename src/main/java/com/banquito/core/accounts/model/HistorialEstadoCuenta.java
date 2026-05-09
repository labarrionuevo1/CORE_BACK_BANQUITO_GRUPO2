package com.banquito.core.accounts.model;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.security.model.UsuarioCore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "HISTORIAL_ESTADO_CUENTA")
public class HistorialEstadoCuenta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUENTA_ID", nullable = false)
    private Cuenta cuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ANTERIOR", length = 15)
    private EstadoCuentaEnum estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_NUEVO", nullable = false, length = 15)
    private EstadoCuentaEnum estadoNuevo;

    @Column(name = "MOTIVO_CAMBIO", nullable = false, length = 300)
    private String motivoCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_CORE_ID")
    private UsuarioCore usuarioCore;

    @CreationTimestamp
    @Column(name = "FECHA_CAMBIO", nullable = false, updatable = false)
    private LocalDateTime fechaCambio;

    public HistorialEstadoCuenta() {
    }

    public HistorialEstadoCuenta(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorialEstadoCuenta that = (HistorialEstadoCuenta) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HistorialEstadoCuenta{" + "id=" + id + '}';
    }

}
