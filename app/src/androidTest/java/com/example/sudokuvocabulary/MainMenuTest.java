package com.example.sudokuvocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class MainMenuTest {
    private UiDevice device;
    private Context context;
    private static final String APP_PACKAGE = "com.example.sudokuvocabulary";
    private static final int LAUNCH_TIMEOUT = 5000;

    /**
     * Initializes the UiDevice and launches the main menu
     * of the app
     */
    @Before
    public void setUp() {
        // Initialize the test device
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Launch the sudoku app main menu
        this.context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Clear out any previous instances
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg(APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }


    /**
     * Example test of comparing app context
     */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.sudokuvocabulary", appContext.getPackageName());
    }

    /**
     * Tests whether the app can be launched from the device's
     * home menu
     */
    @Test
    public void startAppFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start test from home screen
        device.pressHome();

        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        this.context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);

        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(APP_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    /**
     * Tests the app's ability to launch a 9x9 sudoku game from
     * the main menu through the 'Play' button.
     */
    @Test
    public void launchGameFromMainMenu() {
        try {
            // Attempt to find the play button to launch the next activity
            assertTrue(clickButton(new UiSelector()
                    .resourceId(formatId("main_menu_play_button"))
            ));

            // Check that the SetSudokuSize activity has been opened
            assertTrue(doesTextViewExist(new UiSelector()
                    .text("Select Size")
            ));

            // Try to select the 9x9 size option
            assertTrue(clickButton(new UiSelector()
                    .textContains("9x9")
            ));

            // Check that the SudokuView is visible and working
            assertTrue(sudokuBoardIsActive(new UiSelector()));

        } catch (Exception e) { // Somethings has gone very wrong
            handleException(e);
        }
    }

    /**
     * Test the app's ability to launch a game from the word list
     * menu by selecting the default 'Animals' list.
     */
    @Test
    public void selectListFromWordBank() {
        // Try navigating app, handle exceptions that
        // occur when specific UI elements are not found
        try {
            // Try to find and click the word bank button
            assertTrue(clickButton(new UiSelector()
                    .textContains("Word Bank")
            ));

            // Check that the application has entered the word list activity
            assertTrue(doesTextViewExist(new UiSelector()
                    .textContains("word list")
            ));

            // Try to click the 'ANIMALS' list button
            assertTrue(clickButton(new UiSelector()
                    .textContains("animals")
            ));

            // Check that the sudoku game has launched
            // and that the board is visible and working
            assertTrue(sudokuBoardIsActive(new UiSelector()));

        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Test the app's  ability to create new word lists by
     * launching the menu for adding words, inputting a new
     * table name, and selecting nine words.
     */
    @Test public void testAddWordsActivity() {
        String listName = "foods"; // Name of new word list
        String category = "food"; // Word category to access

        try {
            // Try to click word bank button in main menu
            assertTrue(clickButton(new UiSelector().textContains("word bank")));

            // Verify that the app is in the word list menu by finding the
            // matching visible text view
            assertTrue(doesTextViewExist(new UiSelector().textContains("word lists")));

            // Try to click the 'create new word list button
            assertTrue(clickButton(new UiSelector().textContains("create new word list")));

            // Verify that we are in the word list name activity
            // by finding text input box and insert new word list name
            assertTrue(fillEditText(new UiSelector()
                    .textContains("list name"),
                    listName
            ));

            // Try to press the confirmation button
            assertTrue(clickButton(new UiSelector().textContains("confirm")));

            // Check that app is in word category selection menu
            assertTrue(doesTextViewExist(new UiSelector().textContains("categories")));

            // Try to click the specified word category button
            assertTrue(clickButton(new UiSelector()
                    .textContains(category)
            ));

            // Verify that the app is in the activity for selecting words
            // by checking if the TextView with the category name exists
            assertTrue(doesTextViewExist(new UiSelector().textContains(category)));

            // Find the table layout holding the word buttons
            UiObject wordButtons = device.findObject(new UiSelector()
                    .className("android.widget.TableLayout")
            );

            // Select the first nine word buttons
            int button = 0;
            while (button < 9) {
                UiObject row = wordButtons.getChild(new UiSelector()
                        .className("android.widget.TableRow")
                        .instance((button/3))
                );

                for (int column = 0; column < row.getChildCount(); column++) {
                    // Try to select a word in a toggle button
                    UiObject wordButton = row.getChild(new UiSelector()
                            .clickable(true)
                            .checkable(true)
                            .checked(false)
                            .className("android.widget.ToggleButton")
                    );
                    wordButton.click();
                    assertTrue(wordButton.exists());
                    button++;
                }
            }

            // Check that the back button works
            assertTrue(clickButton(new UiSelector().textContains("back")));

        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Tests the app's menus in landscape by calling all other tests
     * in landscape mode.
     */
    @Test
    public void rotatedOrientationTest() {
        try {
            device.setOrientationRight(); // Rotate the device right

            // Run all test cases in this orientation
            launchGameFromMainMenu();
            setUp();
            selectListFromWordBank();
            setUp();
            testAddWordsActivity();

            device.setOrientationNatural(); // Reset device to original position

        } catch (Exception e) { // Device has failed to rotate
            handleException(e);
        }
    }

    @Test
    public void testDarkModeToggle() {
        try {
            // Try to click the dark mode switch in main menu
            assertTrue(clickSwitch(new UiSelector().resourceId(formatId("darkSwitch"))));

            // Get the switch's current state
            UiObject darkSwitch = device.findObject(new UiSelector()
                    .resourceId(formatId("darkSwitch"))
            );
            boolean isChecked = darkSwitch.isChecked();

            // Check if mode is correct
            boolean isDark = AppCompatDelegate.getDefaultNightMode() ==
                    AppCompatDelegate.MODE_NIGHT_YES;
            assertEquals(isDark, isChecked);
        } catch (Exception e) {
            handleException(e);
        }
    }


    private boolean clickButton(UiSelector selector) throws UiObjectNotFoundException {
        UiObject button = device.findObject(selector.className("android.widget.Button"));
        boolean buttonIsValid = button.exists() && button.isEnabled();
        if (buttonIsValid) {
            button.click();
        }
        return buttonIsValid;
    }

    private boolean clickSwitch(UiSelector selector) throws  UiObjectNotFoundException {
        UiObject switchFound = device.findObject(selector);
        if (switchFound.exists()) {
            switchFound.click();
        }
        return switchFound.exists();
    }

    private boolean doesTextViewExist(UiSelector selector) {
        UiObject textView = device.findObject(selector.className("android.widget.TextView"));
        return textView.exists();
    }

    private UiObject findEditText(UiSelector selector) {
        return device.findObject(selector.className("android.widget.EditText"));
    }

    private boolean fillEditText(UiSelector selector, String text) throws UiObjectNotFoundException
    {
        UiObject editable = findEditText(selector);
        boolean doesExist = editable.exists();
        if (doesExist) {
            editable.setText(text);
        }
        return doesExist;
    }

    private boolean sudokuBoardIsActive(UiSelector selector) {
        UiObject sudoku = device.findObject(selector
                .resourceId(formatId("sudokuGridView"))
        );
        return sudoku.exists();
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        assertNull(e);
    }

    private String formatId(String id) {
        return APP_PACKAGE + ":id/" + id;
    }
}