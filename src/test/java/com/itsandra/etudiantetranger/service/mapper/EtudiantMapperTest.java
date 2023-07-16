package com.itsandra.etudiantetranger.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EtudiantMapperTest {

    private EtudiantMapper etudiantMapper;

    @BeforeEach
    public void setUp() {
        etudiantMapper = new EtudiantMapperImpl();
    }
}
