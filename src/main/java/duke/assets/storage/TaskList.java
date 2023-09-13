package duke.assets.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import duke.assets.tasks.TaskAbstract;
import duke.dukeexceptions.StateCannotBeAlteredException;

/**
 * Represents a list of tasks
 */
public class TaskList {
    private static final String HORIZONTAL = "------------------------------------------------------------"
            + "---------------------------";
    private static final String UNHANDLED_EXCEPTION_STRING = "Error: unexpected uncaught exception in task list";
    private final ArrayList<TaskAbstract> taskList;
    private int numberOfTasks;

    /**
     * Constructs an empty task list
     */
    public TaskList() {
        this.taskList = new ArrayList<TaskAbstract>();
        this.numberOfTasks = 0;
    }

    /**
     * Adds a task to the task list
     *
     * @param newTask the task to be added
     */
    public void addTask(TaskAbstract newTask) {
        this.taskList.add(newTask);
        this.numberOfTasks++;
    }

    /**
     * Marks a task at the specified index as complete
     *
     * @param index the index of the task to be marked
     * @return appropriate chatbot response to request
     */
    public String markTaskAt(int index) {
        if (index < 0 || index >= this.numberOfTasks) {
            if (this.numberOfTasks == 0) {
                return "Please add at least one task to your list first :)";
            } else {
                return ("Ensure the index is of in the range 1 - " + this.numberOfTasks);
            }
        } else {
            try {
                this.taskList.get(index).completeTask();
                return "Great, I'll mark the task!";
            } catch (StateCannotBeAlteredException exp) {
                return "Task is already complete :-)";
            } catch (Exception e) { // Other unhandled exceptions that have not been considered
                return UNHANDLED_EXCEPTION_STRING;
            }
        }
    }

    /**
     * Marks a task at the specified index as incomplete
     *
     * @param index the index of the task to be unmarked
     * @return appropriate chatbot response string
     */
    public String unmarkTaskAt(int index) {
        if (index < 0 || index >= this.numberOfTasks) {
            if (this.numberOfTasks == 0) {
                return "Please add at least one task to your list first.";
            } else {
                return ("Ensure the index is of in the range 1 - " + this.numberOfTasks);
            }
        }
        try {
            this.taskList.get(index).undo();
            return "I'll unmark it for now but do remember to complete it!";
        } catch (StateCannotBeAlteredException exp) {
            return "Task is already incomplete :-)";
        } catch (Exception e) { // Other unhandled exceptions that have not been considered
            return UNHANDLED_EXCEPTION_STRING;
        }
    }

    /**
     * Deletes a task at the specified index from the task list
     *
     * @param index the index of the task to be deleted
     * @return appropriate chatbot response string
     */
    public String deleteTaskAt(int index) {
        if (index < 0 || index >= this.numberOfTasks) {
            if (this.numberOfTasks == 0) {
                return "Can't delete from an empty list :(";
            } else {
                return ("Ensure the index is of in the range 1 - " + this.numberOfTasks);
            }
        } else {
            this.taskList.remove(index);
            this.numberOfTasks--;
            return "Deleted!";
        }
    }

    /**
     * Writes the task list to a file
     */
    public void writeToFile() {
        try {
            File myFile = new File("./src/main/java/duke/data/duke.txt");
            FileWriter fw = new FileWriter(myFile);
            PrintWriter pw = new PrintWriter(fw);
            for (TaskAbstract t : this.taskList) {
                pw.println(t.saveToTextFormat());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("ChadGPT: Please check if your I/O is working as intended.");
        }
    }

    /**
     * Returns the task list in text format for saving to file
     *
     * @return the task list in text format for saving to file
     */
    public String saveToFileFormat() {
        String returnString = "";
        for (TaskAbstract t : taskList) {
            returnString += t.saveToTextFormat();
            returnString += "\n";
        }
        return returnString;
    }

    /**
     * Gets the status of all tasks in the task list
     *
     * @return all the status of the tasks compiled into a single string
     */
    public String listTasks() {
        if (numberOfTasks == 0) {
            return "No tasks to list, did you forget to add them perhaps?";
        }
        String baseString = "Here you go!";
        int counter = 0;
        for (TaskAbstract t : this.taskList) {
            baseString += ("\n    " + ++counter + ". " + t.getStatus());
        }
        return baseString;
    }

    /**
     * Clears all tasks from the task list
     */
    public void clearList() {
        this.taskList.clear();
        this.numberOfTasks = 0;
    }

    /**
     * Find and prints the tasks that contain the given token in their task descriptions
     *
     * @param token token to be found in task description
     * @return list of task matching the user input token as a string
     */
    public String find(String token) {
        boolean hasMatch = false;
        String baseString = "No matches unfortunately :(";

        // Check if there is at least 1 task with description matching to token
        for (TaskAbstract t : this.taskList) {
            if (t.hasToken(token)) {
                baseString = "Found them!";
                hasMatch = true;
                break;
            }
        }

        if (hasMatch) {
            int counter = 0;
            for (TaskAbstract t : this.taskList) {
                if (t.hasToken(token)) {
                    baseString += ("\n    " + ++counter + ". " + t.getStatus());
                }
            }
        }
        return baseString;
    }
}
