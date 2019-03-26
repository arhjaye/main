package seedu.project.model.project;

import java.util.ArrayList;
import java.util.List;

import seedu.project.model.task.Task;

/**
 * {@code Project} that keeps track of its own history.
 */
public class VersionedProject extends Project {

    private final List<ReadOnlyProject> projectStateList;
    private int currentStatePointer;
    public VersionedProject(ReadOnlyProject initialState) {
        super(initialState);

        projectStateList = new ArrayList<>();
        projectStateList.add(new Project(initialState));
        currentStatePointer = 0;
    }

    /**
     * Clears all history when selecting a new project
     */
    public void clear() {
        projectStateList.clear();
        currentStatePointer = 0;
    }

    /**
     * Populate history after clearing when selecting a new project
     */
    public void populate(ReadOnlyProject initialState) {
        projectStateList.add(new Project(initialState));
    }

    /**
     * Saves a copy of the current {@code Project} state at the end of the state
     * list. Undone states are removed from the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        projectStateList.add(new Project(this));
        currentStatePointer++;
        indicateModified();
    }

    private void removeStatesAfterCurrentPointer() {
        projectStateList.subList(currentStatePointer + 1, projectStateList.size()).clear();
    }

    /**
     * Restores the project to its previous state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new NoUndoableStateException();
        }
        currentStatePointer--;
        resetData(projectStateList.get(currentStatePointer));
    }

    /**
     * Restores the project to its previously undone state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new NoRedoableStateException();
        }
        currentStatePointer++;
        resetData(projectStateList.get(currentStatePointer));
    }

    /**
     * Returns true if {@code undo()} has project states to undo.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Returns true if {@code redo()} has project states to redo.
     */
    public boolean canRedo() {
        return currentStatePointer < projectStateList.size() - 1;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VersionedProject)) {
            return false;
        }

        VersionedProject otherVersionedProject = (VersionedProject) other;

        // state check
        return super.equals(otherVersionedProject) && projectStateList.equals(otherVersionedProject.projectStateList)
                && currentStatePointer == otherVersionedProject.currentStatePointer;
    }

    /**
     * Compares the target task with the previous edited version
     */
    public List<String> compareTask(Task target1) {
        int targetTaskId = target1.getTaskId();
        int movingStatePointer = currentStatePointer;
        movingStatePointer--;
        while (movingStatePointer >= 0) {
            int taskIndex = projectStateList.get(movingStatePointer).getIndex(targetTaskId);
            if (isThereDiff(target1, projectStateList.get(movingStatePointer).getTaskList()
                    .get(taskIndex))) {

                return getDiffString(target1, projectStateList.get(movingStatePointer).getTaskList()
                        .get(taskIndex));
            } else {
                movingStatePointer--;
            }
        }
        return null;
    }

    /**
     * Returns true if there is a difference between {@code task1, task}.
     * Else returns false
     * {@code task1, task2} must exist in the project
     */
    public boolean isThereDiff(Task task1, Task task2) {
        System.out.print(task1 + "\n");
        System.out.print(task2 + "\n\n");
        if (task1.getName() != task2.getName()) {
            return true;
        } else if (task1.getDescription() != task2.getDescription()) {
            return true;
        } else if (task1.getDeadline() != task2.getDeadline()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Makes and return a List of String of differences between the two task.
     * {@code str1, str2} must have diff.
     */
    public List<String> getDiffString(Task task1, Task target) {

        String taskReturnString = null;
        String targetReturnString = null;
        List<String> returnString = new ArrayList<>();
        // Arrays.asList(" ", " ");

        if (task1.getName() != target.getName()) {
            taskReturnString = ("Name: " + task1.getName());
            targetReturnString = ("Name: " + target.getName());
        }
        if (task1.getDescription() != target.getDescription()) {
            if (taskReturnString == null) {
                taskReturnString = ("Description: " + task1.getDescription());
                targetReturnString = ("Description: " + target.getDescription());
            } else {
                taskReturnString = (taskReturnString + " | Description: " + task1.getDescription());
                targetReturnString = (targetReturnString + " | Description: " + target.getDescription());
            }

        }
        if (task1.getDeadline() != target.getDeadline()) {
            if (taskReturnString == null) {
                taskReturnString = ("Deadline: " + task1.getDeadline());
                targetReturnString = ("Deadline: " + target.getDeadline());
            } else {
                taskReturnString = (taskReturnString + " | Deadline: " + task1.getDeadline());
                targetReturnString = (targetReturnString + " | Deadline: " + target.getDeadline());
            }

        }

        returnString.add(taskReturnString);
        returnString.add(targetReturnString);
        return returnString;
    }

    /**
     * Thrown when trying to {@code undo()} but can't.
     */
    public static class NoUndoableStateException extends RuntimeException {
        private NoUndoableStateException() {
            super("Current state pointer at start of projectState list, unable to undo.");
        }
    }

    /**
     * Thrown when trying to {@code redo()} but can't.
     */
    public static class NoRedoableStateException extends RuntimeException {
        private NoRedoableStateException() {
            super("Current state pointer at end of projectState list, unable to redo.");
        }
    }
}
