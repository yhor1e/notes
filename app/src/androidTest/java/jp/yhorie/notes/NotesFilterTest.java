package jp.yhorie.notes;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.yhorie.template.R;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotesFilterTest {

  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String createDate;
  private String updateDate;

  @Rule
  public ActivityTestRule<NotesActivity> mActivityRule = new ActivityTestRule(NotesActivity.class);

  @Before
  public void setUp() throws Exception {
    Delete.table(Note.class, new SQLCondition[]{Note_Table.id.isNotNull()});
    mActivityRule.getActivity();

    onView(withId(R.id.notesCreateButton)).perform(click());
    onView(withId(R.id.noteTitle)).perform(typeText("title:0"));
    onView(withId(R.id.noteDetail)).perform(typeText("detail:0"));
    onView(withId(R.id.noteRegistButton)).perform(click());

    SystemClock.sleep(1000);  // different createDate in 2 notes for filtering test by createDate

    onView(withId(R.id.notesCreateButton)).perform(click());
    onView(withId(R.id.noteTitle)).perform(typeText("title:1"));
    onView(withId(R.id.noteDetail)).perform(typeText("detail:1"));
    onView(withId(R.id.noteRegistButton)).perform(click());
  }

  @Test
  public void filterNoteByTitle() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("title:1")))).check(matches(isDisplayed()));

    onView(withId(R.id.notesFilter)).perform(typeText("title:0"));

    SystemClock.sleep(1000);  // wait render listview
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("title:1")))).check(doesNotExist());
  }

  @Test
  public void filterNoteByDetail() {

    onView(allOf(withId(R.id.listItem), withChild(withText("detail:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:1")))).check(matches(isDisplayed()));

    onView(withId(R.id.notesFilter)).perform(typeText("detail:0"));

    SystemClock.sleep(1000);  // wait render listview
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:1")))).check(doesNotExist());
  }

  @Test
  public void filterNoteByCreateDate() {
    Note mNote = SQLite.select().from(Note.class).where(Note_Table.title.like("title:0")).querySingle();
    createDate = simpleDateFormat.format(mNote.getCreateDate());

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("title:1")))).check(matches(isDisplayed()));

    onView(withId(R.id.notesFilter)).perform(typeText(createDate));

    SystemClock.sleep(1000);  // wait render listview
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:1")))).check(doesNotExist());
  }

  @Test
  public void filterNoteByUpdateDate() {

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(click());
    SystemClock.sleep(1000);  // different date in createDate and updateDateØ for filtering test by createDate
    onView(withId(R.id.noteRegistButton)).perform(click());

    Note mNote = SQLite.select().from(Note.class).where(Note_Table.title.like("title:0")).querySingle();
    updateDate = simpleDateFormat.format(mNote.getUpdateDate());

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("title:1")))).check(matches(isDisplayed()));

    onView(withId(R.id.notesFilter)).perform(typeText(updateDate));

    SystemClock.sleep(1000);  // wait render listview
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:0")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:1")))).check(doesNotExist());
  }
}