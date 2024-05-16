import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.ArrayList;
import java.util.List;

public class UrbanInfrastructureDevelopment implements Serializable {
    static final long serialVersionUID = 88L;

    /**
     * Given a list of Project objects, prints the schedule of each of them.
     * Uses getEarliestSchedule() and printSchedule() methods of the current project to print its schedule.
     * @param projectList a list of Project objects
     */
    public void printSchedule(List<Project> projectList) {
        for(Project project : projectList){
            project.printSchedule(project.getEarliestSchedule());
        }
    }

    /**
     * Reads an XML file and returns a list of Project objects.
     *
     * @param filename the input XML file
     * @return a list of Project objects
     */
    public List<Project> readXML(String filename) {
        try{
            List<Project> projectList = new ArrayList<>();
            File xmlFile = new File(filename);
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList projectNodeList = doc.getElementsByTagName("Project");

            for(int i = 0; i < projectNodeList.getLength(); i++){
                Element projectNode = (Element) projectNodeList.item(i);
                String projectName = projectNode.getElementsByTagName("Name").item(0).getTextContent();
                List<Task> taskList = new ArrayList<>();
                NodeList taskNodeList = projectNode.getElementsByTagName("Task");
                for(int j = 0; j < taskNodeList.getLength(); j++){
                    Element taskNode = (Element) taskNodeList.item(j);
                    String taskDescription = taskNode.getElementsByTagName("Description").item(0).getTextContent();
                    int taskDuration = Integer.parseInt(taskNode.getElementsByTagName("Duration").item(0).getTextContent());
                    List<Integer> dependencyList = new ArrayList<>();
                    Node dependdeciesNode = taskNode.getElementsByTagName("Dependencies").item(0);
                    NodeList dependencyNodeList = ((Element) dependdeciesNode).getElementsByTagName("DependsOnTaskID");
                    for(int k = 0; k < dependencyNodeList.getLength(); k++){
                        Element dependencyNode = (Element) dependencyNodeList.item(k);
                        // print dependencyNode.getTextContent()
                        int dependencyID = Integer.parseInt(dependencyNode.getTextContent());
                        dependencyList.add(dependencyID);
                    }
                    Task task = new Task(j, taskDescription, taskDuration, dependencyList);
                    taskList.add(task);
                }
                Project project = new Project(projectName, taskList);
                projectList.add(project);
            }
            return projectList;
        
        }catch(Exception e){
            throw new IllegalArgumentException("Error reading XML file");
        }
    }
}
