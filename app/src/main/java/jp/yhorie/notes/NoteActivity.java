package jp.yhorie.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhorie.template.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class NoteActivity extends AppCompatActivity {

  private Note mNote;
  private String mUpdateNoteId = null;
  private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_note);

    Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Intent mIntent = getIntent();
    mUpdateNoteId = mIntent.getStringExtra("updateNoteId");

    FloatingActionButton mDeleteButtonView = (FloatingActionButton) findViewById(R.id.noteDeleteButton);

    if (mUpdateNoteId == null) {
      // ready to create
      mDeleteButtonView.setVisibility(View.INVISIBLE);
      mNote = new Note();
    } else {
      // ready to update
      mDeleteButtonView.setVisibility(View.VISIBLE);

      mNote = SQLite.select().from(Note.class).where(Note_Table.id.eq(Integer.valueOf(mUpdateNoteId))).querySingle();
      ((EditText) findViewById(R.id.noteTitle)).setText(mNote.getTitle());
      ((EditText) findViewById(R.id.noteDetail)).setText(mNote.getDetail());

      if (mNote.getCreateDate() != null) {
        ((TextView) findViewById(R.id.noteCreateDate))
            .setText(this.getString(R.string.create_date_template, mSimpleDateFormat.format(mNote.getCreateDate())));
      }
      if (mNote.getUpdateDate() != null) {
        ((TextView) findViewById(R.id.noteUpdateDate))
            .setText(this.getString(R.string.update_date_template, mSimpleDateFormat.format(mNote.getUpdateDate())));
      }
    }


    mDeleteButtonView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(NoteActivity.this, getString(R.string.message_how_to_delete), Toast.LENGTH_LONG).show();
      }
    });
    mDeleteButtonView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        new AlertDialog.Builder(NoteActivity.this)
            .setTitle(getString(R.string.label_dialog_title))
            .setMessage(getString(R.string.message_delete_comfirm, mNote.getTitle()))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_button_yes), new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                mNote.delete();
                Toast.makeText(NoteActivity.this, getString(R.string.message_deleteted, mNote.getTitle()), Toast.LENGTH_LONG).show();
                finish();
              }
            })
            .setNegativeButton(getString(R.string.label_button_no), null)
            .show();

        return true;
      }
    });


    FloatingActionButton mRegisterButtonView = (FloatingActionButton) findViewById(R.id.noteRegistButton);
    mRegisterButtonView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        EditText titleEditText = (EditText) findViewById(R.id.noteTitle);
        mNote.setTitle(titleEditText.getText().toString());

        EditText detailEditText = (EditText) findViewById(R.id.noteDetail);
        mNote.setDetail(detailEditText.getText().toString());

        if (null == mUpdateNoteId) {
          // create new note
          mNote.setCreateDate(new Date(System.currentTimeMillis()));
          mNote.save();

        } else {
          // update note
          mNote.setUpdateDate(new Date(System.currentTimeMillis()));
          mNote.update();
        }
        finish();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    boolean result = true;

    switch (id) {
      case android.R.id.home:
        finish();
        break;
      default:
        result = super.onOptionsItemSelected(item);
    }

    return result;
  }
}
