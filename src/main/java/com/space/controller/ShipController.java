package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<Ship> getListShips(@RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "planet", required = false) String planet,
                           @RequestParam(value = "shipType",required = false) ShipType shipType,
                           @RequestParam(value = "after", required = false) Long after,
                           @RequestParam(value = "before", required = false) Long before,
                           @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                           @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                           @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                           @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                           @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                           @RequestParam(value = "minRating", required = false) Double minRating,
                           @RequestParam(value = "maxRating", required = false) Double maxRating,
                            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                           @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return shipService.getAllShips(Specification.where(shipService.filterByName(name))
                .and(shipService.filterByPlanet(planet))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByDate(after, before))
                .and(shipService.filterByUsed(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating(minRating, maxRating)),
                pageable).getContent();
    }

    @GetMapping(value = "/ships/count")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Integer getShipsCount(@RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "planet", required = false) String planet,
                          @RequestParam(value = "shipType", required = false) ShipType shipType,
                          @RequestParam(value = "after", required = false) Long after,
                          @RequestParam(value = "before", required = false) Long before,
                          @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                          @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                          @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                          @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                          @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                          @RequestParam(value = "minRating", required = false) Double minRating,
                          @RequestParam(value = "maxRating", required = false) Double maxRating) {
        return shipService.getAllShips(Specification.where(shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet)))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByDate(after, before))
                .and(shipService.filterByUsed(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating(minRating, maxRating))).size();
    }

    @PostMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Ship createShip(@RequestBody Ship ship) {
        return shipService.createShip(ship);
    }

    @PostMapping(value = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Ship editShip(@PathVariable(value = "id") String id, @RequestBody Ship ship) {
        Long validId = shipService.checkIdValid(id);
        return shipService.editShip(validId, ship);
    }

    @GetMapping(value = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Ship getShip(@PathVariable(value = "id") String id) {
        Long validId = shipService.checkIdValid(id);
        return shipService.getShipById(validId);
    }

    @DeleteMapping(value = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    void deleteShip(@PathVariable(value = "id") String id) {
        Long validId = shipService.checkIdValid(id);
        shipService.deleteShip(validId);
    }

}
