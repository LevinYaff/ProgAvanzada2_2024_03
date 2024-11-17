package hn.testuth.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long>, JpaSpecificationExecutor<Asignatura> {

}
