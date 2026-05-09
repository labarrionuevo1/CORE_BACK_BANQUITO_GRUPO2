package com.banquito.core.security.model;

import com.banquito.core.customers.model.Cliente;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
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
@Table(name = "CREDENCIAL_WEB", uniqueConstraints = @UniqueConstraint(name = "UQ_CREDENCIAL_WEB_USUARIO", columnNames = "USUARIO"))
public class CredencialWeb {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "USUARIO", nullable = false, length = 50)
    private String usuario;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "ULTIMO_LOGIN")
    private LocalDateTime ultimoLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoCredencialWebEnum estado = EstadoCredencialWebEnum.ACTIVO;

    @CreationTimestamp
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public CredencialWeb() {
    }

    public CredencialWeb(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredencialWeb that = (CredencialWeb) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CredencialWeb{" + "id=" + id + ", usuario=" + usuario + ", estado=" + estado + '}';
    }

}
