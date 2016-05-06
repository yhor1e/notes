package jp.yhorie.notes;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.sql.Date;
import java.text.SimpleDateFormat;

@ModelContainer
@Table(database = NotesDatabase.class)
public class Note extends BaseModel {

  @PrimaryKey(autoincrement = true)
  private long id;

  @Column
  private String title;

  @Column
  private String detail;

  @Column
  private Date createDate;

  @Column
  private Date updateDate;

  public long getId() {
    return id;
  }

  public void setId(long mId) {
    id = mId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String mTitle) {
    title = mTitle;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String mDetail) {
    detail = mDetail;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date mCreateDate) {
    createDate = mCreateDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date mUpdateDate) {
    updateDate = mUpdateDate;
  }

  @Override
  public String toString() {
    String mRet;
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    mRet = getTitle() != null ? getTitle() : "";
    mRet = mRet + (getDetail() != null ? getDetail() : "");
    mRet = mRet + (getCreateDate() != null ? mSimpleDateFormat.format(getCreateDate()) : "");
    mRet = mRet + (getUpdateDate() != null ? mSimpleDateFormat.format(getUpdateDate()) : "");
    return mRet;
  }
}

