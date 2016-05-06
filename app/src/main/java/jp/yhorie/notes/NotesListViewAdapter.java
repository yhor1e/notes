package jp.yhorie.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.yhorie.template.R;
import com.raizlabs.android.dbflow.list.FlowCursorList;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NotesListViewAdapter extends BaseAdapter implements Filterable {

  Context mContext;
  LayoutInflater mLayoutInflater = null;
  FlowCursorList<Note> mFlowCursorList;
  ArrayList<Note> mNotesfilteredList = new ArrayList<Note>();
  SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public NotesListViewAdapter(Context mContext, FlowCursorList<Note> mFlowCursorList) {
    this.mContext = mContext;
    this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.mFlowCursorList = mFlowCursorList;
    if (mFlowCursorList != null) {
      for (int i = 0; i < mFlowCursorList.getCount(); i++) {
        mNotesfilteredList.add(mFlowCursorList.getItem(i));
      }
    } else {
      mNotesfilteredList = null;
    }
  }

  @Override
  public int getCount() {
    if (mNotesfilteredList == null) {
      return 0;
    }
    return mNotesfilteredList.size();
  }

  @Override
  public Object getItem(int position) {
    return mNotesfilteredList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View mView = convertView == null ?
        mLayoutInflater.inflate(R.layout.listview_item_note, parent, false) :
        convertView ;

    ((TextView) mView.findViewById(R.id.listItemTitle))
        .setText(mNotesfilteredList.get(position).getTitle());
    ((TextView) mView.findViewById(R.id.listItemDetail))
        .setText(mNotesfilteredList.get(position).getDetail());

    Date mCreateDate = mNotesfilteredList.get(position).getCreateDate();
    Date mUpdateDate = mNotesfilteredList.get(position).getUpdateDate();

    ((TextView) mView.findViewById(R.id.listItemDate))
        .setText(mSimpleDateFormat.format(mUpdateDate != null ? mUpdateDate : mCreateDate));

    return mView;
  }


  private Filter filterInstance = null;

  @Override
  public Filter getFilter() {

    if (null == filterInstance) {
      filterInstance = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
          FilterResults mFilterResults = new FilterResults();
          ArrayList<Note> mFilteredList = new ArrayList<Note>();

          if (mFlowCursorList != null) {
            constraint = constraint.toString().toLowerCase();
            for (int i = 0; i < mFlowCursorList.getCount(); i++) {
              Note mNote = mFlowCursorList.getItem(i);
              if (mNote.toString().toLowerCase().contains(constraint.toString())) {
                mFilteredList.add(mNote);
              }
            }
            mFilterResults.count = mFilteredList.size();
            mFilterResults.values = mFilteredList;
          } else {
            mFilterResults.count = 0;
            mFilterResults.values = null;
          }
          return mFilterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          if (results.values != null) {
            mNotesfilteredList = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
          }
        }
      };
    }

    return filterInstance;
  }

  public void setData(FlowCursorList<Note> flowCursorList) {
    this.mFlowCursorList = flowCursorList;
  }
}
