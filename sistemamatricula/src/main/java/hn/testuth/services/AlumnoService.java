package hn.testuth.services;

import hn.testuth.data.Alumno;
import hn.testuth.data.AlumnoRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AlumnoService {

    private final AlumnoRepository repository;

    public AlumnoService(AlumnoRepository repository) {
        this.repository = repository;
    }

    public Optional<Alumno> get(Long id) {
        return repository.findById(id);
    }

    public Alumno update(Alumno entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Alumno> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Alumno> list(Pageable pageable, Specification<Alumno> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
