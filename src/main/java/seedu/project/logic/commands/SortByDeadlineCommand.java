package seedu.project.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.project.model.Model.PREDICATE_SHOW_ALL_TASKS;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import seedu.project.commons.core.Messages;
import seedu.project.logic.CommandHistory;
import seedu.project.logic.LogicManager;
import seedu.project.model.Model;
import seedu.project.model.project.Project;
import seedu.project.model.project.VersionedProject;
import seedu.project.model.task.Task;

/**
 * Lists all tasks sorted ascending according to deadline.
 */
public class SortByDeadlineCommand extends Command {
    public static final String COMMAND_WORD = "sortDeadline";
    public static final String COMMAND_ALIAS = "sd";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows a list of all available tasks sorted by the deadline" + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS_TASK = "List sorted and displayed";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);

        if (!LogicManager.getState()) {
            return new CommandResult(Messages.MESSAGE_GO_TO_TASK_LEVEL);
        } else {
            //ObservableList of all filteredTasks
            ObservableList<Task> filteredTasks = model.getFilteredTaskList();

            Comparator<Task> taskComparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    String[] arrString = o1.getDeadline().toString().split("-", 3);
                    String o1Temp = arrString[2] + arrString[1] + arrString[0];
                    int o1TempInt = Integer.parseInt(o1Temp);

                    String[] arrString2 = o2.getDeadline().toString().split("-", 3);
                    String o2Temp = arrString2[2] + arrString2[1] + arrString2[0];
                    int o2TemptInt = Integer.parseInt(o2Temp);

                    if (o2TemptInt - o1TempInt > 0) {
                        return -1;
                    } else if (o2TemptInt - o1TempInt == 0) {
                        return 0;
                    } else if (o2TemptInt - o1TempInt < 0) {
                        return 1;
                    }

                    return 0;
                }

            };

            int size = filteredTasks.size();

            //filteredTasks.sorted(taskComparator);

            SortedList<Task> sortedList = filteredTasks.sorted(taskComparator);

            ArrayList<Task> properList = new ArrayList<Task>();

            for (int i = 0; i < size; i++) {
                properList.add(sortedList.get(i));
            }

            //System.out.println(size);

            System.out.println("SortedList:");

            model.clearTasks();

            for (int i = 0; i < size; i++) {
                System.out.println(((properList.get(i)).getName()).toString());
                model.addTask(properList.get(i));
            }

            model.updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);

            model.commitProject();

            //this will not work if user clicks on a different project while on task level??? lock UI at prev panel
            if (model.getProject().getClass().equals(VersionedProject.class)) {
                model.setProject(model.getSelectedProject(), (VersionedProject) model.getProject());
            } else {
                model.setProject(model.getSelectedProject(), (Project) model.getProject());
            }

            model.commitProjectList();

            //System.out.println("FilteredTasks:");

            //for(int i = 0; i < size; i++) {
            //    System.out.println(((filteredTasks.get(i)).getName()).toString());
            //}

            return new CommandResult(MESSAGE_SUCCESS_TASK);
        }
    }
}
