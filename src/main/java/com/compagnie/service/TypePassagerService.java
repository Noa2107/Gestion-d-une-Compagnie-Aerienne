package com.compagnie.service;

import com.compagnie.model.TypePassager;
import com.compagnie.repository.TypePassagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TypePassagerService {

    @Autowired
    private TypePassagerRepository typePassagerRepository;

    public List<TypePassager> getAllTypes() {
        return typePassagerRepository.findAll();
    }

    public Optional<TypePassager> getById(Integer id) {
        return typePassagerRepository.findById(id);
    }

    public Optional<TypePassager> getByNom(String nom) {
        return typePassagerRepository.findByNomTypeIgnoreCase(nom);
    }
}
