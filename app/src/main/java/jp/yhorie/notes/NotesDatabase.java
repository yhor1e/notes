package jp.yhorie.notes;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = NotesDatabase.NAME, version = NotesDatabase.VERSION)
public class NotesDatabase {
        public static final String NAME = "NotesDatabase";
        public static final int VERSION = 6;
}
