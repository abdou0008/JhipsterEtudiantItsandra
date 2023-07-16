package com.itsandra.etudiantetranger.service.mapper;

import com.itsandra.etudiantetranger.domain.Filiere;
import com.itsandra.etudiantetranger.service.dto.FiliereDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Filiere} and its DTO {@link FiliereDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FiliereMapper extends EntityMapper<FiliereDTO, Filiere> {
    @Named("nomFiliere")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomFiliere", source = "nomFiliere")
    FiliereDTO toDtoNomFiliere(Filiere filiere);
}
