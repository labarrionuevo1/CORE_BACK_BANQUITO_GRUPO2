package com.banquito.core.customers.model;

import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "CLIENTE", uniqueConstraints = @UniqueConstraint(name = "UQ_CLIENTE_IDENTIFICACION", columnNames = {"TIPO_IDENTIFICACION", "IDENTIFICACION"}))
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "SUBTIPO_CLIENTE_ID", nullable = false)
    private Integer subtipoClienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CLIENTE", nullable = false, length = 15)
    private TipoClienteEnum tipoCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SUBTIPO_CLIENTE_ID", referencedColumnName = "ID", insertable = false, updatable = false),
            @JoinColumn(name = "TIPO_CLIENTE", referencedColumnName = "TIPO_CLIENTE", insertable = false, updatable = false)
    })
    private SubtipoCliente subtipoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_IDENTIFICACION", nullable = false, length = 15)
    private TipoIdentificacionEnum tipoIdentificacion;

    @Column(name = "IDENTIFICACION", nullable = false, length = 20)
    private String identificacion;

    @Column(name = "NOMBRES", length = 100)
    private String nombres;

    @Column(name = "APELLIDOS", length = 100)
    private String apellidos;

    @Column(name = "RAZON_SOCIAL", length = 150)
    private String razonSocial;

    @Column(name = "FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @Column(name = "FECHA_CONSTITUCION")
    private LocalDate fechaConstitucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPRESENTANTE_LEGAL_ID")
    private Cliente representanteLegal;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO_MOVIL", nullable = false, length = 20)
    private String telefonoMovil;

    @Column(name = "DIRECCION", nullable = false, length = 255)
    private String direccion;

    @Column(name = "LATITUD", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "LONGITUD", precision = 11, scale = 8)
    private BigDecimal longitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoClienteEnum estado = EstadoClienteEnum.ACTIVO;

    @Column(name = "ACTIVO_PAGOS_MASIVOS", nullable = false)
    private Boolean activoPagosMasivos = false;

    @CreationTimestamp
    @Column(name = "FECHA_REGISTRO", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public Cliente() {
    }

    public Cliente(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente that = (Cliente) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" + "id=" + id + ", identificacion=" + identificacion + ", estado=" + estado + ", tipoCliente=" + tipoCliente + '}';
    }

}
