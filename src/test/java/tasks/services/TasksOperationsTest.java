package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tasks.model.Task;
import tasks.model.TasksOperations;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
class TasksOperationsTest {

    @Nested
    @DisplayName("Incoming Tasks Method Tests")
    class IncomingTasksTests {

        // F02_TC01
        @Test
        @DisplayName("F02_TC01: Empty tasks list with different time should return empty list")
        void testF02_TC01() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            TasksOperations tasksOperations = new TasksOperations(tasksList);
            Date start = createDate(2000, 4, 2, 12, 31); // era 12:30
            Date end = createDate(2000, 4, 2, 12, 31);

            Iterable<Task> result = tasksOperations.incoming(start, end);
            assertTrue(StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList()).isEmpty());
        }

        // F02_TC02
        @Test
        @DisplayName("F02_TC02: Tasks present but null dates should return empty list")
        void testF02_TC02() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            Task task = new Task("Alpha Task", createDate(2000, 4, 15, 12, 30));
            tasksList.add(task);
            TasksOperations tasksOperations = new TasksOperations(tasksList);

            Iterable<Task> result = tasksOperations.incoming(null, null);
            assertTrue(StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList()).isEmpty());
        }

        // F02_TC03
        @Test
        @DisplayName("F02_TC03: Tasks within adjusted date range")
        void testF02_TC03() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            Task task1 = new Task("Task A", createDate(2000, 4, 25, 12, 30));
            Task task2 = new Task("Task B", createDate(2000, 4, 29, 12, 30));
            task1.setActive(true);
            task2.setActive(true);
            tasksList.addAll(
                    new Task("Excluded", createDate(2000, 4, 15, 12, 31)), // în afara intervalului
                    new Task("Also Excluded", createDate(2000, 4, 20, 12, 31)),
                    task1, task2
            );
            TasksOperations tasksOperations = new TasksOperations(tasksList);

            Date start = createDate(2000, 4, 21, 12, 30);
            Date end = createDate(2000, 4, 30, 12, 30);

            Iterable<Task> result = tasksOperations.incoming(start, end);
            List<Task> incomingTasks = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());

            assertEquals(2, incomingTasks.size());
            assertTrue(incomingTasks.containsAll(List.of(task1, task2)));
        }

        // F02_TC04
        @Test
        @DisplayName("F02_TC04: All inactive tasks")
        void testF02_TC04() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            Task t1 = new Task("Inactive1", createDate(2000, 4, 15, 12, 30));
            Task t2 = new Task("Inactive2", createDate(2000, 4, 20, 12, 30));
            Task t3 = new Task("Inactive3", createDate(2000, 4, 25, 12, 30));
            Task t4 = new Task("Inactive4", createDate(2000, 4, 26, 12, 30));
            t1.setActive(false);
            t2.setActive(false);
            t3.setActive(false);
            t4.setActive(false);
            tasksList.addAll(t1, t2, t3, t4);

            TasksOperations tasksOperations = new TasksOperations(tasksList);
            Date start = createDate(2000, 4, 26, 12, 30);
            Date end = createDate(2000, 4, 27, 12, 30);

            Iterable<Task> result = tasksOperations.incoming(start, end);
            assertTrue(StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList()).isEmpty());
        }

        // F02_TC05
        @Test
        @DisplayName("F02_TC05: Null start with reversed tasks")
        void testF02_TC05() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            tasksList.addAll(
                    new Task("T4", createDate(2000, 4, 30, 12, 30)),
                    new Task("T3", createDate(2000, 4, 25, 12, 30)),
                    new Task("T2", createDate(2000, 4, 20, 12, 30)),
                    new Task("T1", createDate(2000, 4, 15, 12, 30))
            );
            TasksOperations tasksOperations = new TasksOperations(tasksList);

            Date end = createDate(2000, 4, 22, 12, 30);
            Iterable<Task> result = tasksOperations.incoming(null, end);
            assertTrue(StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList()).isEmpty());
        }

        // F02_TC0
        @Test
        @DisplayName("F02_TC06: Null end date with no valid incoming tasks")
        void testF02_TC06() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            tasksList.addAll(
                    new Task("TaskX", createDate(2000, 4, 15, 12, 30)),
                    new Task("TaskY", createDate(2000, 4, 20, 12, 30)),
                    new Task("TaskZ", createDate(2000, 4, 25, 12, 30)),
                    new Task("TaskW", createDate(2000, 4, 30, 12, 30))
            );
            TasksOperations tasksOperations = new TasksOperations(tasksList);

            Date start = createDate(2000, 4, 22, 12, 30);
            Iterable<Task> result = tasksOperations.incoming(start, null);
            assertTrue(StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList()).isEmpty());
        }

        // F02_TC07
        @Test
        @DisplayName("F02_TC07: Single task with slightly different time")
        void testF02_TC07() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            Task task = new Task("Task Edge", createDate(2000, 4, 20, 12, 31));
            task.setActive(true);
            tasksList.add(task);
            TasksOperations tasksOperations = new TasksOperations(tasksList);

            Date start = createDate(2000, 4, 17, 12, 30);
            Date end = createDate(2000, 4, 26, 12, 30);

            Iterable<Task> result = tasksOperations.incoming(start, end);
            List<Task> incomingTasks = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());

            assertEquals(1, incomingTasks.size());
            assertTrue(incomingTasks.contains(task));
        }

        // F02_TC08
        @Test
        @DisplayName("F02_TC08: Tasks with small time change")
        void testF02_TC08() {
            ObservableList<Task> tasksList = FXCollections.observableArrayList();
            Task task1 = new Task("Morning Task", createDate(2000, 4, 20, 12, 30));
            task1.setActive(true);
            Task task2 = new Task("Afternoon Task", createDate(2000, 4, 21, 12, 31));
            task2.setActive(true);
            tasksList.addAll(task1, task2);
            TasksOperations tasksOperations = new TasksOperations(tasksList);

            Date start = createDate(2000, 4, 17, 12, 30);
            Date end = createDate(2000, 4, 26, 12, 30);

            Iterable<Task> result = tasksOperations.incoming(start, end);
            List<Task> incomingTasks = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());

            assertEquals(2, incomingTasks.size());
            assertTrue(incomingTasks.containsAll(List.of(task1, task2)));
        }


        private static Date createDate(int year, int month, int day, int hour, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, day, hour, minute);
            return cal.getTime();
        }
    }
}
