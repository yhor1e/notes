package jp.yhorie.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yhorie.template.R;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

public class NotesActivity extends AppCompatActivity {

  private NotesListViewAdapter mNotesListViewAdapter;
  private FlowCursorList mFlowCursorList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notes);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton mCreateButtonView = (FloatingActionButton) findViewById(R.id.notesCreateButton);
    mCreateButtonView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        startActivityForResult(intent, 0);
      }
    });

    // fetch data from table
    try {
      mFlowCursorList = new FlowCursorList<Note>(true, new Select().from(Note.class).orderBy(OrderBy.fromProperty(Note_Table.updateDate)));
    } catch (IllegalArgumentException e) {
      mFlowCursorList = null;
    }

    // set up listview
    ListView mNotesListView = (ListView) findViewById(R.id.notesList);
    mNotesListViewAdapter = new NotesListViewAdapter(NotesActivity.this, mFlowCursorList);
    mNotesListView.setAdapter(mNotesListViewAdapter);
    mNotesListView.setEmptyView(findViewById(R.id.emptyList));
    mNotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Note mNote = (Note) parent.getAdapter().getItem(position);
        Intent mIntent = new Intent(getApplicationContext(), NoteActivity.class);
        mIntent.putExtra("updateNoteId", String.valueOf(mNote.getId()));
        startActivityForResult(mIntent, 1);
      }

    });

    mNotesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
        final Note mClickedNote = (Note) (parent.getAdapter().getItem(position));
        new AlertDialog.Builder(NotesActivity.this)
            .setTitle(getString(R.string.label_dialog_title))
            .setMessage(getString(R.string.message_delete_comfirm, mClickedNote.getTitle()))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_button_yes), new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                mClickedNote.delete();
                mFlowCursorList.refresh();
                mNotesListViewAdapter.setData(mFlowCursorList);

                EditText mFilterView = (EditText) findViewById(R.id.notesFilter);
                String s = mFilterView.getText().toString();
                mNotesListViewAdapter.getFilter().filter(s);
              }
            })
            .setNegativeButton(getString(R.string.label_button_no), null)
            .show();
        return true;
      }
    });

    // set up filter view
    EditText mFilterView = (EditText) findViewById(R.id.notesFilter);
    mFilterView.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        NotesActivity.this.mNotesListViewAdapter.getFilter().filter(s);
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    // get ACTION_SEND intent
    Intent mGetIntent = getIntent();
    String mAction = mGetIntent.getAction();
    if (Intent.ACTION_SEND.equals(mAction)) {
      Bundle extras = mGetIntent.getExtras();
      if (extras != null) {
        CharSequence mCharSequenceSubject = extras.getCharSequence(Intent.EXTRA_SUBJECT);
        CharSequence mCharSequenceText = extras.getCharSequence(Intent.EXTRA_TEXT);
        if (mCharSequenceText != null) {
          Intent mIntent = new Intent(getApplicationContext(), NoteActivity.class);
          mIntent.putExtra("actionSendSubject", String.valueOf(mCharSequenceSubject));
          mIntent.putExtra("actionSendText", String.valueOf(mCharSequenceText));
          startActivityForResult(mIntent, 2);
        }
      }
    }

  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    String mAction = intent.getAction();
    if (Intent.ACTION_SEND.equals(mAction)) {
      Bundle extras = intent.getExtras();
      if (extras != null) {
        CharSequence mCharSequenceSubject = extras.getCharSequence(Intent.EXTRA_SUBJECT);
        CharSequence mCharSequenceText = extras.getCharSequence(Intent.EXTRA_TEXT);
        if (mCharSequenceText != null) {
          Intent mIntent = new Intent(getApplicationContext(), NoteActivity.class);
          mIntent.putExtra("actionSendSubject", String.valueOf(mCharSequenceSubject));
          mIntent.putExtra("actionSendText", String.valueOf(mCharSequenceText));
          startActivityForResult(mIntent, 3);
        }
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int mId = item.getItemId();

    switch (mId) {
      case R.id.aboutNotes:
        startActivity(new Intent(this, AboutNotesActivity.class));
        return true;

      case R.id.usingOSS:
        startActivity(new Intent(this, UsingOssActivity.class));
        return true;

      default:
        return super.onOptionsItemSelected(item);

    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(mFlowCursorList == null && (requestCode == 0 || requestCode == 3)){
      try {
        mFlowCursorList = new FlowCursorList<Note>(true, new Select().from(Note.class).orderBy(OrderBy.fromProperty(Note_Table.updateDate)));
      } catch (IllegalArgumentException e) {
        mFlowCursorList = null;
      }
    }

    if (mFlowCursorList != null) {
      mFlowCursorList.refresh();
      mNotesListViewAdapter.setData(mFlowCursorList);

      EditText mFilterView = (EditText) findViewById(R.id.notesFilter);
      String s = mFilterView.getText().toString();
      mNotesListViewAdapter.getFilter().filter(s);
    }
  }
}
