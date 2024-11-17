package hn.testuth.data;

import jakarta.persistence.Entity;

@Entity
public class Asignatura extends AbstractEntity {

    private String nombre;
    private String catedratico;
    private String horario;
    private String modalidad;
    private Integer precio;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getCatedratico() {
        return catedratico;
    }
    public void setCatedratico(String catedratico) {
        this.catedratico = catedratico;
    }
    public String getHorario() {
        return horario;
    }
    public void setHorario(String horario) {
        this.horario = horario;
    }
    public String getModalidad() {
        return modalidad;
    }
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }
    public Integer getPrecio() {
        return precio;
    }
    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

}
