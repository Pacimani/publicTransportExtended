package routes;

import exceptions.EmptyRouteException;
import exceptions.IncompatibleTypeException;
import exceptions.TransportFormatException;
import stops.Stop;
import utilities.Writeable;
import vehicles.PublicTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a route in the transportation network.
 *
 * <p>A route is essentially a collection of stops which public transport vehicles
 * can follow.
 */
public abstract class Route implements Writeable {
    // the name of the route
    private String name;

    // the number of the route
    private int routeNumber;

    // tracks where vehicles are currently located on the route
    private List<PublicTransport> vehicles;

    // the stops which make up the route
    private List<Stop> route;

    /**
     * Creates a new Route with the given name and number.
     *
     * <p>The route should initially have no stops or vehicles on it.
     *
     * <p>If the given name contains any newline characters ('\n') or carriage returns
     * ('\r'), they should be removed from the string before it is stored.
     *
     * <p>If the given name is null, an empty string should be stored in its place.
     *
     * @param name The name of the route.
     * @param routeNumber The route number of the route.
     */
    public Route(String name, int routeNumber) {
        this.name = name == null ? "" : name.replace("\n", "")
                .replace("\r", "");
        this.routeNumber = routeNumber;
        this.vehicles = new ArrayList<>();
        this.route = new ArrayList<>();
    }

    /**
     * Returns the name of the route.
     *
     * @return The route name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of the route.
     *
     * @return The route number.
     */
    public int getRouteNumber() {
        return routeNumber;
    }

    /**
     * Returns the stops which comprise this route.
     *
     * <p>The order of the stops in the returned list should be the same as the
     * order in which the stops were added to the route.
     *
     * <p>Modifying the returned list should not result in changes to the internal
     * state of the class.
     *
     * @return The stops making up the route.
     */
    public List<Stop> getStopsOnRoute() {
        return new ArrayList<>(route);
    }

    /**
     * Returns the first stop of the route (i.e. the first stop to be added to the
     * route).
     *
     * @return The start stop of the route.
     * @throws EmptyRouteException If there are no stops currently on the route
     */
    public Stop getStartStop() throws EmptyRouteException {
        if (route.isEmpty()) {
            throw new EmptyRouteException();
        }

        return route.get(0);
    }

    /**
     * Adds a stop to the route.
     *
     * <p>If the given stop is null, it should not be added to the route.
     *
     * <p>If this is the first stop to be added to the route, the given stop should
     * be recorded as the starting stop of the route. Otherwise, the given stop
     * should be recorded as a neighbouring stop of the previous stop on the route
     * (and vice versa) using the {@link Stop#addNeighbouringStop(Stop)} method.
     *
     * <p>This route should also be added as a route of the given stop (if the given
     * stop is not null) using the {@link Stop#addRoute(Route)} method.
     *
     * @param stop The stop to be added to this route.
     */
    public void addStop(Stop stop) {
        if (stop == null) {
            return;
        }

        stop.addRoute(this);
        route.add(stop);

        // return if this was the first stop
        if (route.size() == 1) {
            return;
        }

        Stop previous = route.get(route.size() - 2);
        previous.addNeighbouringStop(stop);
        stop.addNeighbouringStop(previous);
    }

    /**
     * Returns the public transport vehicles currently on this route.
     *
     * <p>No specific order is required for the public transport objects in the
     * returned list.
     *
     * <p>Modifying the returned list should not result in changes to the internal
     * state of the class.
     *
     * @return The vehicles currently on the route.
     */
    public List<PublicTransport> getTransports() {
        return new ArrayList<>(this.vehicles);
    }

    /**
     * Adds a vehicle to this route.
     *
     * <p>If the given transport is null, it should not be added to the route.
     *
     * <p>The method should check for the transport being null first, then for an
     * empty route, and then for incompatible types (in that order).</p>
     *
     * @param transport The vehicle to be added to the route.
     * @throws EmptyRouteException If there are not yet any stops on the route.
     * @throws IncompatibleTypeException IIf the given string or existingStops
     * list is null, or the string is incorrectly formatted (according to the
     * encode() representation). This includes, but is not limited to:
     * A route type that is not one of "bus", "train", or "ferry".
     * The route number is not an integer value.
     * The stop name given in the string does not match one of the given
     * existingStops.
     * Any extra delimiters (, : |) being encountered whilst parsing.
     * Any of the parts of the string being missing. This includes empty s
     * trings as stop names in the routeString (e.g. 'bus,red,1:||'). A
     * routeString with no stops (e.g. 'bus,red,1:') is, however, valid.
     * A routeString with an empty name is also valid (as this is allowed by
     * the Route constructor).
     */
    public void addTransport(PublicTransport transport)
            throws EmptyRouteException, IncompatibleTypeException {
        if (transport == null) {
            return;
        }

        if (route.isEmpty()) {
            throw new EmptyRouteException();
        }

        if (!getType().equals(transport.getType())) {
            throw new IncompatibleTypeException();
        }

        vehicles.add(transport);
    }

