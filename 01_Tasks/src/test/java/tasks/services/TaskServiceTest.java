package tasks.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TasksService Test Suite")
@TestMethodOrder(OrderAnnotation.class)
class TasksServiceTest {

    private TasksService tasksService;
    private ArrayTaskList taskList;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        taskList = new ArrayTaskList();
        tasksService = new TasksService(taskList);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Date parseDate(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }

    @Nested
    @DisplayName("Equivalence Class Partitioning Tests")
    class EquivalenceClassPartitioningTests {

        @Test
        @Order(1)
        @DisplayName("TC1: Valid task should be added successfully")
        void testAddTask_Valid() throws ParseException {
            Date start = parseDate("2000-01-01");
            Date end = parseDate("2022-01-01");
            Task task = new Task("Valid Task", start, end, 10);

            tasksService.addTask(task);

            assertEquals(1, taskList.size());
            assertEquals(task, taskList.getTask(0));
        }

        @Test
        @Order(2)
        @DisplayName("TC2: Empty title should throw IllegalArgumentException")
        void testAddTask_EmptyTitle() throws ParseException {
            Date start = parseDate("2020-01-01");
            Date end = parseDate("2020-01-02");

            Exception exception = assertThrows(IllegalArgumentException.class, () -> new Task("", start, end, 5));
            assertTrue(exception.getMessage().contains("Title cannot be empty"));
        }

        @Test
        @Order(3)
        @DisplayName("TC3: Negative interval should throw exception")
        void testAddTask_NegativeInterval() throws ParseException {
            Date start = parseDate("2000-01-01");
            Date end = parseDate("2020-01-01");

            Exception exception = assertThrows(IllegalArgumentException.class, () -> new Task("NegativeInterval", start, end, -3));
            assertTrue(exception.getMessage().contains("interval should me > 1"));
        }

        @Test
        @Order(4)
        @DisplayName("TC4: Invalid end date should throw IllegalArgumentException")
        void testAddTask_TC2_ECP_InvalidEndDate() throws ParseException {
            // Arrange
            Date start = parseDate("2024-04-03");
            Date end = parseDate("1947-09-08");

            // Act
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                Task task = new Task("Alt Titlu", start, end, 4);
                tasksService.addTask(task);
            });

            //Assert
            assertTrue(exception.getMessage().contains("Time cannot be negative"));
        }


        @Test
        @Order(5)
        @DisplayName("TC5: Zero interval should throw exception")
        void testAddTask_ZeroInterval() throws ParseException {
            Date start = parseDate("2000-01-01");
            Date end = parseDate("2022-01-01");

            Exception exception = assertThrows(IllegalArgumentException.class, () -> new Task("ZeroInterval", start, end, 0));
            assertTrue(exception.getMessage().contains("interval should me > 1"));
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @ParameterizedTest
        @CsvSource({
                "EdgeCase1, 1970-01-01, 1970-01-02, 1",
                "EdgeCase2, 2000-01-01, 2000-01-01, 1",
                "EdgeCase3, 1970-01-02, 1970-01-03, 2"
        })
        @DisplayName("TC6-8: Valid edge case inputs should be accepted")
        void testAddTask_ValidBoundaryCases(String title, String startStr, String endStr, int interval) throws ParseException {
            Date start = parseDate(startStr);
            Date end = parseDate(endStr);
            Task task = new Task(title, start, end, interval);

            tasksService.addTask(task);

            assertEquals(1, taskList.size());
            assertEquals(task, taskList.getTask(0));
        }

        @Test
        @Order(9)
        @DisplayName("TC9: Minimum allowed interval of 1 should be accepted")
        void testAddTask_MinimumInterval() throws ParseException {
            Date start = parseDate("2010-01-01");
            Date end = parseDate("2010-01-02");
            Task task = new Task("MinInterval", start, end, 1);

            tasksService.addTask(task);

            assertEquals(1, taskList.size());
        }

        @Test
        @Order(10)
        @DisplayName("TC10: Very large interval should be accepted")
        void testAddTask_LargeInterval() throws ParseException {
            Date start = parseDate("2010-01-01");
            Date end = parseDate("2030-01-01");
            Task task = new Task("LargeInterval", start, end, 99999);

            tasksService.addTask(task);

            assertEquals(1, taskList.size());
        }

        @Test
        @Order(11)
        @DisplayName("TC11: Time equals end date should return null on nextTimeAfter")
        void testNextTimeAfter_EndDate() throws ParseException {
            Date start = parseDate("2020-01-01");
            Date end = parseDate("2020-01-02");
            Task task = new Task("RepeatTask", start, end, 1);
            task.setActive(true);

            Date current = parseDate("2020-01-02");
            assertNull(task.nextTimeAfter(current));
        }

        @Test
        @Order(12)
        @DisplayName("TC12: nextTimeAfter before start should return start")
        void testNextTimeAfter_BeforeStart() throws ParseException {
            Date start = parseDate("2025-01-01");
            Date end = parseDate("2025-01-10");
            Task task = new Task("RepeatTask", start, end, 2);
            task.setActive(true);

            Date current = parseDate("2024-12-31");
            assertEquals(start, task.nextTimeAfter(current));
        }

        @Test
        @Order(13)
        @DisplayName("TC13: Inactive task returns null for nextTimeAfter")
        void testNextTimeAfter_Inactive() throws ParseException {
            Date start = parseDate("2025-01-01");
            Date end = parseDate("2025-01-10");
            Task task = new Task("InactiveTask", start, end, 2);
            task.setActive(false);

            Date current = parseDate("2025-01-02");
            assertNull(task.nextTimeAfter(current));
        }

        @Test
        @Order(14)
        @DisplayName("TC14: Valid one-time task should behave correctly")
        void testOneTimeTask() throws ParseException {
            Date time = parseDate("2025-01-01");
            Task task = new Task("OneTime", time);
            task.setActive(true);

            assertEquals(time, task.nextTimeAfter(parseDate("2024-12-31")));
            assertNull(task.nextTimeAfter(time));
        }
    }
}