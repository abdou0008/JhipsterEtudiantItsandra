package com.itsandra.etudiantetranger.service.mapper;

import com.itsandra.etudiantetranger.domain.Pays;
import com.itsandra.etudiantetranger.service.dto.PaysDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pays} and its DTO {@link PaysDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PaysMapper extends EntityMapper<PaysDTO, Pays> {
    @Named("nomPays")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomPays", source = "nomPays")
    PaysDTO toDtoNomPays(Pays pays);
}
