package com.itsandra.etudiantetranger.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NiveauMapperTest {

    private NiveauMapper niveauMapper;

    @BeforeEach
    public void setUp() {
        niveauMapper = new NiveauMapperImpl();
    }
}
