package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class TagCommand extends Command {
    public static final String COMMAND_WORD = "sex";
    private final Tag tag;
    private final Index index;

    public TagCommand(Tag tag, Index index) {
        this.tag = tag;
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Person p = model.getFilteredPersonList().get(index.getZeroBased());
        Person newPerson = p.addNewTagImmutably(tag);

        model.setPerson(p, newPerson);
        return new CommandResult("Tag " +  tag + " added!");
    }
}
