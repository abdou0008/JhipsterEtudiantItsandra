package com.itsandra.etudiantetranger.service.mapper;

import com.itsandra.etudiantetranger.domain.Niveau;
import com.itsandra.etudiantetranger.service.dto.NiveauDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Niveau} and its DTO {@link NiveauDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface NiveauMapper extends EntityMapper<NiveauDTO, Niveau> {
    @Named("nomNiveau")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomNiveau", source = "nomNiveau")
    NiveauDTO toDtoNomNiveau(Niveau niveau);
}
