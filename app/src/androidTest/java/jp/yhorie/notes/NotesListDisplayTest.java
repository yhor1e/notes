package jp.yhorie.notes;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.yhorie.template.R;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotesListDisplayTest {

  private final static Integer CREATE_ITEMS_SIZE = 10;  // > more than window height
  private static Boolean dateSetUpisDone = false;

  @Rule
  public ActivityTestRule<NotesActivity> mActivityRule = new ActivityTestRule(NotesActivity.class);

  @Before
  public void setUp() throws Exception {
    Delete.table(Note.class, new SQLCondition[]{Note_Table.id.isNotNull()});
    mActivityRule.getActivity();

    if(dateSetUpisDone) {
      return;
    }
    for (int i = 0; i < CREATE_ITEMS_SIZE; i++) {
      onView(withId(R.id.notesCreateButton)).perform(click());
      onView(withId(R.id.noteTitle)).perform(typeText("title:" + i));
      onView(withId(R.id.noteDetail)).perform(typeText("detail:" + i));
      onView(withId(R.id.noteRegistButton)).perform(click());
    }
    dateSetUpisDone = true;
  }

  @Test
  public void firstTodoDisplayed() {
    onView(withText("title:" + String.valueOf(CREATE_ITEMS_SIZE - 1))).check(matches(isDisplayed()));
  }

  @Test
  public void lastTodoNotDisplayed() {
    onView(withText("title:0")).check(doesNotExist());
  }

  @Test
  public void listScrollsAndLastTodoDisplayed() {
    onData(Matchers.instanceOf(Note.class))
        .atPosition(CREATE_ITEMS_SIZE - 1)
        .check(matches(Matchers.allOf(isDisplayed(), withChild(withText("title:0")))));
  }
}