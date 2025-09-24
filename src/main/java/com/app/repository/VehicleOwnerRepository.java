package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.VehicleOwner;

public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, Integer> {

}
