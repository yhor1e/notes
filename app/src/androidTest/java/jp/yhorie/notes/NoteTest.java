package jp.yhorie.notes;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;

import org.junit.Before;
import org.junit.Test;


import java.lang.System;
import java.sql.Date;

import static org.junit.Assert.*;

public class NoteTest {

  private String title = "mNote title";
  private String detail = "I should be honest";
  private Date createDate = new Date(System.currentTimeMillis());
  private Note mNote;

  @Before
  public void setUp() throws Exception {
    Delete.table(Note.class, new SQLCondition[]{Note_Table.id.isNotNull()});
    mNote = new Note();
  }

  @Test
  public void testGetId() throws Exception {
    assertNotNull(mNote.getId());
  }

  @Test
  public void testSetAndGetTitle() throws Exception {
    mNote.setTitle(title);
    assertEquals(title, mNote.getTitle());
  }

  @Test
  public void testSetAndGetDetail() throws Exception {
    mNote.setDetail(detail);
    assertEquals(detail, mNote.getDetail());
  }

  @Test
  public void testSetAndGetCreateDate() throws Exception {
    mNote.setCreateDate(createDate);
    assertEquals(createDate, mNote.getCreateDate());
  }

  @Test
  public void testSetAndGetUpdateDate() throws Exception {
    Date d = new Date(System.currentTimeMillis());
    mNote.setUpdateDate(d);
    assertEquals(d, mNote.getUpdateDate());
  }

}