    /**
     * Compares this stop with another object for equality.
     *
     * Two routes are equal if their names and route numbers are equal.
     *
     * {@inheritDoc}
     *
     * @param other The other object to compare for equality.
     * @return True if the objects are equal (as defined above), false otherwise
     *         (including if other is null or not an instance of the {@link Route}
     *         class.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Route)) {
            return false;
        }
        Route otherRoute = (Route) other;
        return name.equals(((Route) other).getName())
                && routeNumber == otherRoute.getRouteNumber();
    }

    @Override
    public int hashCode() {

        return routeNumber;
    }

    /**
     * Returns the type of this route.
     *
     * @return The type of the route (see subclasses)
     */
    public abstract String getType();

    /**
     * Creates a string representation of a route in the format:
     *
     * <p>'{type},{name},{number}:{stop0}|{stop1}|...|{stopN}'
     *
     * <p>without the surrounding quotes, and where {type} is replaced by the type
     * of the route, {name} is replaced by the name of the route, {number} is replaced
     * by the route number, and {stop0}|{stop1}|...|{stopN} is replaced by a list of
     * the names of the stops stops making up the route. For example:
     *
     * <p>bus,red,1:UQ Lakes|City|Valley
     *
     * @return A string representation of the route.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getType()).append(",");
        builder.append(name).append(",").append(routeNumber);
        builder.append(":");

        for (Stop stop : route) {
            builder.append(stop.getName()).append("|");
        }

        if (!route.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    /**
     * Creates a new route object based on the given string representation.
     * The format of the string should match that returned by the encode()
     * method.
     *
     * The correct stops should also be added to the decoded route using the
     * addStop(Stop) method. If there are multiple stops in the existingStops
     * list which have the same name, then the first stop in the list with a
     * matching name should be used. This still applies if there are multiple
     * stops in the routeString with the same name (i.e. they should both use
     * the first stop in the list with a matching name).
     *
     * Whilst parsing, if spaces (i.e. ' ') are encountered before or after
     * integers, (e.g. {number}), the spaces should simply be trimmed
     * (for example, using something like String.trim()). If spaces are
     * encountered before or after strings (e.g. {type}), the spaces should be
     * considered part of the string and not handled differently from any other
     * character.
     * @param routeString The string to decode.
     * @param existingStops The stops which currently exist in the transport
     * network.
     * @return The decoded route object (a BusRoute, TrainRoute, or FerryRoute,
     * depending on the type given in the string).
     * @throws TransportFormatException If the given string or existingStops
     * list is null, or the string is incorrectly formatted (according to the
     * encode() representation). This includes, but is not limited to:
     * A route type that is not one of "bus", "train", or "ferry".
     * The route number is not an integer value.
     * The stop name given in the string does not match one of the given
     * existingStops. Any extra delimiters (, : |) being encountered whilst
     * parsing. Any of the parts of the string being missing. This includes
     * empty strings as stop names in the routeString (e.g. 'bus,red,1:||'). A
     * routeString with no stops (e.g. 'bus,red,1:') is, however, valid. A
     * routeString with an empty name is also valid (as this is allowed by the
     * Route constructor).
     */
    public static Route decode(String routeString,
                               List<Stop> existingStops)
            throws TransportFormatException {
        if(routeString == null || existingStops == null) {
            throw new TransportFormatException ();
        }

        //Make an array of the contents on each iteration.
        String[] routesContent = routeString.split(",");
        //Extracting the route type from the section of the element
        // (comma separated section)
        String routeType = routesContent[0];
        //Then the route name as the second element
        String routeName = routesContent[1];
        int routeNumber;
        String[] routeStopNames = null;
        String routeStopName = null ;

        try {
            //extracting the route number from the second section of the
            // component and convert it to int.
            routeNumber = Integer.parseInt(routesContent[2].substring
                    ( 0, 1 ).trim());
            //checking if there route has more then one stop given by stop
            // names then put them in an array
            if(routesContent[2].contains ( "|" )){
                routeStopNames = routesContent[2].substring(2).
                        split("\\|");

                //make sure that after the split we have more than one StopName
                if(routeStopNames.length <= 1 ) {
                    throw new TransportFormatException();
                }

                //If it does not have many stop, check if it has only one
                // stopName then assign it to a string variable.
            }else if (routesContent[2].split( ":" ).length == 2) {
                routeStopName = routesContent[2].substring (2);

            }
        } catch (Exception e) {
            throw new TransportFormatException();
        }

        return routeDecoder(routeType, routeName,
                routeNumber, routeStopNames,
                routeStopName, existingStops);

    }
    /*
     * A method which helps the Route.decode method in decoding the route
     * component on the file. by creating object of route based on the route
     * type given.
     * @param routeType the route type eg: train, bus or ferry type of routes
     * @param routeName the name of the route contained in the file.
     * @param routeNumber an integer number of the route.
     * @param routeStopNames a list of stop names of the route contained on the
     * file.
     * @param routeStopName the stop name if a route has only one stop
     * @throws TransportFormatException @Link decode(String, List<Stop>)
     */
    private static Route routeDecoder(String routeType, String routeName,
                                      int routeNumber, String[] routeStopNames,
                                      String routeStopName, List<Stop> stops)
            throws TransportFormatException {

        switch (routeType){
            case ("train"):
               Route trainRoute = routeDecoder(new TrainRoute(
                       routeName, routeNumber),
                       routeType, routeStopNames,
                       routeStopName, stops);
               return trainRoute;
            case ("bus"):
                Route busRoute = routeDecoder(new BusRoute(
                        routeName, routeNumber),
                        routeType, routeStopNames,
                        routeStopName, stops);
                return busRoute;
            case ("ferry"):
                Route ferryRoute = routeDecoder(new FerryRoute(
                                routeName, routeNumber),
                        routeType, routeStopNames,
                        routeStopName, stops);
                return ferryRoute;

            default:
                throw new TransportFormatException();

        }
    }

