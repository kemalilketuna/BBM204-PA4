import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class UrbanTransportationApp implements Serializable {
    static final long serialVersionUID = 99L;
    
    public HyperloopTrainNetwork readHyperloopTrainNetwork(String filename) {
        HyperloopTrainNetwork hyperloopTrainNetwork = new HyperloopTrainNetwork();
        hyperloopTrainNetwork.readInput(filename);
        return hyperloopTrainNetwork;
    }

    private double calculateDistance(Station station1, Station station2) {
        double dst =  Math.sqrt(Math.pow(station1.coordinates.x - station2.coordinates.x, 2) + Math.pow(station1.coordinates.y - station2.coordinates.y, 2));
        return dst;        
    }

    /**
     * Function calculate the fastest route from the user's desired starting point to 
     * the desired destination point, taking into consideration the hyperloop train
     * network. 
     * @return List of RouteDirection instances
     */
    public List<RouteDirection> getFastestRouteDirections(HyperloopTrainNetwork network) {
        List<RouteDirection> routeDirections = new ArrayList<>();

        int n = 2;
        for (TrainLine trainLine: network.lines) {
            n += trainLine.trainLineStations.size();
        }

        Integer[] path = new Integer[n];
        List<Station> stations = new ArrayList<>();
        stations.add(network.startPoint);
        path[0] = 0;
        int label = 1;
        int index = 1;
        for(TrainLine trainLine: network.lines){
            stations.addAll(trainLine.trainLineStations);
            for (int i = 0; i < trainLine.trainLineStations.size(); i++) {
                path[index] = label;
                index++;
            }
            label++;    
        }
        stations.add(network.destinationPoint);
        path[n-1] = label;

        // dijkstra algorithm
        
        double[] weight = new double[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            weight[i] = Integer.MAX_VALUE;
            prev[i] = -1;
            visited[i] = false;
        }
        weight[0] = 0;
        // Priority Queue (distance, station index)
        PriorityQueue<double[]> pq = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
        pq.add(new double[]{0, 0});

        while (!pq.isEmpty()) {
            double[] cur = pq.poll();
            int u = (int) cur[1];
            if (visited[u]) {
                continue;
            }
            visited[u] = true;
            for (int v = 0; v < n; v++) {
                if (u == v) {
                    continue;
                }
                double time = 0;
                double distance = calculateDistance(stations.get(u), stations.get(v));
                if (path[u] != path[v]) {
                    time = distance / network.averageWalkingSpeed;
                } else {
                    time = distance / network.averageTrainSpeed;
                }
                time = new BigDecimal(time).setScale(2, RoundingMode.HALF_UP).doubleValue();
                if (weight[v] > weight[u] + time) {
                    weight[v] = weight[u] + time;
                    prev[v] = u;
                    pq.add(new double[]{weight[v], v});
                }
            }
        }

        List<RouteDirection> reversed = new ArrayList<>();
        int endNode = n-1;
        int startNode = prev[endNode];
        while (startNode != -1) {
            if (path[startNode] != path[endNode]) {
                RouteDirection routeDirection = new RouteDirection(stations.get(startNode).description, stations.get(endNode).description, weight[endNode] - weight[startNode], false);
                reversed.add(routeDirection);
            } else {
                RouteDirection routeDirection = new RouteDirection(stations.get(startNode).description, stations.get(endNode).description, weight[endNode] - weight[startNode], true);
                reversed.add(routeDirection);
            }
            endNode = startNode;
            startNode = prev[endNode];
        }
        
        for (int i = reversed.size() - 1; i >= 0; i--) {
            routeDirections.add(reversed.get(i));
        }

        return routeDirections;
    }

    /**
     * Function to print the route directions to STDOUT
     */
    public void printRouteDirections(List<RouteDirection> directions) {
        double totalDuration = 0;
        for (RouteDirection direction: directions) {
            totalDuration += direction.duration;
        }

        System.out.println("The fastest route takes " + Math.round(totalDuration) + " minute(s).");
        System.out.println("Directions");
        System.out.println("----------");
        int i = 1;
        for (RouteDirection direction: directions) {
            // direction.duration round to 2 decimal places
            double duration = new BigDecimal(direction.duration).setScale(2, RoundingMode.HALF_UP).doubleValue();

            if (direction.trainRide) {
                System.out.println(i + ". Get on the train from \"" + direction.startStationName + "\" to \"" + direction.endStationName + "\" for " + String.format("%.2f", duration) + " minutes.");
            } else {
                System.out.println(i + ". Walk from \"" + direction.startStationName + "\" to \"" + direction.endStationName + "\" for " + String.format("%.2f", duration) + " minutes.");
            }
            i++;
        }
    }
}