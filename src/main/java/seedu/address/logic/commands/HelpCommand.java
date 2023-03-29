package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    private final boolean isModifying = false;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    @Override
    public boolean checkModifiable() {
        return isModifying;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false, null);
    }
}
