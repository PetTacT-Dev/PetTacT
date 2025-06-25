package com.pettact.api.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.PetShelterEntity;

@Repository
public interface PetShelterRepository extends JpaRepository<PetShelterEntity, Long> {
}
