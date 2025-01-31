package duke.assets.commands;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duke.assets.storage.TaskList;
import duke.assets.tasks.Event;
import duke.assets.tasks.TaskAbstract;

/**
 * Represents a command to create a new event task
 */
public class CreateEventCommand extends CommandAbstract {
    // Constants
    private static final String INPUT_EVENT_REGEX_STRING = String.format("^event .+ /from %s( | %s )/to %s($| %s$)",
            VALID_DATE_REGEX_STRING, VALID_TIME_REGEX_STRING, VALID_DATE_REGEX_STRING, VALID_TIME_REGEX_STRING);
    // Non-Constants
    private final boolean isDone;

    /**
     * Constructs a new CreateEventCommand object with the given input command string and completion flag
     *
     * @param input the input command string
     * @param isDone a flag indicating whether the new task is already completed
     */
    public CreateEventCommand(String input, boolean isDone) {
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
        Pattern commandRegex = Pattern.compile(INPUT_EVENT_REGEX_STRING, Pattern.CASE_INSENSITIVE);
        Matcher inputMatcher = commandRegex.matcher(this.input);
        if (!inputMatcher.find()) {
            findException();
            return false;
        }
        return true;
    }

    /**
     * Completes the operation specified by the input command on the specified task list
     *
     * @param tasklist the task list to operate on
     * @return appropriate chatbot response to user query, or UNHANDLED_EXCEPTION_STRING for unhandled edge cases
     */
    @Override
    protected String completeOperation(TaskList tasklist) {
        String information = this.input.split(" /from ")[0].split("^(?i)(event)\\s")[1];
        String allDateAndTime = this.input.split(" /from ")[1];
        String startDateAndTime = allDateAndTime.split(" /to ")[0];
        String endDateAndTime = allDateAndTime.split(" /to ")[1];
        TaskAbstract newTask = new Event(information, startDateAndTime, endDateAndTime);
        if (this.isDone) {
            newTask.completeNewTask();
        }
        tasklist.addTask(newTask);
        return "No problem! I have added the event: \"" + information + "\" to the list.";
    }

    /**
     * Handles exceptions that occur when validating the input command and returns the appropriate chatbot
     * response as a string
     *
     * @return string of appropriate bot response, UNHANDLED_EXCEPTION_STRING for any unhandled edge cases
     */
    protected String findException() {
        return checkInformationPresent();
    }

    /**
     * Check if the user included information about the event task
     *
     * @return appropriate debugging chatbot message
     */
    private String checkInformationPresent() {
        String[] delimitedBySlash = this.input.split("/");

        try { // Checks if user included information about the event task
            String information = delimitedBySlash[0].split(" ")[1];
        } catch (IndexOutOfBoundsException indexExcept) {
            return "Please include information about the task you would like to add.";
        }
        return checkDatePresent();
    }

    /**
     * Check if the user included the date in the right format
     *
     * @return appropriate debugging chatbot message
     */
    private String checkDatePresent() {
        String[] delimitedBySlash = this.input.split("/");

        try { // Checks if users have included the start and end dates after /from and /to respectively
            String startDate = delimitedBySlash[1].substring(5, delimitedBySlash[1].length() - 1);
            String endDate = delimitedBySlash[2].substring(3);
        } catch (StringIndexOutOfBoundsException stringExcept) {
            return "Please ensure that you have included the start and end dates.";
        } catch (IndexOutOfBoundsException indexExcept) {
            return "Please verify you have included the start date after /from and "
                    + "end date after /to commands";
        }
        return checkDateTimeFormatting();
    }

    /**
     * Check if the user input format is of the right date and time format
     *
     * @return appropriate debugging chatbot message
     */
    private String checkDateTimeFormatting() {
        try { // Checks if user has input all dates and time in the correct format
            Pattern datePattern = Pattern.compile(" " + VALID_DATE_REGEX_STRING + "($| )");
            Matcher dateMatcher = datePattern.matcher(this.input);
            if (dateMatcher.results().count() < 2) {
                throw new DateTimeException("Invalid date");
            }
            String dates = this.input.split(" /from ")[1];
            String[] startDateArr = dates.split(" /to ")[0].split(" ");
            String startDate = startDateArr[0];
            String startDateYear = startDate.substring(0, 4);
            String startDateMonth = startDate.substring(5, 7);
            String startDateDay = startDate.substring(8, 10);
            LocalDate startDateObj = LocalDate.parse(String.format("%s-%s-%s", startDateYear,
                    startDateMonth, startDateDay));
            if (startDateArr.length > 1) {
                Integer.parseInt(startDateArr[1]);
                LocalTime startTimeObj = LocalTime.parse(startDateArr[1].substring(0, 2) + ":"
                        + startDateArr[1].substring(2));
            }
        } catch (NumberFormatException numberExcept) {
            return "Please ensure the time of your deadline is in numerical format.";
        } catch (IndexOutOfBoundsException | IllegalArgumentException indexExcept) {
            return "Ensure that deadline date follows the following format: "
                    + "yyyy-mm-dd or yyyy/mm/dd.";
        } catch (DateTimeException dateTimeException) {
            return "Please input a valid date and time";
        }
        return checkEndDateTimeFormatting();
    }

    /**
     * Processes the event end date and time input to check if it follows the appropriate formatting
     *
     * @return appropriate debugging chatbot message, or unhandled exception message for any edge cases not caught
     */
    private String checkEndDateTimeFormatting() {
        try {
            String dates = this.input.split(" /from ")[1];
            String[] endDateArr = dates.split(" /to ")[1].split(" ");
            String endDate = endDateArr[0];
            String endDateYear = endDate.substring(0, 4);
            String endDateMonth = endDate.substring(5, 7);
            String endDateDay = endDate.substring(8, 10);
            LocalDate endDateObj = LocalDate.parse(String.format("%s-%s-%s", endDateYear,
                    endDateMonth, endDateDay));
            if (endDateArr.length > 1) {
                Integer.parseInt(endDateArr[1]);
                LocalTime endDateTime = LocalTime.parse(endDateArr[1].substring(0, 2) + ":"
                        + endDateArr[1].substring(2));
            }
        } catch (NumberFormatException numberExcept) {
            return "Please ensure the time of your deadline is in numerical format.";
        } catch (IndexOutOfBoundsException | IllegalArgumentException indexExcept) {
            return "Ensure that deadline date follows the following format: "
                    + "yyyy-mm-dd or yyyy/mm/dd.";
        } catch (DateTimeException dateTimeException) {
            return "Please input a valid date and time";
        }
        // If exception is not caught by here, will flag a standard error string
        return UNHANDLED_EXCEPTION_STRING;
    }
}

