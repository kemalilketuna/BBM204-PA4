import java.io.Serializable;
import java.util.*;

public class Project implements Serializable {
    static final long serialVersionUID = 33L;
    private final String name;
    private final List<Task> tasks;

    public Project(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    /**
     * @return the total duration of the project in days
     */
    public int getProjectDuration() {
        int projectDuration = 0;

        int startTimes[] = getEarliestSchedule();
        for (int i = 0; i < tasks.size(); i++) {
            projectDuration = Math.max(projectDuration, startTimes[i] + tasks.get(i).getDuration());
        }

        return projectDuration;
    }

    /**
     * Schedule all tasks within this project such that they will be completed as early as possible.
     *
     * @return An integer array consisting of the earliest start days for each task.
     */
    public int[] getEarliestSchedule() {
        int n = tasks.size();
        int[] schedule = new int[n];
        int[] visited = new int[n];

        List<Integer> topSort = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (visited[i] == 0) {
                Stack<Integer> stack = new Stack<>();
                stack.push(i);
                while (!stack.isEmpty()) {
                    int u = stack.peek();
                    visited[u] = 1;
                    boolean hasUnvisited = false;
                    for (int v : tasks.get(u).getDependencies()) {
                        if (visited[v] == 0) {
                            stack.push(v);
                            hasUnvisited = true;
                        }
                    }
                    if (!hasUnvisited) {
                        topSort.add(stack.pop());
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            int u = topSort.get(i);
            for (int v : tasks.get(u).getDependencies()) {
                schedule[u] = Math.max(schedule[u], schedule[v] + tasks.get(v).getDuration());
            }
        }
        return schedule;
    }

    public static void printlnDash(int limit, char symbol) {
        for (int i = 0; i < limit; i++) System.out.print(symbol);
        System.out.println();
    }

    /**
     * Some free code here. YAAAY! 
     */
    public void printSchedule(int[] schedule) {
        int limit = 65;
        char symbol = '-';
        printlnDash(limit, symbol);
        System.out.println(String.format("Project name: %s", name));
        printlnDash(limit, symbol);

        // Print header
        System.out.println(String.format("%-10s%-45s%-7s%-5s","Task ID","Description","Start","End"));
        printlnDash(limit, symbol);
        for (int i = 0; i < schedule.length; i++) {
            Task t = tasks.get(i);
            System.out.println(String.format("%-10d%-45s%-7d%-5d", i, t.getDescription(), schedule[i], schedule[i]+t.getDuration()));
        }
        printlnDash(limit, symbol);
        System.out.println(String.format("Project will be completed in %d days.", getProjectDuration()));
        printlnDash(limit, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;

        int equal = 0;

        for (Task otherTask : ((Project) o).tasks) {
            if (tasks.stream().anyMatch(t -> t.equals(otherTask))) {
                equal++;
            }
        }

        return name.equals(project.name) && equal == tasks.size();
    }

}
