package com.space.service;

import com.space.IncorrectRequestException;
import com.space.ShipDoesntExistException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
//    @Transactional
    public List<Ship> getAllShips() {
        return shipRepository.findAll();
    }

    @Override
//    @Transactional
    public Ship createShip(Ship ship) {
        if (ship.getName() == null
            || ship.getPlanet() == null
            || ship.getShipType() == null
            || ship.getProdDate() == null
            || ship.getSpeed() == null
            || ship.getCrewSize() == null)
            throw new IncorrectRequestException("Error request");
        checkInputParams(ship);
        if (ship.getUsed() == null)
            ship.setUsed(false);

        ship.setRating(calculateRating(ship));

        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Ship editShip(Long id, Ship ship) {
        if (id < 0) throw new IncorrectRequestException("Incorrect id");
        if (!shipRepository.existsById(id)) throw new ShipDoesntExistException("Ship doesn't exist");
        Ship editShip = shipRepository.getOne(id);
        checkInputParams(ship);
        if (ship.getName() != null)
            editShip.setName(ship.getName());

        if (ship.getPlanet() != null)
            editShip.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            editShip.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            editShip.setProdDate(ship.getProdDate());

        if (ship.getSpeed() != null)
            editShip.setSpeed(ship.getSpeed());

        if (ship.getUsed() != null)
            editShip.setUsed(ship.getUsed());

        if (ship.getCrewSize() != null)
            editShip.setCrewSize(ship.getCrewSize());

        editShip.setRating(calculateRating(editShip));

        return shipRepository.save(editShip);
    }

    @Override
//    @Transactional
    public void deleteShip(Long id) {
        if (shipRepository.existsById(id))
            shipRepository.deleteById(id);
        else throw new ShipDoesntExistException("Ship doesn't exist");
    }

    @Override
//    @Transactional
    public Ship getShipById(Long id) {
        if (!shipRepository.existsById(id))
            throw new ShipDoesntExistException("Ship doesn't exist");
        return shipRepository.findById(id).get();
    }

    private void checkInputParams(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new IncorrectRequestException("Incorrect name");

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new IncorrectRequestException("Incorrect planet");

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            if (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019)
                throw new IncorrectRequestException("Incorrect date");
        }

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01d || ship.getSpeed() > 0.99d))
            throw new IncorrectRequestException("Incorrect speed");

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new IncorrectRequestException("Incorrect crewSize");
    }

    private Double calculateRating(Ship ship) {
        double k;
        if (ship.getUsed()) k = 0.5;
        else k = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        BigDecimal rating = new BigDecimal((80*ship.getSpeed()*k)/(3019 - year + 1));
        rating = rating.setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }

    @Override
    public Specification<Ship> filterByName(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {
        return (root, query, criteriaBuilder) -> planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, criteriaBuilder) -> shipType == null ? null : criteriaBuilder.equal(root.get("shipType"), shipType);
    }

    @Override
    public Specification<Ship> filterByDate(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) return null;
            if (after == null) {
                Date newBefore = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), newBefore);
            }
            if (before == null) {
                Date newAfter = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), newAfter);
            }
            Date newBefore = new Date(before);
            Date newAfter = new Date(after);
            return criteriaBuilder.between(root.get("prodDate"), newAfter, newBefore);
        };
    }

    @Override
    public Specification<Ship> filterByUsed(Boolean isUsed) {
        return (root, query, criteriaBuilder) -> {
            if (isUsed == null) return null;
            if (isUsed) return criteriaBuilder.isTrue(root.get("isUsed"));
            else return criteriaBuilder.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max==null) return null;
            if (min==null) return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), min);
            return criteriaBuilder.between(root.get("speed"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max==null) return null;
            if (min==null) return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), min);
            return criteriaBuilder.between(root.get("crewSize"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByRating(Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max==null) return null;
            if (min==null) return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), min);
            return criteriaBuilder.between(root.get("rating"), min, max);
        };
    }


    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName) {
        return shipRepository.findAll(specification, sortedByName);
    }

    @Override
    public List<Ship> getAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Long checkIdValid(String id) {
        if (id == null || id.equals("0") || id.equals(""))
            throw new IncorrectRequestException("Incorrect id");
        try {
            Long validId = Long.parseLong(id);
            return validId;
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException("Incorrect id");
        }
    }
}
