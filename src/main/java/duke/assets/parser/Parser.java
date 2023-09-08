package duke.assets.parser;

import duke.assets.commands.*;
import duke.assets.storage.TaskList;
import duke.dukeexceptions.CorruptDataException;
import duke.dukeexceptions.InvalidCommandException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Parser {
    private static final String GENERAL_DATA_REGEX_STRING = "^[TDE] \\| [01] \\| .+";
    private static final String DEADLINE_REGEX_STRING = "^D \\| [01] \\| .+ \\| \\d{4}-\\d{2}-\\d{2}" +
            "($| [0-2][0-9][0-5][0-9]$)";
    private static final String EVENT_REGEX_STRING = "^E \\| [01] \\| .+ \\| \\d{4}-\\d{2}-\\d{2}" +
            "( [0-2][0-9][0-5][0-9] | )- \\d{4}-\\d{2}-\\d{2}($| [0-2][0-9][0-5][0-9]$)";

    private CommandAbstract createUserCommand(String input) throws InvalidCommandException {
        try {
            String command = input.split(" ")[0];
        } catch (IndexOutOfBoundsException exp) {
            throw new InvalidCommandException(input);
        }

        switch (input.split(" ")[0].toLowerCase()) {
            case "bye":
                return new ByeCommand(input);
            case "list":
                return new ListCommand(input);
            case "mark":
                return new MarkCommand(input);
            case "unmark":
                return new UnmarkCommand(input);
            case "delete":
                return new DeleteCommand(input);
            case "todo":
                return new CreateTodoCommand(input, false);
            case "deadline":
                return new CreateDeadlineCommand(input, false);
            case "event":
                return new CreateEventCommand(input, false);
        }
        throw new InvalidCommandException(input);
    }

    public void passUserCommand(String input, TaskList tasklist) {
        try {
            CommandAbstract command = createUserCommand(input);
            command.execute(tasklist);
            command.printChatbotLine();
        } catch (InvalidCommandException exp) {
            //System.out.println("ChadGPT: Please ensure that you have entered a valid command.");
        }
    }

    private CommandAbstract createDataCommand(String input) throws CorruptDataException {
        Pattern dataRegex = Pattern.compile(GENERAL_DATA_REGEX_STRING);
        Pattern deadlineRegex = Pattern.compile(DEADLINE_REGEX_STRING);
        Pattern eventRegex = Pattern.compile(EVENT_REGEX_STRING);
        Matcher dataMatcher = dataRegex.matcher(input);
        if (dataMatcher.find()) {
            String[] delimited = input.split(" \\| ");
            boolean isDone = delimited[1].equals("1");
            dataMatcher.reset();
            switch(delimited[0]) {
                case "T":
                    return new CreateTodoCommand("todo " + delimited[2], isDone);
                case "D":
                    if (!dataMatcher.usePattern(deadlineRegex).find()) {
                        throw new CorruptDataException(input);
                    }
                    return new CreateDeadlineCommand("deadline " + delimited[2]
                            + " /by " + delimited[3], isDone);
                case "E":
                    if (!dataMatcher.usePattern(eventRegex).find()) {
                        throw new CorruptDataException(input);
                    }
                    String[] dateAndTimeDelimited = delimited[3].split(" - ");
                    return new CreateEventCommand("event " + delimited[2]
                            + " /from " + dateAndTimeDelimited[0] + " /to "
                            + dateAndTimeDelimited[1], isDone);
            }
        }
        throw new CorruptDataException(input);
    }

    public void passDataCommand(String input, TaskList tasklist) throws CorruptDataException {
        try {
            CommandAbstract command = createDataCommand(input);
            command.execute(tasklist);
        } catch (InvalidCommandException exp) {
            throw new CorruptDataException(input);
        }
    }
}