    /*
     * Overload method of routeDecoder which enhance decoding of routes
     * components on the file as its counterpart.
     * @param trainRoute an object of TrainRoute class
     * @param busRoute an object of BusRoute class
     * @param ferryRoute an object of ferryRoute class
     * @param routeType the type of route found on this network. eg train, bus
     * or ferry.
     * @param routeStopNames a list of stop names found on this routes
     * @param routeStopName one stop name if a route has only one stop
     * @return list of route object; trainRoute, busRoute and ferryRoute.
     * @throws TransportFormatException @Link decode(String, List<Stop>)
     */
    private static Route routeDecoder(Route route, String routeType,
                                      String[] routeStopNames,
                                      String routeStopName,
                                      List<Stop> stops)
            throws TransportFormatException {

        List<String> stopNameOnString = new ArrayList<>();
        List<String> validStopNameOnNet = new ArrayList<>();

        if(routeStopNames != null){
            for(String stopName: routeStopNames){
                stopNameOnString.add(stopName);
            }

        } else if (routeStopName != null ) {
            stopNameOnString.add(routeStopName);
        }
        //Iterate over the stops on this network if it has any, add their names
        // to be compared with stop names on file
        for (Stop stop : stops) {

            for (String stopName : stopNameOnString) {
                if (stop.getName().equals(stopName)) {

                    if (routeType.equals("train")) {
                        //check if this is the first stop to add this route.
                        // If is not, add the route to the stop.
                        if(!validStopNameOnNet.contains(stop.getName())) {
                            route.addStop( new Stop(stopName,
                                    stop.getX(), stop.getY()));
                            stop.addRoute(route);
                        }

                    } else if (routeType.equals("bus")) {

                        if(!validStopNameOnNet.contains(stop.getName())) {
                            route.addStop ( new Stop ( stopName,
                                    stop.getX(), stop.getY()));
                            stop.addRoute(route);
                        }

                    } else if (routeType.equals("ferry")) {

                        if(!validStopNameOnNet.contains(stop.getName())) {
                            route.addStop( new Stop( stopName,
                                    stop.getX(), stop.getY()));
                            stop.addRoute(route);
                        }
                    }
                }
            }
            //storing stop names to avoid similar multiple stops being added
            // to the same route or to the stop and later test for invalid names
            validStopNameOnNet.add(stop.getName());

        }
        //Checking for invalid stop names
        if(stopNameOnString.size() > 0 &&
                !validStopNameOnNet.containsAll(stopNameOnString)) {
            throw new TransportFormatException();
        }

        return route;
    }

    /**
     * Encodes this route as a string in the same format as specified in
     * toString().
     * @return This route encoded as a string.
     */
    public String encode() {
        return this.toString ();
    }

}
