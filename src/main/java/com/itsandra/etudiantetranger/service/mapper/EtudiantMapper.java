package com.itsandra.etudiantetranger.service.mapper;

import com.itsandra.etudiantetranger.domain.Etudiant;
import com.itsandra.etudiantetranger.service.dto.EtudiantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Etudiant} and its DTO {@link EtudiantDTO}.
 */
@Mapper(componentModel = "spring", uses = { NiveauMapper.class, FiliereMapper.class, PaysMapper.class })
public interface EtudiantMapper extends EntityMapper<EtudiantDTO, Etudiant> {
    @Mapping(target = "nomNiveau", source = "nomNiveau", qualifiedByName = "nomNiveau")
    @Mapping(target = "nomFiliere", source = "nomFiliere", qualifiedByName = "nomFiliere")
    @Mapping(target = "nomPays", source = "nomPays", qualifiedByName = "nomPays")
    EtudiantDTO toDto(Etudiant s);
}
