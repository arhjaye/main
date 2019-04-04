package systemtests;

import static seedu.project.commons.core.Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
import static seedu.project.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.project.logic.commands.DeleteCommand.MESSAGE_DELETE_TASK_SUCCESS;
import static seedu.project.testutil.TestUtil.getLastIndex;
import static seedu.project.testutil.TestUtil.getMidIndex;
import static seedu.project.testutil.TestUtil.getTask;
import static seedu.project.testutil.TypicalIndexes.INDEX_FIRST_TASK;

//import org.junit.Test;

import seedu.project.commons.core.Messages;
import seedu.project.commons.core.index.Index;
import seedu.project.logic.commands.DeleteCommand;
import seedu.project.logic.commands.RedoCommand;
import seedu.project.logic.commands.UndoCommand;
import seedu.project.model.Model;
import seedu.project.model.task.Task;

public class DeleteCommandSystemTest extends ProjectSystemTest {

    private static final String MESSAGE_INVALID_DELETE_COMMAND_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
    /**
     * Just for the sake of it
     */
    //@Test
    public void delete() {
        /* ----------------- Performing delete operation while an unfiltered list is being shown -------------------- */

        selectProject(Index.fromOneBased(1));

        /* Case: delete the first task in the list, command with leading spaces and trailing spaces -> deleted */
        Model expectedModel = getModel();
        String command = "     " + DeleteCommand.COMMAND_WORD + "      " + INDEX_FIRST_TASK.getOneBased() + "       ";
        Task deletedTask = removeTask(expectedModel, INDEX_FIRST_TASK);
        String expectedResultMessage = String.format(MESSAGE_DELETE_TASK_SUCCESS, deletedTask);
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: delete the last task in the list -> deleted */
        Model modelBeforeDeletingLast = getModel();
        modelBeforeDeletingLast.setSelectedProject(expectedModel.getFilteredProjectList().get(0));
        Index lastTaskIndex = getLastIndex(modelBeforeDeletingLast);
        assertCommandSuccess(lastTaskIndex);

        /* Case: undo deleting the last task in the list -> last task restored */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, modelBeforeDeletingLast, expectedResultMessage);

        /* Case: redo deleting the last task in the list -> last task deleted again */
        command = RedoCommand.COMMAND_WORD;
        removeTask(modelBeforeDeletingLast, lastTaskIndex);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, modelBeforeDeletingLast, expectedResultMessage);

        /* Case: delete the middle task in the list -> deleted */
        Index middleTaskIndex = getMidIndex(getModel());
        assertCommandSuccess(middleTaskIndex);

        /* ------------------ Performing delete operation while a filtered list is being shown ---------------------- */

        /* Case: filtered task list, delete index within bounds of project and task list -> deleted */
        /*        showTasksWithName(KEYWORD_MATCHING_TEST);
        Index index = INDEX_FIRST_TASK;
        assertTrue(index.getZeroBased() < expectedModel.getFilteredTaskList().size());
        assertCommandSuccess(index);*/

        /* Case: filtered task list, delete index within bounds of project but out of bounds of task list
         * -> rejected
         */
        /*        showTasksWithName(KEYWORD_MATCHING_TEST);
        int invalidIndex = getModel().getProject().getTaskList().size();
        command = DeleteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_TASK_DISPLAYED_INDEX);*/

        /* --------------------- Performing delete operation while a task card is selected ------------------------ */

        /* Case: delete the selected task -> task list panel selects the task before the deleted task */
        showAllTasks();
        expectedModel = getModel();
        expectedModel.setSelectedProject(expectedModel.getFilteredProjectList().get(0));
        Index selectedIndex = getLastIndex(expectedModel);
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased() - 1);
        selectTask(selectedIndex);
        command = DeleteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        deletedTask = removeTask(expectedModel, selectedIndex);
        expectedResultMessage = String.format(MESSAGE_DELETE_TASK_SUCCESS, deletedTask);
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* --------------------------------- Performing invalid delete operation ------------------------------------ */

        /* Case: invalid index (0) -> rejected */
        command = DeleteCommand.COMMAND_WORD + " 0";
        assertCommandFailure(command, MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: invalid index (-1) -> rejected */
        command = DeleteCommand.COMMAND_WORD + " -1";
        assertCommandFailure(command, MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getProject().getTaskList().size() + 1);
        command = DeleteCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased();
        assertCommandFailure(command, MESSAGE_INVALID_TASK_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(DeleteCommand.COMMAND_WORD + " abc", MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: invalid arguments (extra argument) -> rejected */
        assertCommandFailure(DeleteCommand.COMMAND_WORD + " 1 abc", MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("DelETE 1", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Removes the {@code Task} at the specified {@code index} in {@code model}'s project.
     * @return the removed task
     */
    private Task removeTask(Model model, Index index) {
        Task targetTask = getTask(model, index);
        model.deleteTask(targetTask);
        return targetTask;
    }

    /**
     * Deletes the task at {@code toDelete} by creating a default {@code DeleteCommand} using {@code toDelete} and
     * performs the same verification as {@code assertCommandSuccess(String, Model, String)}.
     * @see DeleteCommandSystemTest#assertCommandSuccess(String, Model, String)
     */
    private void assertCommandSuccess(Index toDelete) {
        Model expectedModel = getModel();
        Task deletedTask = removeTask(expectedModel, toDelete);
        String expectedResultMessage = String.format(MESSAGE_DELETE_TASK_SUCCESS, deletedTask);

        assertCommandSuccess(
                DeleteCommand.COMMAND_WORD + " " + toDelete.getOneBased(), expectedModel, expectedResultMessage);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url and selected card remains unchanged.<br>
     * 4. Asserts that the status bar's sync status changes.<br>
     * 5. Asserts that the command box has the default style class.<br>
     * Verifications 1 and 2 are performed by
     * {@code ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.
     * @see ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String)} except that the browser url
     * and selected card are expected to update accordingly depending on the card at {@code expectedSelectedCardIndex}.
     * @see DeleteCommandSystemTest#assertCommandSuccess(String, Model, String)
     * @see ProjectSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
            Index expectedSelectedCardIndex) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);

        /*        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }*/

        assertCommandBoxShowsDefaultStyle();
        //assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 4. Asserts that the command box has the error style.<br>
     * Verifications 1 and 2 are performed by
     * {@code ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        //assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        //assertStatusBarUnchanged();
    }
}
