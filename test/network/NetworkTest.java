package network;

import exceptions.TransportFormatException;
import org.junit.Before;
import org.junit.Test;
import passengers.ConcessionPassenger;
import passengers.Passenger;
import routes.BusRoute;
import routes.FerryRoute;
import routes.Route;
import routes.TrainRoute;
import stops.Stop;
import vehicles.Bus;
import vehicles.Ferry;
import vehicles.PublicTransport;
import vehicles.Train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class NetworkTest {

    private Stop stop;
    private Stop stop1;
    private Stop stop2;
    private List <Stop> stops;
    private List <Route> routes;
    private Network network;
    private Route ferryRoute;
    private Route busRoute;
    private Route trainRoute;
    private PublicTransport bus;
    private PublicTransport train;
    private PublicTransport ferry;
    private Passenger passenger;
    private ConcessionPassenger concessionPassenger;

    @Before
    public void setUp(){
        stop = new Stop("stop0",0,1 );
        stop1 = new Stop("stop1", 1,5 );
        stop2 = new Stop("stop2",3,9 );
        busRoute = new BusRoute( "blue", 2);
        trainRoute = new TrainRoute( "red", 1);
        ferryRoute = new FerryRoute("Gala", 3);
        ferry = new Ferry(2,23,ferryRoute,"3");
        network = new Network();
        bus = new Bus ( 2, 20, busRoute,
                "ABC23");
        stops = new ArrayList<>();
        routes = new ArrayList<>();
        passenger = new Passenger("Pac");
        concessionPassenger = new ConcessionPassenger("Ruk",
                stop, 4290234);


    }

    @Test(expected = IOException.class)
    public void testIOException() throws IOException,
            TransportFormatException{
        //Testing whether the exception occurres when a null name is given
        network = new Network(null);

        //giving an existing filename with no extension. eg .txt
        network = new Network ("pac");
        //giving a non existing filename
        network = new Network("pams.txt");

    }

    @Test(expected = TransportFormatException.class)
    public void testTransportFormatException() throws TransportFormatException
    , IOException {
        //Test for TFE when there is a black line on the file.
        network = new Network("invalidBlankLines.txt");
        //test for when there an invalid an empty stopName on the route
        network = new Network("invalidRouteEmptyStopNames.txt");
        //test for invalid RouteStops
        network = new Network("invalidRouteStops.txt");
        //testing for invalid route type
        network = new Network("invalidRouteTypes.txt");
        //test for invalidStopCount
        network = new Network("invalidStopCount.txt");
        //test for invalidStopDelimiters
        network = new Network("invalidStopDelimiters.txt");
        //test for invalid transport integer number
        network = new Network("invalidTransportIntegers.txt");
        //test for missing part in transport
        network = new Network("invalidTransportPartsMissing.txt");
        //test with a negative number of element on the file
        network = new Network("invalidWithNegativeNumber.txt");
        //test to save a file name with space and new line
        network.save("file nam\ne ");

    }

    @Test
    public void addRoute() throws IOException, TransportFormatException{

        Network network = new Network();
        //check first if this network has any route
        assertEquals(0, network.getRoutes().size());
        //check for null if has any effect.
        network.addRoute(null);
        assertEquals(0, network.getRoutes().size());
        //add one route and check the size
        network.addRoute(trainRoute);
        assertEquals( 1,network.getRoutes().size());
        //add another route, check if it exist as well as the size
        network.addRoute(busRoute);
        assertEquals(2, network.getRoutes().size());
        assertTrue(network.getRoutes().contains(busRoute));
        //add routes from a valid text file and check if they are added
        network = new Network ( "validFromSpec.txt" );
        assertEquals ( 2, network.getRoutes().size());

    }

    @Test
    public void addStop() throws IOException, TransportFormatException {

        Network network = new Network();
        //check first if this network has any route
        assertEquals(0, network.getStops().size());
        //check for null if has any effect.
        network.addStop(null);
        assertEquals(0, network.getStops().size());
        //add one route and check the size
        network.addStop(stop);
        assertEquals( 1,network.getStops().size());
        //add another route, check if it exist as well as the size
        network.addStop(stop1);
        assertEquals(2, network.getStops().size());
        assertTrue(network.getStops().contains(stop1));
        //add routes from a valid text file and check if they are added
        network = new Network ( "validFromSpec.txt" );
        assertEquals ( 4, network.getStops().size());

    }

    @Test
    public void addStops() {

        Network network = new Network();
        List<Stop> stops = new ArrayList <> ();
        stops.add(stop);
        stops.add(stop1);
        stops.add(stop2);
        //check first if this network has any route
        assertEquals(0, network.getStops().size());
        //check for null arguments
        network.addStops(null);
        assertEquals(0, network.getStops().size());
        //check for a one null stop in a list
        stops.add(null);
        network.addStops(stops);
        assertEquals(0, network.getStops().size());
        //Adding a valid list of stops.
        stops.clear();
        stops.add(stop);
        stops.add(stop1);
        stops.add(stop2);
        network.addStops(stops);
        assertEquals(3, network.getStops().size());

    }

    @Test
    public void addVehicle() throws IOException, TransportFormatException {
        //start with a constructor with no argument and test if it has any veh..
        network = new Network();
        assertEquals(0, network.getVehicles().size());
        //Adding a null value
        network.addVehicle(null);
        assertEquals(0, network.getVehicles().size());
        //adding a vehicle and check if it is added
        trainRoute = new TrainRoute("kalala", 3);
        train = new Train(8,200,trainRoute,12);
        network.addVehicle(train);
        assertEquals(1, network.getVehicles().size());

        assertEquals("train,8,200,3,12",
                network.getVehicles().get(0).encode());

        assertTrue(network.getVehicles().contains(train));
        //check for non existing vehicle on the network
        assertFalse(network.getVehicles().contains(bus));
        //try to add a vehicle with passenger in it and add it to this net..
        try {
            bus.addPassenger(passenger);
            bus.addPassenger(concessionPassenger);
        } catch (Exception e) {

        }
        network.addVehicle(bus);
        assertEquals(2, network.getVehicles().size());
        //finally use a valid text file to check if the vehicle are added
        routes.add(busRoute);
        routes.add(trainRoute);
        Network net = new Network("validFromSpec.txt");

        assertEquals(5, (net.getVehicles().size() +
                network.getVehicles().size()));

    }

    @Test
    public void getRoutes() throws IOException, TransportFormatException {
        Network network = new Network();
        //check first if this network has any route
        assertEquals(0, network.getRoutes().size());
        //check for null if has any effect.
        network.addRoute(null);
        assertEquals(0, network.getRoutes().size());
        //add one route and check the size
        network.addRoute(trainRoute);
        assertEquals( 1,network.getRoutes().size());
        //add another route, check if it exist as well as the size
        network.addRoute(busRoute);
        assertEquals(2, network.getRoutes().size());
        assertTrue(network.getRoutes().contains(busRoute));
        //add routes from a valid text file and check if they are added
        network = new Network ( "validFromSpec.txt" );
        assertEquals ( 2, network.getRoutes().size());
        //checking if a when adding a decoded is on this network
        network.addStop(stop);
        network.addStop(stop1);
        network.addStop(stop2);
        Route route = busRoute.decode("train,red,1:stop0|stop2|stop1",
                network.getStops());
        network.addRoute(route);
        assertEquals ( 3, network.getRoutes().size());
        assertTrue(network.getRoutes().contains(route));

    }

    @Test
    public void getStops() throws IOException, TransportFormatException {
        Network network = new Network();
        //check first if this network has any route
        assertEquals(0, network.getStops().size());
        //check for null if has any effect.
        network.addStop(null);
        assertEquals(0, network.getStops().size());
        //add one route and check the size
        network.addStop(stop);
        assertEquals( 1,network.getStops().size());
        //add another route, check if it exist as well as the size
        network.addStop(stop1);
        assertEquals(2, network.getStops().size());
        assertTrue(network.getStops().contains(stop1));
        //add routes from a valid text file and check if they are added
        network = new Network ( "validFromSpec.txt" );
        assertEquals ( 4, network.getStops().size());
        //checking if given decoded stop contained in the returned stops
        Stop stop3 = stop.decode ( "UQ:3:8" );
        network.addStop(stop3);
        assertTrue ( network.getStops().contains(stop3));
    }

    @Test
    public void getVehicles() throws IOException, TransportFormatException{
        //start with a constructor with no argument and test if it has any veh..
        network = new Network();
        assertEquals(0, network.getVehicles().size());
        //Adding a null value
        network.addVehicle(null);
        assertEquals(0, network.getVehicles().size());
        //adding a vehicle and check if it is added
        trainRoute = new TrainRoute("kalala", 3);
        train = new Train(8,200,trainRoute,12);
        network.addVehicle(train);
        assertEquals(1, network.getVehicles().size());

        assertEquals("train,8,200,3,12",
                network.getVehicles().get(0).encode());

        assertTrue(network.getVehicles().contains(train));
        //check for non existing vehicle on the network
        assertFalse(network.getVehicles().contains(bus));
        //try to add a vehicle with passenger in it and add it to this net..
        try {
            bus.addPassenger(passenger);
            bus.addPassenger(concessionPassenger);
        } catch (Exception e) {

        }
        network.addVehicle(bus);
        assertEquals(2, network.getVehicles().size());
        //finally use a valid text file to check if the vehicle are added

        Network net = new Network("validFromSpec.txt");

        assertEquals(5, (net.getVehicles().size() +
                network.getVehicles().size()));
        //adding a decoded vehicle and see if it is being return
        network.addRoute(busRoute);
        network.addRoute(trainRoute);
        network.addRoute(ferryRoute);
        PublicTransport bus3  = bus.decode ( "bus,78,6,2,AB23",
                network.getRoutes());

        network.addVehicle(bus3);
        assertEquals( 3, network.getVehicles().size());
    }

    @Test
    public void save() throws IOException, TransportFormatException{

        //testing for null; nothing happens. No file is created when content
        //from a valid file are added
        network = new Network ( "validEmptyRoute.txt" );
        network.save(null);

        //Create a file from an empty constructor and see if it can be
        //overwritten by an invalid file contents
        //a file is created with 0s from empty Const.., no overwritten occurred
        Network network1 = new Network();
        network1.save("pax");
        try {
            network1 = new Network( "invalidBlankLines.txt" );
            network1.save("pax");
        } catch (Exception e){

        }
        //saving a file with no vehicle and routes
        network1.addStop(stop);
        network1.addStop(stop1);
        network1.addStop(stop2);
        network1.save("onlyStop");
        //saving stop and route only
        network1.addRoute(busRoute);
        network1.addRoute(trainRoute);
        network1.addRoute(ferryRoute);
        network1.save("stopsAndRoutes");

        //using a valid file name and save the contents to a file named pac.
        network = new Network ( "validEmptyRoute.txt" );

        network.save ( "pac" );

        //using the second valid file and see if the result is created on new
        //file.
        network = new Network ( "validFromSpec.txt" );
        network.save ( "final" );

        //saving stops and vehicle only
        Network net = new Network();
        net.addStops(network.getStops());
        for(PublicTransport pt: network.getVehicles()){
            net.addVehicle(pt);
        }
        net.save("stopsAndVehicle");

        //Saving a route only
        Network netwk = new Network();
        for(Route route: network.getRoutes()){
            netwk.addRoute(route);
        }
        netwk.save("routeOnly");

        //saving routes and vehicles
        for(PublicTransport pt: network.getVehicles()){
            netwk.addVehicle(pt);
        }
        netwk.save("routeAndVehicle");

        //saving vehicle only
        Network network2 = new Network();
        for(PublicTransport pt: network.getVehicles()){
            network2.addVehicle(pt);
        }
        network2.save("vehicleOnly");
    }
}

