package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.INDEX_PLACEHOLDER;
import static seedu.address.logic.parser.CliSyntax.KEYWORD_PLACEHOLDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.Model;

//@@author EvitanRelta-reused
// Reused from https://github.com/AY2223S1-CS2103T-T12-2/tp
// with almost complete overhauling, including refactoring, bug-fixing, adding
// of asserts, and changing the behaviour of the feature to suit our needs.
/**
 * Suggests and autocompletes command/argument based on the user input.
 */
public class AutocompleteEngine {

    private static final ArrayList<String> COMMAND_LIST = new ArrayList<>(List.of(
            AddCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            RemarkCommand.COMMAND_WORD,
            ShowRemarkCommand.COMMAND_WORD
    ));
    private static final HashMap<String, ArrayList<Prefix>> ARGUMENT_PREFIX_MAP = new HashMap<>(Map.of(
            AddCommand.COMMAND_WORD, AddCommand.ARGUMENT_PREFIXES,
            ClearCommand.COMMAND_WORD, ClearCommand.ARGUMENT_PREFIXES,
            DeleteCommand.COMMAND_WORD, DeleteCommand.ARGUMENT_PREFIXES,
            EditCommand.COMMAND_WORD, EditCommand.ARGUMENT_PREFIXES,
            ExitCommand.COMMAND_WORD, ExitCommand.ARGUMENT_PREFIXES,
            FindCommand.COMMAND_WORD, FindCommand.ARGUMENT_PREFIXES,
            HelpCommand.COMMAND_WORD, HelpCommand.ARGUMENT_PREFIXES,
            ListCommand.COMMAND_WORD, ListCommand.ARGUMENT_PREFIXES,
            RemarkCommand.COMMAND_WORD, RemarkCommand.ARGUMENT_PREFIXES,
            ShowRemarkCommand.COMMAND_WORD, ShowRemarkCommand.ARGUMENT_PREFIXES
    ));

    private final Model model;

    /**
     * Constructs a {@code CommandSuggestor} with predefined commands and argument prompts.
     */
    public AutocompleteEngine(Model model) {
        this.model = model;

        // Assert 'commandArgPrefixes' keys contains all the elements of 'commandList'
        assert COMMAND_LIST.stream().allMatch(ARGUMENT_PREFIX_MAP::containsKey);
        // and vice versa.
        assert ARGUMENT_PREFIX_MAP.keySet().stream().allMatch(COMMAND_LIST::contains);
        // Assert that they both contains only the same values and nothing else, with no duplicates.
        assert ARGUMENT_PREFIX_MAP.keySet().size() == COMMAND_LIST.size();

        // For commands with index arguments, the index must be the first argument.
        assert ARGUMENT_PREFIX_MAP.values().stream()
                .filter(argPrefix -> argPrefix.contains(INDEX_PLACEHOLDER))
                .allMatch(argPrefix -> argPrefix.get(0).equals(INDEX_PLACEHOLDER));

        // For commands with keyword arguments, the keyword is assumed to be the only argument.
        assert ARGUMENT_PREFIX_MAP.values().stream()
                .filter(argPrefix -> argPrefix.contains(KEYWORD_PLACEHOLDER))
                .allMatch(argPrefix -> argPrefix.size() == 1);

        // All prefix-less arguments (eg. index/keywords) are assumed to come before prefixed args.
        assert ARGUMENT_PREFIX_MAP.values().stream()
                .allMatch(argPrefix -> argPrefix.stream()
                        .dropWhile(Prefix::isPlaceholder)
                        .noneMatch(Prefix::isPlaceholder));
    }

    /**
     * Suggests a command based on the user input.
     *
     * @param userInput User input.
     * @return Suggested command (including the user input).
     * @throws CommandException If the user input is invalid.
     */
    public String suggestCommand(String userInput) throws CommandException {
        assert userInput != null;

        if (userInput.isBlank()) {
            return userInput;
        }

        String strippedLeadingInput = userInput.stripLeading();
        String inputLeadingSpaces = userInput.substring(
                0, userInput.length() - strippedLeadingInput.length());

        String[] splitArr = strippedLeadingInput.split(" ", 2);
        String commandWord = splitArr[0];
        String commandBody = splitArr.length > 1 ? " " + splitArr[1] : "";

        CommandException noSuchCommandException = new CommandException(
            String.format("No command starting with \"%s\" found.", commandWord));

        boolean isCommandComplete = strippedLeadingInput.contains(" ");
        if (!isCommandComplete) {
            String suggestedCommand = COMMAND_LIST.stream()
                    .filter(command -> command.startsWith(commandWord))
                    .findFirst()
                    .orElseThrow(() -> noSuchCommandException);
            return inputLeadingSpaces + suggestedCommand + suggestArguments(suggestedCommand, commandBody);
        }

        boolean isInvalidCommand = !COMMAND_LIST.contains(commandWord);
        if (isInvalidCommand) {
            throw noSuchCommandException;
        }

        return userInput + suggestArguments(commandWord, commandBody);
    }

