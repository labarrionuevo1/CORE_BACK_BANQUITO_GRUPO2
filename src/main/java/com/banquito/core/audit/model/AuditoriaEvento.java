package com.banquito.core.audit.model;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "AUDITORIA_EVENTO")
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_CORE_ID")
    private UsuarioCore usuarioCore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREDENCIAL_WEB_ID")
    private CredencialWeb credencialWeb;

    @Column(name = "MODULO", nullable = false, length = 50)
    private String modulo;

    @Column(name = "ACCION", nullable = false, length = 80)
    private String accion;

    @Column(name = "ENTIDAD", nullable = false, length = 80)
    private String entidad;

    @Column(name = "ENTIDAD_ID", length = 80)
    private String entidadId;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESULTADO", nullable = false, length = 20)
    private ResultadoAuditoriaEnum resultado;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL_ORIGEN", nullable = false, length = 20)
    private CanalOrigenEnum canalOrigen;

    @Column(name = "IP_ORIGEN", length = 45)
    private String ipOrigen;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "DETALLE_JSON", columnDefinition = "LONGTEXT")
    private String detalleJson;

    @CreationTimestamp
    @Column(name = "FECHA_EVENTO", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    public AuditoriaEvento() {
    }

    public AuditoriaEvento(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditoriaEvento that = (AuditoriaEvento) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AuditoriaEvento{" +
                "id=" + id +
                ", modulo='" + modulo + '\'' +
                ", accion='" + accion + '\'' +
                ", entidad='" + entidad + '\'' +
                ", resultado=" + resultado +
                ", canalOrigen=" + canalOrigen +
                '}';
    }
}