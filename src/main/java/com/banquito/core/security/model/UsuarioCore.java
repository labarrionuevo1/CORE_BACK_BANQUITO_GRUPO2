package com.banquito.core.security.model;

import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
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
@Table(name = "USUARIO_CORE", uniqueConstraints = @UniqueConstraint(name = "UQ_USUARIO_CORE_USUARIO", columnNames = "USUARIO"))
public class UsuarioCore {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUCURSAL_ID")
    private Sucursal sucursal;

    @Column(name = "USUARIO", nullable = false, length = 50)
    private String usuario;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "NOMBRE_COMPLETO", nullable = false, length = 150)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROL", nullable = false, length = 50)
    private RolUsuarioCoreEnum rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoUsuarioCoreEnum estado = EstadoUsuarioCoreEnum.ACTIVO;

    @Column(name = "ULTIMO_LOGIN")
    private LocalDateTime ultimoLogin;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public UsuarioCore() {
    }

    public UsuarioCore(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioCore that = (UsuarioCore) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UsuarioCore{" + "id=" + id + ", usuario=" + usuario + ", estado=" + estado + '}';
    }

}
