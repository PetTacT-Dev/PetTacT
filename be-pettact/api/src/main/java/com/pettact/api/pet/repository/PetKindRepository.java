package com.pettact.api.pet.repository;

import com.pettact.api.pet.entity.PetKindEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetKindRepository extends JpaRepository<PetKindEntity, String> {
	boolean existsByKindCd(String kindCd); 
}
