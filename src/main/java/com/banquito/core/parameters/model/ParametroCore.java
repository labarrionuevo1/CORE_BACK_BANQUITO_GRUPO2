package com.banquito.core.parameters.model;

import com.banquito.core.parameters.enums.TipoDatoParametroEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "PARAMETRO_CORE")
public class ParametroCore {
    @Id
    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "VALOR_TEXTO", nullable = false, length = 255)
    private String valorTexto;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DATO", nullable = false, length = 20)
    private TipoDatoParametroEnum tipoDato;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @UpdateTimestamp
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public ParametroCore() {
    }

    public ParametroCore(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametroCore that = (ParametroCore) o;
        if (codigo == null || that.codigo == null) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "ParametroCore{" + "codigo=" + codigo + '}';
    }

}
