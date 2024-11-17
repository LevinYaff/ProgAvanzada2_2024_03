package hn.testuth.services;

import hn.testuth.data.Asignatura;
import hn.testuth.data.AsignaturaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AsignaturaService {

    private final AsignaturaRepository repository;

    public AsignaturaService(AsignaturaRepository repository) {
        this.repository = repository;
    }

    public Optional<Asignatura> get(Long id) {
        return repository.findById(id);
    }

    public Asignatura update(Asignatura entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Asignatura> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Asignatura> list(Pageable pageable, Specification<Asignatura> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
