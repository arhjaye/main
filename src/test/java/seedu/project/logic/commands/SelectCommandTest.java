package seedu.project.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.project.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.project.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.project.logic.commands.CommandTestUtil.showTaskAtIndex;
import static seedu.project.testutil.TypicalIndexes.INDEX_FIRST_TASK;
import static seedu.project.testutil.TypicalIndexes.INDEX_SECOND_TASK;
import static seedu.project.testutil.TypicalTasks.getTypicalProjectList;

import org.junit.Test;

import seedu.project.commons.core.Messages;
import seedu.project.commons.core.index.Index;
import seedu.project.logic.CommandHistory;
import seedu.project.logic.LogicManager;
import seedu.project.model.Model;
import seedu.project.model.ModelManager;
import seedu.project.model.UserPrefs;
import seedu.project.model.project.Project;

/**
 * Contains integration tests (interaction with the Model) for {@code SelectCommand}.
 */
public class SelectCommandTest {
    private Model model = new ModelManager(getTypicalProjectList(), new Project(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalProjectList(), new Project(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validIndexUnfilteredList_success() {

        model.setProject(model.getFilteredProjectList().get(0));
        model.setSelectedProject(model.getFilteredProjectList().get(0));
        expectedModel.setProject(model.getFilteredProjectList().get(0));
        expectedModel.setSelectedProject(model.getFilteredProjectList().get(0));

        LogicManager.setState(true);

        Index lastTaskIndex = Index.fromOneBased(model.getFilteredTaskList().size());

        assertExecutionSuccess(INDEX_FIRST_TASK);
        assertExecutionSuccess(INDEX_SECOND_TASK);
        assertExecutionSuccess(lastTaskIndex);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        model.setProject(model.getFilteredProjectList().get(0));
        model.setSelectedProject(model.getFilteredProjectList().get(0));
        expectedModel.setProject(model.getFilteredProjectList().get(0));
        expectedModel.setSelectedProject(model.getFilteredProjectList().get(0));

        LogicManager.setState(true);

        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredTaskList().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX
        );
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        model.setProject(model.getFilteredProjectList().get(0));
        model.setSelectedProject(model.getFilteredProjectList().get(0));
        expectedModel.setProject(model.getFilteredProjectList().get(0));
        expectedModel.setSelectedProject(model.getFilteredProjectList().get(0));

        LogicManager.setState(true);

        showTaskAtIndex(model, INDEX_FIRST_TASK);
        showTaskAtIndex(expectedModel, INDEX_FIRST_TASK);


        assertExecutionSuccess(INDEX_FIRST_TASK);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        model.setProject(model.getFilteredProjectList().get(0));
        model.setSelectedProject(model.getFilteredProjectList().get(0));
        expectedModel.setProject(model.getFilteredProjectList().get(0));
        expectedModel.setSelectedProject(model.getFilteredProjectList().get(0));

        LogicManager.setState(true);

        showTaskAtIndex(model, INDEX_FIRST_TASK);
        showTaskAtIndex(expectedModel, INDEX_FIRST_TASK);

        Index outOfBoundsIndex = INDEX_SECOND_TASK;

        LogicManager.setState(true);

        // ensures that outOfBoundIndex is still in bounds of project list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getProject().getTaskList().size());

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        model.setProject(model.getFilteredProjectList().get(0));
        model.setSelectedProject(model.getFilteredProjectList().get(0));
        expectedModel.setProject(model.getFilteredProjectList().get(0));
        expectedModel.setSelectedProject(model.getFilteredProjectList().get(0));

        LogicManager.setState(true);

        SelectCommand selectFirstCommand = new SelectCommand(INDEX_FIRST_TASK);
        SelectCommand selectSecondCommand = new SelectCommand(INDEX_SECOND_TASK);

        // same object -> returns true
        assertTrue(selectFirstCommand.equals(selectFirstCommand));

        // same values -> returns true
        SelectCommand selectFirstCommandCopy = new SelectCommand(INDEX_FIRST_TASK);
        assertTrue(selectFirstCommand.equals(selectFirstCommandCopy));

        // different types -> returns false
        assertFalse(selectFirstCommand.equals(1));

        // null -> returns false
        assertFalse(selectFirstCommand.equals(null));

        // different task -> returns false
        assertFalse(selectFirstCommand.equals(selectSecondCommand));
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index},
     * and checks that the model's selected task is set to the task at {@code index} in the filtered task list.
     */
    private void assertExecutionSuccess(Index index) {
        SelectCommand selectCommand = new SelectCommand(index);
        String expectedMessage = String.format(SelectCommand.MESSAGE_SELECT_TASK_SUCCESS, index.getOneBased());
        System.out.println(index);
        System.out.println(index.getZeroBased());
        System.out.println(model.getFilteredTaskList().size());

        expectedModel.setSelectedTask(model.getFilteredTaskList().get(index.getZeroBased()));

        assertCommandSuccess(selectCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String expectedMessage) {
        SelectCommand selectCommand = new SelectCommand(index);
        assertCommandFailure(selectCommand, model, commandHistory, expectedMessage);
    }
}
