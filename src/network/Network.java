package network;

import exceptions.TransportFormatException;
import routes.Route;
import stops.Stop;
import vehicles.PublicTransport;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Represents the transportation network, and manages all of the various components therein.
 * <p> A network consists of  vehicles, routes and stops as one entity.
 */
public class Network {

    // The stops which make up this network
    private List<Stop> stops;

    // The routes which make up the network
    private List<Route> routes;

    // The vehicle this network has.
    private List<PublicTransport> vehicles;

    /**
     * Creates a new empty Network with no stops, vehicles, or routes.
     */
    public Network() {

        this.stops = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        this.routes = new ArrayList<>();

    }

    /**
     * Creates a new Network from information contained in the file indicated
     * by the given filename.
     * The file should be in the following format:
     * {number_of_stops}
     * {stop0:x0:y0}
     * ...
     * {stopN:xN:yN}
     * {number_of_routes}
     * {type0,name0,number0:stop0|stop1|...|stopM}
     * ...
     * {typeN,nameN,numberN:stop0|stop1|...|stopM}
     * {number_of_vehicles}
     * {type0,id0,capacity0,routeNumber,extra}
     * ...
     * {typeN,idN,capacityN,routeNumber,extra}
     * where {number_of_stops}, {number_of_routes}, and {number_of_vehicles}
     * are the number of stops,
     * routes, and vehicles (respectively) in the network, and where
     * {stop0,x0,y0} is the encode() representation of
     * a Stop. type0,name0,number0:stop0|stop1|...|stopM} is the encode()
     * representation of a Route, and {typeN,idN,
     * capacityN,routeNumber,extra} is the encode() representation of a
     * PublicTransport.
     *
     * Examples of valid file contents:
     * 4
     * stop0:0:1
     * stop1:-1:0
     * stop2:4:2
     * stop3:2:-8
     * 2
     * train,red,1:stop0|stop2|stop1
     * bus,blue,2:stop1|stop3|stop0
     * 3
     * train,123,30,1,2
     * train,42,60,1,3
     * bus,412,20,2,ABC123
     *
     * @param filename The name of the file to load the network from.
     * @throws IOException If any IO exceptions occur whilst trying to read
     * from the file, or if the filename is null.
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
    public Network(String filename)
            throws IOException,
            TransportFormatException {

        if(filename == null){
            throw new IOException();
        }

        int vehicleNumber;
        int routeNumber;
        int stopNumber;

        BufferedReader reader;
        try {
            File filePath = new File(filename);
            reader = new BufferedReader(new
                    FileReader(filePath));

        } catch (Exception e) {
            throw new IOException();
        }

        try {

            String lineRead;
            int count;
            //Use the helper method to test if the first line is a valid digit
            stopNumber = networkHelper(reader.readLine());
            for (count = 0; count < stopNumber; count++) {
                lineRead = reader.readLine();
                addStop(Stop.decode(lineRead));
            }

            routeNumber = networkHelper(reader.readLine());
            for (count = 0; count < routeNumber; count++) {
                lineRead = reader.readLine();
                addRoute(Route.decode(lineRead,
                        getStops()));

            }

            vehicleNumber = networkHelper(reader.readLine());
            for (count = 0; count < vehicleNumber; count++) {
                lineRead = reader.readLine();
                addVehicle(PublicTransport.decode(lineRead,
                        getRoutes()));
            }
            reader.close();
        } catch (Exception e) {
            throw new TransportFormatException();
        }
    }

    /*
     * A method which help the Network(filename) to check whether the first line
     * is a valid integer value.
     * @param lineRead the first line read to be checked
     * @return an integer value of specific number line contents on the file.
     * @throws TransportFormatException see {@Link Network(String filename)}.
     */
    private int networkHelper(String lineRead) throws TransportFormatException {

        int numberRead;

        try {
            numberRead = Integer.parseInt(lineRead.trim());
            if(numberRead < 0) {
                throw new TransportFormatException();
            }
        } catch (Exception e) {
            throw new TransportFormatException();
        }

        return numberRead;
    }

    /**
     * Adds the given route to the network.
     * If the given route is null, it should not be added to the network.
     * @param route The route to add to the network.
     */
    public void addRoute(Route route) {

        if (route != null) {
            this.routes.add(route);
        }
    }

    /**
     * Adds the given stop to the transportation network.
     * If the given stop is null, it should not be added to the network.
     * @param stop The stop to add to the network.
     */
    public void addStop(Stop stop) {

        if (stop != null) {
            this.stops.add(stop);
        }
    }

    /**
     * Adds multiple stops to the transport network.
     * If any of the stops in the given list are null, none of them should be
     * added (i.e. either all of the stops are added, or none are).
     * @param stops The stops to add to the network.
     */
    public void addStops(List<Stop> stops) {

        if(stops == null ) {
            return;
        }
        for (Stop stop : stops) {
            if (stop == null) {
                return;
            }
        }
        this.stops.addAll(stops);
    }

    /**
     * Adds the given vehicle to the network.
     * If the given vehicle is null, it should not be added to the network.
     * @param vehicle The vehicle to add to the network.
     */
    public void addVehicle(PublicTransport vehicle) {

        if (vehicle != null) {

            this.vehicles.add(vehicle);
        }
    }

    /**
     * Gets all the routes in this network.
     * Modifying the returned list should not result in changes to the internal
     * state of the class
     * @return All the routes in the network.
     */
    public List<Route> getRoutes() {

        return new ArrayList<>(this.routes);
    }

    /**
     * Gets all of the stops in this network.
     * Modifying the returned list should not result in changes to the internal
     * state of the class.
     * @return All the stops in the network.
     */
    public List<Stop> getStops() {

        return new ArrayList <>(this.stops);
    }

    /**
     * Gets all the vehicles in this transportation network.
     * Modifying the returned list should not result in changes to the internal
     * state of the class.
     * @return All the vehicles in the transportation network.
     */
    public List<PublicTransport> getVehicles() {

        return new ArrayList<>(this.vehicles);
    }

    /**
     * Saves this network to the file indicated by the given filename.
     * The file should be written with the same format as described in the
     * Network(String) constructor.
     * The stops should be written to the file in the same order in which they
     * were added to the network. This also applies to the routes and the
     * vehicles.
     * If the given filename is null, the method should do nothing.
     * @param filename The name of the file to save the network to.
     * @throws IOException If there are any IO errors whilst writing to the
     * file.
     */
    public void save(String filename) throws IOException {

        if (filename == null) {
            return;
        }

        //Ensure that we are saving a .txt file extension for reusable.
        String file = filename.contains(".txt") ? filename : filename + ".txt";
        StringBuilder builder = new StringBuilder();

        builder.append(getStops().size()).append("\n");
        for (Stop stop : getStops()) {
            builder.append(stop.encode()).append("\n");
        }

        builder.append(getRoutes().size()).append("\n");
        for (Route route : getRoutes()) {
            builder.append(route.encode()).append("\n");
        }

        builder.append(getVehicles().size()).append("\n");
        for (PublicTransport vehicle : getVehicles ()) {
            builder.append(vehicle.encode ()).append("\n");
        }

        String networkFile = builder.toString().trim();

        PrintWriter writer = new PrintWriter(file);
        writer.print(networkFile);
        writer.close();
    }
}
