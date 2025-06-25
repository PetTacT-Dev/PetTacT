package com.pettact.api.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.PetFacilityEntity;

@Repository
public interface PetFacilityRepository extends JpaRepository<PetFacilityEntity, Long> {
}
