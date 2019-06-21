package vehicles;

import exceptions.EmptyRouteException;
import exceptions.OverCapacityException;
import exceptions.TransportFormatException;
import passengers.Passenger;
import routes.Route;
import stops.Stop;
import utilities.Writeable;

import java.util.ArrayList;
import java.util.List;

/**
 * A base public transport vehicle in the transportation network.
 */
public abstract class PublicTransport
        extends Object
        implements Writeable {
    // the passengers currently on board the vehicle
    private List<Passenger> passengers;

    // the place the vehicle is currently stopped
    private Stop currentLocation;

    // the maximum passengers allowed on board the vehicle
    private int capacity;

    // the vehicle's identifier
    private int id;

    // the route the vehicle follows
    private Route route;

    /**
     * Creates a new public transport vehicle with the given id, capacity, and
     * route.
     *
     * <p>The vehicle should initially have no passengers on board, and should be placed
     * at the beginning of the given route (given by {@link Route#getStartStop()}).
     * If the route is empty, the current location should be stored as null.
     *
     * <p> If the given capacity is negative, 0 should be stored as the capacity
     * instead (meaning no passengers will be allowed on board this vehicle).
     *
     * @param id The identifying number of the vehicle.
     * @param capacity The maximum number of passengers allowed on board.
     * @param route The route the vehicle follows. Note that the given route should
     *              never be null (@require route != null), and thus will not be
     *              tested with a null value.
     */
    public PublicTransport(int id, int capacity, Route route) {
        this.passengers = new ArrayList<>();
        this.capacity = capacity < 0 ? 0 : capacity;
        this.id = id;
        this.route = route;
        try {
            this.currentLocation = route.getStartStop();
        } catch (EmptyRouteException e) {
            this.currentLocation = null;
        }
    }

    /**
     * Returns the route this vehicle is on.
     *
     * @return The route this vehicle is on.
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Returns the id of this vehicle.
     *
     * @return The id of this vehicle.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current location of this vehicle.
     *
     * @return The stop this vehicle is currently located at, or null if it is not
     *         currently located at a stop.
     */
    public Stop getCurrentStop() {
        return currentLocation;
    }

    /**
     * Returns the number of passengers currently on board this vehicle.
     *
     * @return The number of passengers in the vehicle.
     */
    public int passengerCount() {
        return passengers.size();
    }

    /**
     * Returns the maximum number of passengers allowed on this vehicle.
     *
     * @return The maximum capacity of the vehicle.
     */
    public int getCapacity() {

        return capacity;
    }

    /**
     * Returns the type of this vehicle, as determined by the type of the route it
     * is on (i.e. The type returned by {@link Route#getType()}).
     *
     * @return The type of this vehicle.
     */
    public String getType() {

        return route.getType();
    }

    /**
     * Returns the passengers currently on-board this vehicle.
     *
     * <p>No specific order is required for the passenger objects in the returned
     * list.
     *
     * <p>Modifying the returned list should not result in changes to the internal
     * state of the class.
     *
     * @return The passengers currently on the public transport vehicle.
     */
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    /**
     * Adds the given passenger to this vehicle.
     *
     * <p>If the passenger is null, the method should return without adding it
     * to the vehicle.
     *
     * <p>If the vehicle is already at (or over) capacity, an exception should
     * be thrown and the passenger should not be added to the vehicle.
     *
     * @param passenger The passenger boarding the vehicle.
     * @throws OverCapacityException If the vehicle is already at (or over) capacity.
     */
    public void addPassenger(Passenger passenger) throws OverCapacityException {
        if (passenger == null) {
            return;
        }

        if (passengers.size() >= capacity) {
            throw new OverCapacityException();
        }
        passengers.add(passenger);
    }

    /**
     * Removes the given passenger from the vehicle.
     *
     * <p>If the passenger is null, or is not on board the vehicle, the method should
     * return false, and should not have any effect on the passengers currently
     * on the vehicle.
     *
     * @param passenger The passenger disembarking the vehicle.
     * @return True if the passenger was successfully removed, false otherwise (including
     *         the case where the given passenger was not on board the vehicle to
     *         begin with).
     */
    public boolean removePassenger(Passenger passenger) {
        return passengers.remove(passenger);
    }

    /**
     * Empties the vehicle of all its current passengers, and returns all the passengers
     * who were removed.
     *
     * <p>No specific order is required for the passenger objects in the returned
     * list.
     *
     * <p>If there are no passengers currently on the vehicle, the method just
     * returns an empty list.
     *
     * <p>Modifying the returned list should not result in changes to the internal
     * state of the class.
     *
     * @return The passengers who used to be on the vehicle.
     */
    public List<Passenger> unload() {
        List<Passenger> leaving = passengers;
        passengers = new ArrayList<>();
        return leaving;
    }

    /**
     * Updates the current location of the vehicle to be the given stop.
     *
     * <p>If the given stop is null, or is not on this public transport's route
     * the current location should remain unchanged.
     *
     * @param stop The stop the vehicle has travelled to.
     */
    public void travelTo(Stop stop) {
        if (!route.getStopsOnRoute().contains(stop)) {
            return;
        }

        currentLocation = stop == null ? currentLocation : stop;
    }

    /**
     * Creates a string representation of a public transport vehicle in the format:
     *
     * <p>'{type} number {id} ({capacity}) on route {route}'
     *
     * <p>without the surrounding quotes, and where {type} is replaced by the type of
     * the vehicle, {id} is replaced by the id of the vehicle, {capacity} is replaced
     * by the maximum capacity of the vehicle, and {route} is replaced by the route
     * number of the route the vehicle is on. For example:
     *
     * <p>bus number 1 (30) on route 1
     *
     * @return A string representation of the vehicle.
     */
    @Override
    public String toString() {
        return getType() + " number " + id + " (" + capacity + ") on route "
                + route.getRouteNumber();
    }

    /**
     * Creates a new public transport object based on the given string
     * representation.
     * The format of the given string should match that returned by the encode()
     * method, with one modification:
     *
     * '{type},{id},{capacity},{route},{extra}'
     *
     * where all parts of the string are as defined in encode(), except for
     * {extra}, which should be replaced with a different part depending on the
     * type of vehicle being decoded (as given by {type}).
     *
     * If {type} is bus, then {extra} should be the registration number of the
     * bus.
     * If {type} is train, then {extra} should be the carriage count of the
     * train.
     * If {type} is ferry, then {extra} should be the type of the ferry.
     *
     * The decoded vehicle should be added to the Route indicated by {route}
     * using the Route.addTransport(PublicTransport) method. If there are
     * multiple routes in the existingRoutes list which have the same number,
     * then the first route in the list with a matching number should be used.
     *
     * Whilst parsing, if spaces (i.e. ' ') are encountered before or after
     * integers, (e.g. {id}), the spaces should simply be trimmed (for example,
     *
     * using something like String.trim()). If spaces are encountered before or
     * after strings (e.g. {type}), the spaces should be considered part of the
     * string and not handled differently from any other character.
     * @param transportString The string to decode.
     * @param existingRoutes The routes which currently exist in the transport
     * network.
     * @return The decoded public transport object (a Bus, Train, or Ferry,
     * depending on the type given in the string).
     * @throws TransportFormatException If the given string or existingRoutes
     * list is null, or the string is otherwise incorrectly formatted
     * (according to the encode() representation). This includes, but is not
     * limited to:
     * A transport type that is not one of "bus", "train", or "ferry".
     * One of the id, capacity, or route number is not an integer value.
     * The route number given in the string does not match one of the given
     * existingRoutes.
     * The type of the route referenced in the given string does not match the
     * type given in the transportString (e.g. a Bus referencing a TrainRoute).
     * A vehicle of type train whose {extra} part (i.e. carriage count) is not
     * an integer value.
     * An error (i.e. EmptyRouteException or IncompatibleTypeException is
     * encountered whilst adding the vehicle to its route
     * Any extra delimiters (,) being encountered whilst parsing.
     * Any of the parts of the string being missing.
     */
    public static PublicTransport decode(String transportString,
                                         List<Route> existingRoutes)
            throws TransportFormatException{

        if (transportString == null || existingRoutes == null) {
            throw new TransportFormatException();
        }

        int id;
        int capacity;
        int routeNumber;
        String vehicleType;
        String extra;
        //Splitting the string to array to enhance the allocation of each param.
        String [] vehicles = transportString.split(",");

        try {
            vehicleType = vehicles[0];
            id = Integer.parseInt(vehicles[1].trim());
            capacity = Integer.parseInt(vehicles[2].trim());
            routeNumber = Integer.parseInt(vehicles[3].trim());
            extra = vehicles[4];

        } catch (Exception e){
            throw new TransportFormatException();
        }

        //invoking the helper method so that the object of vehicle is created
        //based on the type of vehicle.
        return vehicleDecoder(vehicleType, id,
                capacity, routeNumber, extra,
                existingRoutes);
    }


    /*
     * A method which enhance the decoding of vehicle then create and return
     * the the object based on the vehicle type and route number on the file.
     * @param vehicleType the type of the vehicle eg: bus, train or ferry.
     * @param id the id of the given type of vehicle
     * @param capacity the capacity of the vehicle
     * @param routeNumber the route number for the route this vehicle is
     * currently on
     * @param extra based on the type, @Link decode(String, List<Route>)
     * @param routes The routes which currently exist in the transport
     * @return a vehicle object given by the vehicle type.
     * @throws TransportFormatException @Link TransportFormatException class
     */
    private static PublicTransport vehicleDecoder(String
            vehicleType, int id, int capacity, int routeNumber, String extra,
                                                  List<Route> routes)
            throws TransportFormatException {

        //Loop over all the routes on this network
        PublicTransport vehicle = null;
        for (Route route : routes) {
            //check if the route type and number is the same as the vehicle type
            // and route number the vehicle has.
            if (route.getRouteNumber() == routeNumber &&
                    route.getType().equals(vehicleType.trim())) {
                if (vehicleType.equals("train")) {
                    //if the route type is train, the extra is the carriageCount.
                    int carriageCount;
                    carriageCount = Integer.parseInt(extra.trim());
                    vehicle = new Train(id, capacity, route,
                            carriageCount);
                    try {
                        route.addTransport(vehicle);

                    } catch (Exception e) {

                    }
                    break;

                } else if (vehicleType.equals("bus")) {
                    vehicle = new Bus(id, capacity, route,
                            extra);

                    try {
                        route.addTransport(vehicle);

                    } catch (Exception e) {

                    }
                    break;

                } else if (vehicleType.equals("ferry")) {
                    vehicle = new Ferry(id, capacity, route,
                            extra);

                    try {
                        route.addTransport(vehicle);

                    } catch (Exception e) {

                    }
                    break;

                } else {
                    throw new TransportFormatException();
                }
            }
        }

        return vehicle;
    }

    /**
     * Encodes this vehicle as a string in the format:
     * '{type},{id},{capacity},{route}'
     *
     * without the surrounding quotes, and where {type} is replaced by the
     * type of the vehicle, {id} is replaced by the id of the vehicle,
     * {capacity} is replaced by the maximum capacity of the vehicle, and
     * {route} is replaced by the route number of the route the vehicle is on.
     * For example:
     *
     * bus,1,30,1
     * @return This vehicle encoded as a string.
     */
    public String encode() {

        StringBuilder builder = new StringBuilder();

        builder.append(getType()).append(",");
        builder.append(getId()).append(",");
        builder.append(getCapacity()).append(",");
        builder.append(getRoute().getRouteNumber());

        return builder.toString();
    }
}
