package seedu.partyplanet.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.partyplanet.logic.commands.exceptions.CommandException;
import seedu.partyplanet.model.Model;
import seedu.partyplanet.model.person.Person;
import seedu.partyplanet.model.tag.Tag;

/**
 * Deletes all persons, that is tagged with the target tags, from PartyPlanet.
 */
public class DeleteContactWithTagCommand extends DeleteCommand {

    public static final String MESSAGE_PERSON_NOT_REMOVED =
            "These tags do not exist in persons listed. No person removed.";

    private final Set<Tag> targetTags;

    private final List<Person> deletedPersons;
    private final boolean isExact;

    /**
     * Creates an DeleteContactWithTagCommand to delete the {@code Person} with specified {@code Tag}
     */
    public DeleteContactWithTagCommand(Set<Tag> targetTags, boolean isExact) {
        assert targetTags.size() > 0;
        this.targetTags = targetTags;
        deletedPersons = new ArrayList<>();
        this.isExact = isExact;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> personList = model.getFilteredPersonList();

        if (isExact) {
            for (Person person : personList) {
                if (containsExactTags(person)) {
                    deletedPersons.add(person);
                }
            }
        } else {
            for (Person person : personList) {
                if (containsAnyTags(person)) {
                    deletedPersons.add(person);
                }
            }
        }

        for (Person person : deletedPersons) {
            model.deletePerson(person);
        }

        // Only save state if there are changes (person deleted)
        if (!deletedPersons.isEmpty()) {
            model.addState();

            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, displayPersons()));
        } else {
            return new CommandResult(MESSAGE_PERSON_NOT_REMOVED);
        }
    }

    /**
     * Return true only if the person contains all of the tags
     */
    private boolean containsExactTags(Person person) {
        for (Tag t : targetTags) {
            if (!person.getTags().contains(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true only if the person contains any of the tags
     */
    private boolean containsAnyTags(Person person) {
        for (Tag t : targetTags) {
            if (person.getTags().contains(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns list of persons in the form "a, b, c,..."
     */
    private String displayPersons() {
        return deletedPersons.stream()
                .map(p -> p.getName().toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteContactWithTagCommand // instanceof handles nulls
                && targetTags.equals(((DeleteContactWithTagCommand) other).targetTags)); // state check
    }
}
