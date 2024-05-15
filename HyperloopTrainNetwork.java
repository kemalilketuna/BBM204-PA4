import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyperloopTrainNetwork implements Serializable {
    static final long serialVersionUID = 11L;
    public double averageTrainSpeed;
    public final double averageWalkingSpeed = 1000 / 6.0;
    public int numTrainLines;
    public Station startPoint;
    public Station destinationPoint;
    public List<TrainLine> lines;

    /**
     * Method with a Regular Expression to extract integer numbers from the fileContent
     * @return the result as int
     */
    public int getIntVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract string constants from the fileContent
     * @return the result as String
     */
    public String getStringVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*\"(.*)\"");
        Matcher m = p.matcher(fileContent);
        m.find();
        return m.group(1);
    }

    /**
     * Write the necessary Regular Expression to extract floating point numbers from the fileContent
     * Your regular expression should support floating point numbers with an arbitrary number of
     * decimals or without any (e.g. 5, 5.2, 5.02, 5.0002, etc.).
     * @return the result as Double
     */
    public Double getDoubleVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+(?:\\.[0-9]+)?)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Double.parseDouble(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract a Point object from the fileContent
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     * destination_point =(10000 ,1000)
     * @return the result as a Point object
     */
    public Point getPointVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*\\([\\t ]*([0-9]+)[\\t ]*,[\\t ]*([0-9]+)[\\t ]*\\)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
    } 

    /**
     * Function to extract the train lines from the fileContent by reading train line names and their 
     * respective stations.
     * @return List of TrainLine instances
     */
    public List<TrainLine> getTrainLines(String fileContent) {
        List<TrainLine> trainLines = new ArrayList<>();
        String[] lines = fileContent.split("\n");
        for (int i = 0; i < lines.length; i+=2) {
            String lineName = getStringVar("train_line_name", lines[i]);
            String secondPart = "=(.*)$";
            Pattern p2 = Pattern.compile(secondPart);
            Matcher m2 = p2.matcher(lines[i+1]);
            m2.find();
            String stations = m2.group(1);
            String coordinateRegex = "\\([\\t ]*([0-9]+)[\\t ]*,[\\t ]*([0-9]+)[\\t ]*\\)";
            Pattern p = Pattern.compile(coordinateRegex);
            Matcher m = p.matcher(stations);
            List<Station> stationList = new ArrayList<>();
            int stationCount = 1;
            while (m.find()) {
                Point point = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                stationList.add(new Station(point, lineName + " Line Station " + String.valueOf(stationCount)));
                stationCount++;
            }
            trainLines.add(new TrainLine(lineName, stationList));
        }
        return trainLines;
    }

    /**
     * Function to populate the given instance variables of this class by calling the functions above.
     */
    public void readInput(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String row = reader.readLine();
            numTrainLines = getIntVar("num_train_lines", row);
            row = reader.readLine();
            startPoint = new Station(getPointVar("starting_point", row), "Starting Point");
            row = reader.readLine();
            destinationPoint = new Station(getPointVar("destination_point", row), "Final Destination");
            row = reader.readLine();
            averageTrainSpeed = getDoubleVar("average_train_speed", row);
            String fileContent = "";
            while ((row = reader.readLine()) != null){
                fileContent += row + "\n";
            }
            lines = getTrainLines(fileContent);

            reader.close();
        }catch(Exception e){
            throw new IllegalArgumentException("Error reading input file");
        }

    }
}