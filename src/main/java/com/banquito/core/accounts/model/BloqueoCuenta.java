package com.banquito.core.accounts.model;

import com.banquito.core.accounts.enums.EstadoBloqueoCuentaEnum;
import com.banquito.core.security.model.UsuarioCore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "BLOQUEO_CUENTA")
public class BloqueoCuenta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUENTA_ID", nullable = false)
    private Cuenta cuenta;

    @Column(name = "MONTO_BLOQUEADO", nullable = false, precision = 19, scale = 4)
    private BigDecimal montoBloqueado;

    @Column(name = "MOTIVO", nullable = false, length = 100)
    private String motivo;

    @Column(name = "AUTORIDAD_ORDENANTE", length = 200)
    private String autoridadOrdenante;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoBloqueoCuentaEnum estado = EstadoBloqueoCuentaEnum.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_CORE_ID")
    private UsuarioCore usuarioCore;

    @CreationTimestamp
    @Column(name = "FECHA_BLOQUEO", nullable = false, updatable = false)
    private LocalDateTime fechaBloqueo;

    @Column(name = "FECHA_LIBERACION")
    private LocalDateTime fechaLiberacion;

    @Column(name = "OBSERVACIONES", length = 300)
    private String observaciones;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public BloqueoCuenta() {
    }

    public BloqueoCuenta(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloqueoCuenta that = (BloqueoCuenta) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BloqueoCuenta{" + "id=" + id + ", estado=" + estado + '}';
    }

}
