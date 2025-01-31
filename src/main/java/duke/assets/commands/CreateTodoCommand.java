package duke.assets.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duke.assets.storage.TaskList;
import duke.assets.tasks.TaskAbstract;
import duke.assets.tasks.Todo;

/**
 * Represents a command to create a new todo task
 */
public class CreateTodoCommand extends CommandAbstract {
    private final boolean isDone;

    /**
     * Constructs a new CreateTodoCommand object with the given input command string and completion flag
     *
     * @param input the input command string
     * @param isDone a flag indicating whether the new task is already completed
     */
    public CreateTodoCommand(String input, boolean isDone) {
        super(input);
        this.isDone = isDone;
    }

    /**
     * Determines whether the input command is valid for the specified task list
     *
     * @param tasklist the task list to validate against
     * @return true if the input command is valid, false otherwise
     */
    @Override
    protected boolean isValid(TaskList tasklist) {
        return this.isValid();
    }

    /**
     * Determines whether the input command is valid
     *
     * @return true if the input command is valid, false otherwise
     */
    private boolean isValid() {
        Pattern commandRegex = Pattern.compile("^todo .+", Pattern.CASE_INSENSITIVE);
        Matcher inputMatcher = commandRegex.matcher(this.input);
        return inputMatcher.find();
    }

    /**
     * Completes the operation specified by the input command on the specified task list
     *
     * @param tasklist the task list to operate on
     * @return string of appropriate bot response, UNHANDLED_EXCEPTION_STRING for any unhandled edge cases
     */
    @Override
    protected String completeOperation(TaskList tasklist) {
        String information = this.input.split("^((?i)(todo))\\s")[1];
        TaskAbstract newTask = new Todo(information);
        if (this.isDone) {
            newTask.completeNewTask();
        }
        tasklist.addTask(newTask);
        return "No problem! Just remember to do your task before the deadline.";
    }

    /**
     * Handles exceptions that occur when validating the input command and returns the appropriate chatbot
     * response as a string
     *
     * @return string of appropriate bot response, UNHANDLED_EXCEPTION_STRING for any unhandled edge cases
     */
    @Override
    protected String findException() {
        Pattern commandRegex = Pattern.compile("^todo .+", Pattern.CASE_INSENSITIVE);
        Matcher inputMatcher = commandRegex.matcher(this.input);
        if (!inputMatcher.find()) {
            return "Please provide a description about your task.";
        }
        return UNHANDLED_EXCEPTION_STRING;
    }
}
