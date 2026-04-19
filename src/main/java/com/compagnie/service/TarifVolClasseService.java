package com.compagnie.service;

import com.compagnie.model.TarifVolClasse;
import com.compagnie.model.TypePassager;
import com.compagnie.model.Vol;
import com.compagnie.repository.TarifVolClasseRepository;
import com.compagnie.repository.TypePassagerRepository;
import com.compagnie.repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TarifVolClasseService {

    @Autowired
    private TarifVolClasseRepository tarifVolClasseRepository;

    @Autowired
    private VolRepository volRepository;

    @Autowired
    private TypePassagerRepository typePassagerRepository;

    public List<TarifVolClasse> getAllTarifs() {
        return tarifVolClasseRepository.findAll();
    }

    public Optional<TarifVolClasse> getTarifById(Long id) {
        return tarifVolClasseRepository.findById(id);
    }

    public TarifVolClasse addTarif(TarifVolClasse tarif) {
        if (tarif.getVol() == null || tarif.getVol().getIdVol() == null) {
            throw new IllegalArgumentException("Un vol doit etre selectionne");
        }
        if (tarif.getClasse() == null || tarif.getClasse().trim().isEmpty()) {
            throw new IllegalArgumentException("La classe est obligatoire");
        }
        if (tarif.getTypePassager() == null || tarif.getTypePassager().getIdTypePassager() == null) {
            throw new IllegalArgumentException("Le type passager est obligatoire");
        }
        if (tarif.getPrixUnitaire() == null || tarif.getPrixUnitaire().signum() < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit etre positif");
        }

        Optional<Vol> volOpt = volRepository.findById(tarif.getVol().getIdVol());
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }

        Optional<TypePassager> typeOpt = typePassagerRepository.findById(tarif.getTypePassager().getIdTypePassager());
        if (typeOpt.isEmpty()) {
            throw new IllegalArgumentException("Type passager introuvable");
        }

        if (tarifVolClasseRepository.findByVolIdVolAndClasseIgnoreCaseAndTypePassagerIdTypePassager(
                volOpt.get().getIdVol(),
                tarif.getClasse().trim(),
                typeOpt.get().getIdTypePassager()
        ).isPresent()) {
            throw new IllegalArgumentException("Un tarif existe deja pour ce vol et cette classe");
        }

        tarif.setVol(volOpt.get());
        tarif.setClasse(tarif.getClasse().trim());
        tarif.setTypePassager(typeOpt.get());
        return tarifVolClasseRepository.save(tarif);
    }

    public TarifVolClasse updateTarif(Long id, TarifVolClasse tarif) {
        Optional<TarifVolClasse> existing = tarifVolClasseRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Tarif introuvable");
        }

        TarifVolClasse toUpdate = existing.get();

        if (tarif.getVol() == null || tarif.getVol().getIdVol() == null) {
            throw new IllegalArgumentException("Un vol doit etre selectionne");
        }
        if (tarif.getClasse() == null || tarif.getClasse().trim().isEmpty()) {
            throw new IllegalArgumentException("La classe est obligatoire");
        }
        if (tarif.getTypePassager() == null || tarif.getTypePassager().getIdTypePassager() == null) {
            throw new IllegalArgumentException("Le type passager est obligatoire");
        }
        if (tarif.getPrixUnitaire() == null || tarif.getPrixUnitaire().signum() < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit etre positif");
        }

        Optional<Vol> volOpt = volRepository.findById(tarif.getVol().getIdVol());
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }

        Optional<TypePassager> typeOpt = typePassagerRepository.findById(tarif.getTypePassager().getIdTypePassager());
        if (typeOpt.isEmpty()) {
            throw new IllegalArgumentException("Type passager introuvable");
        }

        String classe = tarif.getClasse().trim();
        Long idVol = volOpt.get().getIdVol();

        Integer idTypePassager = typeOpt.get().getIdTypePassager();

        Optional<TarifVolClasse> autre = tarifVolClasseRepository.findByVolIdVolAndClasseIgnoreCaseAndTypePassagerIdTypePassager(idVol, classe, idTypePassager);
        if (autre.isPresent() && !autre.get().getIdTarifVolClasse().equals(id)) {
            throw new IllegalArgumentException("Un tarif existe deja pour ce vol et cette classe");
        }

        toUpdate.setVol(volOpt.get());
        toUpdate.setClasse(classe);
        toUpdate.setTypePassager(typeOpt.get());
        toUpdate.setPrixUnitaire(tarif.getPrixUnitaire());

        return tarifVolClasseRepository.save(toUpdate);
    }

    public void deleteTarif(Long id) {
        if (!tarifVolClasseRepository.existsById(id)) {
            throw new IllegalArgumentException("Tarif introuvable");
        }
        tarifVolClasseRepository.deleteById(id);
    }
}