    /**
     * Returns the new user input when user auto-completes the command.
     *
     * @param userInput Current user input.
     * @param commandSuggestion Current command suggestion.
     * @return New user input.
     */
    public String autocompleteCommand(String userInput, String commandSuggestion) {
        // Command suggested but not yet entered by user
        String remainingSuggestion = commandSuggestion.substring(userInput.length());
        Pattern nextAutocompleteRegex = Pattern.compile("^ *[a-z0-9_]*\\/*", Pattern.CASE_INSENSITIVE);
        String nextAutocomplete = Optional.of(nextAutocompleteRegex.matcher(remainingSuggestion))
                .filter(Matcher::find)
                .map(Matcher::group)
                .filter(match -> !match.trim().equals(INDEX_PLACEHOLDER.getPlaceholderText()))
                .filter(match -> !match.trim().equals(KEYWORD_PLACEHOLDER.getPlaceholderText()))
                .orElse("");
        return userInput + nextAutocomplete;
    }

    private Map<Prefix, ArrayList<String>> getExistingArgValuesForAutocomplete() {
        return new HashMap<>(Map.of(PREFIX_TAG, model.getExistingTagValues()));
    }

    /**
     * Suggests prompts for arguments for {@code command} based on the user input.
     *
     * @param command The command to suggest arguments for.
     * @param commmandBody The command body of the current user input.
     * @return Suggested arguments.
     * @throws CommandException If the user input is invalid.
     */
    private String suggestArguments(String command, String commmandBody) throws CommandException {
        ArrayList<Prefix> argPrefixes = ARGUMENT_PREFIX_MAP.get(command);
        assert argPrefixes != null;
        ArgumentMultimap argumentMultimap =
                ArgumentTokenizer.tokenize(" " + commmandBody, argPrefixes.toArray(Prefix[]::new));
        Map<Prefix, ArrayList<String>> existingArgValues = getExistingArgValuesForAutocomplete();

        if (commmandBody.isBlank()) {
            String allArgs = argPrefixes.stream()
                    .map(prefix -> prefix.getPrefix() + prefix.getPlaceholderText())
                    .collect(Collectors.joining(" "));
            String leadingPadding = commmandBody.isEmpty() ? " " : "";
            return leadingPadding + allArgs;
        }

        String[] splitArr = commmandBody.trim().split(" +");
        int numOfWords = splitArr.length;
        assert numOfWords > 0;
        String firstWord = splitArr[0];
        String lastWord = splitArr[splitArr.length - 1];
        assert !firstWord.isBlank();
        assert !firstWord.contains(" ");
        assert !lastWord.isBlank();
        assert !lastWord.contains(" ");

        boolean isIndexRequired = argPrefixes.contains(INDEX_PLACEHOLDER);
        if (isIndexRequired) {
            // Index is assumed to always be the first arg.
            assert argPrefixes.get(0) == INDEX_PLACEHOLDER;

            //TODO to remove when commands are dry-run to see if they're valid,
            //where an error should be thrown should the first word not be a integer.
            boolean hasIntAsFirstWord = firstWord.matches("\\d+");
            if (!hasIntAsFirstWord) {
                throw new CommandException("Invalid index.");
            }
        }

        boolean hasNoTrailingSpace = !commmandBody.endsWith(" ");
        if (hasNoTrailingSpace) {
            Prefix currPrefix = new Prefix(lastWord.replaceAll("[^\\/]*$", ""));
            boolean toAutocompleteArgValue = existingArgValues.containsKey(currPrefix);

            if (toAutocompleteArgValue) {
                String argValue = lastWord.replaceAll("^.*\\/", "");
                String matchingExistingValues = existingArgValues.get(currPrefix)
                        .stream()
                        .filter(value -> value.startsWith(argValue))
                        .collect(Collectors.joining(" | "));
                return matchingExistingValues.isEmpty()
                        ? ""
                        : matchingExistingValues.substring(argValue.length());
            }

            // User is not filling the value of prefix.
            // Example of filling the value: "add n/Sha" where user is halfway filling the name field.
            // But it's false when: "add n/" where user is not filling it yet.
            boolean isFillingPrefixValue = lastWord.contains("/") && !lastWord.endsWith("/");
            if (isFillingPrefixValue) {
                // Don't suggest anything if user is filling.
                return "";
            }

            String matchingArgs = argPrefixes.stream()
                    // Excludes prefix-less arguments like index/keywords.
                    .filter(prefix -> !prefix.isPlaceholder())
                    // Get only unfilled arguments.
                    .filter(prefix -> argumentMultimap.getValue(prefix).isEmpty())
                    .map(prefix -> prefix.getPrefix() + prefix.getPlaceholderText())
                    .filter(str -> str.startsWith(lastWord))
                    .collect(Collectors.joining(" "));

            return matchingArgs.isEmpty()
                    ? ""
                    : matchingArgs.substring(lastWord.length());
        }

        boolean isKeywordRequired = argPrefixes.contains(KEYWORD_PLACEHOLDER);
        if (isKeywordRequired) {
            // Commands with keyword argument are assumed to only require that keyword as argument.
            // If the keyword isn't the only arg., then more checks/parsing needs to be done when
            // gettingt he suggestions.
            assert argPrefixes.size() == 1;
        }

        String remainingArgs = argPrefixes.stream()
                // Skip the filled prefix-less arguments.
                .skip(numOfWords)
                // Remove filled prefixed arguments.
                .filter(prefix -> prefix.isPlaceholder() || argumentMultimap.getValue(prefix).isEmpty())
                .map(prefix -> prefix.getPrefix() + prefix.getPlaceholderText())
                .collect(Collectors.joining(" "));
        return remainingArgs;
    }
}
//@@author
