/*
 * Copyright (c) 2017 CODEMONS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package codemons.transporter.trip.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codemons.transporter.location.model.Location;
import codemons.transporter.location.service.LocationService;
import codemons.transporter.reservation.service.ReservationService;
import codemons.transporter.route.model.Route;
import codemons.transporter.route.model.RouteRO;
import codemons.transporter.route.service.RouteService;
import codemons.transporter.trip.model.Trip;
import codemons.transporter.trip.model.TripRO;
import codemons.transporter.trip.model.TripRepository;
import codemons.transporter.vehicle.model.Vehicle;
import codemons.transporter.vehicle.service.VehicleService;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ReservationService reservationService;


     public List<TripRO> getTripROsByFromToJourneyDate(long from, long to, Date dt) {
        List<Trip> trips = tripRepository.findByFromAndToAndDt(from, to, dt);

        List<TripRO> tripROs = new ArrayList<TripRO>();

         for (Trip trip:
              trips) {
             List<RouteRO> routemap = new ArrayList<RouteRO>();
             Route route = routeService.getRoute(Long.parseLong(trip.getRoutemap()));
             Location fromLocation = locationService.getLocation(route.getFrom());
             Location toLocation = locationService.getLocation(route.getTo());
             RouteRO routeRO = new RouteRO(route, fromLocation, toLocation);
             Vehicle vehicle = vehicleService.getVehicle(trip.getVehicle());

             routemap.add(routeRO);

             long seats = trip.getSeating();
             seats = seats - reservationService.countReservationsByTrip(trip.getId());

             TripRO dto = new TripRO(trip, routemap, vehicle);
             dto.setSeating(seats);


             tripROs.add(dto);
         }
        return tripROs;
     }


}

