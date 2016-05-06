package jp.yhorie.notes;

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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NoteCRUDTest {

  private Note mNote;
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
  }

  @Test
  public void displayNoteActivity() {
    onView(withId(R.id.notesCreateButton)).perform(click());

    onView(withHint("title")).check(matches(isDisplayed()));
    onView(withHint("detail")).check(matches(isDisplayed()));
  }

  @Test
  public void createNote() {
    onView(withText("title:0")).check(matches(isDisplayed()));
    onView(withText("detail:0")).check(matches(isDisplayed()));
  }

  @Test
  public void updateNote() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(click());

    mNote = SQLite.select().from(Note.class).where(Note_Table.title.like("title:0")).querySingle();
    createDate = simpleDateFormat.format(mNote.getCreateDate());

    // check note content
    onView(withId(R.id.noteTitle)).check(matches(withText("title:0")));
    onView(withId(R.id.noteDetail)).check(matches(withText("detail:0")));
    onView(withId(R.id.noteCreateDate)).check(matches(withText("created at " + createDate)));

    // modify note content
    onView(withId(R.id.noteTitle)).perform(clearText(), typeText("title:modified"));
    onView(withId(R.id.noteDetail)).perform(clearText(), typeText("detail:modified"));
    onView(withId(R.id.noteRegistButton)).perform(click());

    mNote = SQLite.select().from(Note.class).where(Note_Table.title.like("title:modified")).querySingle();
    updateDate = simpleDateFormat.format(mNote.getUpdateDate());

    // check listview content
    onView(allOf(withId(R.id.listItem), withChild(withText("title:modified")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:modified")))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText(updateDate)))).check(matches(isDisplayed()));
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(doesNotExist());
    onView(allOf(withId(R.id.listItem), withChild(withText("detail:0")))).check(doesNotExist());
    onView(allOf(withId(R.id.listItem), withChild(withText(createDate)))).check(doesNotExist());

    // display note
    onView(allOf(withId(R.id.listItem), withChild(withText("title:modified")))).perform(click());

    // check note content
    onView(withId(R.id.noteTitle)).check(matches(withText("title:modified")));
    onView(withId(R.id.noteDetail)).check(matches(withText("detail:modified")));
    onView(withId(R.id.noteCreateDate)).check(matches(withText("created at " + createDate)));
    onView(withId(R.id.noteUpdateDate)).check(matches(withText("updated at " + updateDate)));
  }

  @Test
  public void displayDeleteDialogAtNoteActivity() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(click());

    onView(withId(R.id.noteDeleteButton)).perform(longClick());

    onView(withText("Comfirm")).check(matches(isDisplayed()));
    onView(withText("Delete Note ( title: title:0 ) ?")).check(matches(isDisplayed()));
    onView(withText("Yes")).check(matches(isDisplayed()));
    onView(withText("No")).check(matches(isDisplayed()));
  }

  @Test
  public void displayHowToDeleteToast() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(click());

    onView(withId(R.id.noteDeleteButton)).perform(click());

    onView(withText("If you want to delete, do long click.")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
  }

  public void deleteCancelNoteAtNoteActivity() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(click());

    onView(withId(R.id.noteDeleteButton)).perform(longClick());
    onView(withId(android.R.id.button2)).perform(click());
    pressBack();

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(matches(isDisplayed()));
  }

  @Test
  public void deleteNoteAtNoteActivity() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(click());

    onView(withId(R.id.noteDeleteButton)).perform(longClick());
    onView(withId(android.R.id.button1)).perform(click());

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(doesNotExist());

    onView(withText("Deleted NOTE ( title: title:0 ) .")).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    onView(withText("There is no NOTE.")).check(matches(isDisplayed()));
  }

  @Test
  public void displayDeleteDialogAtNotesActivity() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(longClick());

    onView(withText("Comfirm")).check(matches(isDisplayed()));
    onView(withText("Delete Note ( title: title:0 ) ?")).check(matches(isDisplayed()));
    onView(withText("Yes")).check(matches(isDisplayed()));
    onView(withText("No")).check(matches(isDisplayed()));
  }

  @Test
  public void deleteNoteCancelAtNotesActivity() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(longClick());

    onView(withId(android.R.id.button2)).perform(click());

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(matches(isDisplayed()));
  }

  @Test
  public void deleteNoteAtNotesActivity() {
    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).perform(longClick());
    onView(withId(android.R.id.button1)).perform(click());

    onView(allOf(withId(R.id.listItem), withChild(withText("title:0")))).check(doesNotExist());
    onView(withText("There is no NOTE.")).check(matches(isDisplayed()));
  }
}
