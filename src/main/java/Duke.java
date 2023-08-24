import Tasks.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

enum taskTypes {
    todo,
    deadline,
    event
}

public class Duke {
    static final String logo = "\n   _____ _    _          _____   _____ _____ _______ \n" +
            "  / ____| |  | |   /\\   |  __ \\ / ____|  __ \\__   __|\n" +
            " | |    | |__| |  /  \\  | |  | | |  __| |__) | | |   \n" +
            " | |    |  __  | / /\\ \\ | |  | | | |_ |  ___/  | |   \n" +
            " | |____| |  | |/ ____ \\| |__| | |__| | |      | |   \n" +
            "  \\_____|_|  |_/_/    \\_\\_____/ \\_____|_|      |_|   \n";
    static final String horizontal = "----------------------------------------------------------------------------" +
            "-----------";
    static List<Task> taskList = new ArrayList<Task>();
    static int taskCounter = 0;
    enum validCommands {
        bye,
        list,
        mark,
        unmark,
        deadline,
        event,
        todo,
        delete
    };

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println(horizontal + logo + horizontal);
        System.out.println("ChadGPT: Welcome to ChadGPT, What would you like to do today?\n" + horizontal);
        System.out.print("User: ");
        while (!sc.hasNext("bye")) {
            String nextLine = sc.nextLine();
            if (isValidCommand(nextLine)) {
                passCommand(nextLine);
            }
            System.out.print(horizontal + "\nUser: ");
        }
        sc.close();
        System.out.print("ChadGPT: Bye. Hope to see you again soon!\n" + horizontal);
    }

    private static void passCommand(String nextLine) {
        String[] strArray = nextLine.split(" ");
        String command = strArray[0];
        if (command.equals("list")) {
            System.out.println("ChadGPT: Here are your tasks: ");
            int counter = 0;
            for (Task t : taskList) {
                System.out.print("    " + ++counter + ". ");
                t.printStatus();
            }
        } else if (command.equals("mark")) {
            int index = Integer.parseInt(strArray[1]) - 1;
            if (taskList.get(index).isComplete()) {
                System.out.println("ChadGPT: Oops, the task is already complete.");
            } else {
                taskList.get(index).completeTask();
                System.out.println("ChadGPT: Nice! I'll mark the task as done: ");
                taskList.get(index).printStatus();
            }
        } else if (command.equals("unmark")) {
            int index = Integer.parseInt(strArray[1]) - 1;
            if (!taskList.get(index).isComplete()) {
                System.out.println("ChadGPT: Oops, the task is currently incomplete.");
            } else {
                taskList.get(index).undo();
                System.out.println("ChadGPT: No problem, I'll mark this task as not done yet: ");
                taskList.get(index).printStatus();
            }
        } else if (command.equals("delete")) {
            int index = Integer.parseInt(strArray[1]) - 1;
            System.out.println("ChadGPT: No problem, I'll remove the task from your task list: ");
            taskList.remove(index).printStatus();
            taskCounter--;
        } else {
            Task newTask = createTask(nextLine, strArray);
            System.out.println("ChadGPT: added task '" + newTask.toString() + "'");
            System.out.println("You now have " + taskCounter + " tasks in the list.");
            taskList.add(newTask);
        }
    }

   private static Task createTask(String nextLine, String[] strArr) {
        String[] delimited = nextLine.split("/");
        switch(strArr[0].toLowerCase()) {
            case "todo":
                taskCounter++;
                return new ToDo(nextLine.substring(5));
            case "deadline":
                taskCounter++;
                return new Deadline(delimited[0].substring(9, delimited[0].length() - 1),
                        delimited[1].substring(3));
            case "event":
                taskCounter++;
                return new Event(delimited[0].substring(6, delimited[0].length() - 1),
                        delimited[1].substring(5, delimited[1].length() - 1),
                        delimited[2].substring(3));
        }
        throw new IllegalArgumentException("Invalid task type");
   }

   private static boolean isValidCommand(String nextLine) {
        String[] delimitedBySpace = nextLine.split(" ");
        String[] delimitedBySlash = nextLine.split("/");

        try {
            String command = delimitedBySpace[0].toLowerCase();
            validCommands.valueOf(command);
        } catch (IndexOutOfBoundsException indexExcept) {
            System.out.println("ChadGPT: Please input a valid command.");
            return false;
        } catch (IllegalArgumentException argExcept) {
            System.out.println("ChadGPT: Sorry I don't understand this command :-(");
            return false;
        }

        switch (delimitedBySpace[0].toLowerCase()) {
            case "mark":
            case "delete":
            case "unmark":
                try {
                    int location = Integer.parseInt(delimitedBySpace[1]);
                    if (location > taskCounter) {
                        System.out.println(taskCounter > 0 ? "ChadGPT: Please input a " +
                                "valid task index. You may use the command \"list\" to get " +
                                "the task indexes." : "ChadGPT: Please add a task first.");
                        return false;
                    }
                } catch (IndexOutOfBoundsException indexExcept) {
                    System.out.println("ChadGPT: Please input index of task you would like to alter.");
                    return false;
                } catch (IllegalArgumentException argExcept) {
                    System.out.println("ChadGPT: Please input an integer for the index of the task you would like to alter.");
                    return false;
                }
                return true;
            case "todo":
                try {
                    String information = delimitedBySpace[1];
                } catch (IndexOutOfBoundsException indexExcept) {
                    System.out.println("ChadGPT: Please include information about the task you would like to add.");
                    return false;
                }
                return true;
            case "deadline":
                try {
                    String information = delimitedBySpace[1].split(" ")[1];
                } catch (IndexOutOfBoundsException indexExcept) {
                    System.out.println("ChadGPT: Please include information about the task you would like to add.");
                    return false;
                }
                try {
                    String endDate = delimitedBySlash[1].substring(3);
                } catch (StringIndexOutOfBoundsException stringExcept) {
                    System.out.println("ChadGPT: Please ensure your deadline date is included.");
                    return false;
                } catch (IndexOutOfBoundsException indexExcept) {
                    System.out.println("ChadGPT: Please include the deadline date of your task after /by command.");
                    return false;
                }
                return true;
            case "event":
                try {
                    String information = delimitedBySlash[0].split(" ")[1];
                } catch (IndexOutOfBoundsException indexExcept) {
                    System.out.println("ChadGPT: Please include information about the task you would like to add.");
                    return false;
                }
                try {
                    String startDate = delimitedBySlash[1].substring(5, delimitedBySlash[1].length() - 1);
                    String endDate = delimitedBySlash[2].substring(3);
                } catch (StringIndexOutOfBoundsException stringExcept) {
                    System.out.println("ChadGPT: Please ensure that you have included the start and end dates.");
                    return false;
                } catch (IndexOutOfBoundsException indexExcept) {
                    System.out.println("ChadGPT: Please verify you have included the start date after /from and " +
                            "end date after /to commands");
                    return false;
                }
        }
        return true;
   }
}