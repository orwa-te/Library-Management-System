package com.task.library.service;

import com.task.library.entity.Patron;
import com.task.library.exception.ResourceNotFoundException;
import com.task.library.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatronService {

    @Autowired
    private PatronRepository patronRepository;

    @Cacheable("patrons")
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    @Cacheable(value = "patrons", key = "#id")
    public Patron getPatronById(Long id) {
        return patronRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id " + id));
    }

    @Transactional
    @CacheEvict(value = "patrons", allEntries = true)
    public Patron createPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    @Transactional
    @CacheEvict(value = "patrons", allEntries = true)
    public Patron updatePatron(Long id, Patron patronDetails) {
        Patron patron = getPatronById(id);

        patron.setName(patronDetails.getName());
        patron.setContactInformation(patronDetails.getContactInformation());

        return patronRepository.save(patron);
    }

    @Transactional
    @CacheEvict(value = "patrons", allEntries = true)
    public void deletePatron(Long id) {
        Patron patron = getPatronById(id);
        patronRepository.delete(patron);
    }
}
