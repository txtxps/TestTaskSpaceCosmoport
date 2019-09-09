package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {
    List<Ship> getAllShips();
    Ship createShip(Ship ship);
    Ship editShip(Long id, Ship ship);
    void deleteShip(Long id);
    Ship getShipById(Long id);
    Specification<Ship> filterByName(String name);
    Specification<Ship> filterByPlanet(String planet);
    Specification<Ship> filterByShipType(ShipType shipType);
    Specification<Ship> filterByDate(Long after, Long before);
    Specification<Ship> filterByUsed(Boolean isUsed);
    Specification<Ship> filterBySpeed(Double min, Double max);
    Specification<Ship> filterByCrewSize(Integer min, Integer max);
    Specification<Ship> filterByRating(Double min, Double max);
    Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName);
    List<Ship> getAllShips(Specification<Ship> specification);
    Long checkIdValid(String id);
}
