package com.example.mycontentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mycontentprovider.db.ToDoListDBAdapter;

public class ToDoProvider extends ContentProvider {

    private static final String TAG = "ContentProviderDemo";
    /** The authority of this content provider. */
    public static final String AUTHORITY = "com.example.mycontentprovider.provider";


    public static final String PATH_TODO_LIST = ToDoListDBAdapter.DB_NAME; // Complete (Select All..)
    public static final String PATH_TODO_PLACE = "TODO_LIST_FROM_PLACE"; // Partial (Project some..)
    public static final String PATH_TODO_COUNT = "TODOS_COUNT";

    /** The URIs for the ToDoList table. */
    public static final Uri CONTENT_URI_1 = Uri.parse("content://" + AUTHORITY + "/" + PATH_TODO_LIST);
    public static final Uri CONTENT_URI_2 = Uri.parse("content://" + AUTHORITY + "/" + PATH_TODO_PLACE);
    public static final Uri CONTENT_URI_3 = Uri.parse("content://" + AUTHORITY + "/" + PATH_TODO_COUNT);

    /** The match code for some items (zero or more values) in the ToDoList table. */
    public static final int CODE_TODOS_LIST_DIR = 1;
    /** The match code for some items (zero or more values) in the ToDoList table. */
    public static final int CODE_TODOS_FROM_SPECIFIC_PLACE_DIR = 2;
    /**  The match code for an item (one at most) in the ToDoList table. */
    public static final int CODE_TODOS_COUNT_ITEM = 3;


    /** The URI matcher. */
    public static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    // TODO: Since All of fields values are static > better to initialize them in static block;
    static {
        // Associate the "Constants" with the "Authority & Path";
        MATCHER.addURI(AUTHORITY, PATH_TODO_LIST, CODE_TODOS_LIST_DIR);
        MATCHER.addURI(AUTHORITY, PATH_TODO_PLACE, CODE_TODOS_FROM_SPECIFIC_PLACE_DIR);
        MATCHER.addURI(AUTHORITY, PATH_TODO_COUNT, CODE_TODOS_COUNT_ITEM);
    }

    // Android Suggestion for Construct MIME_TYPE :>
    // "Standard_Android_MIME_TYPE" + "/" + "vnd." + "Reverse_Domain_Name." + "Any_Custom_String";
    public static final String MIME_TYPE_1 = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + ToDoListDBAdapter.DB_NAME;
    public static final String MIME_TYPE_2 = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + "todos.place";
    public static final String MIME_TYPE_3 = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + "todosCount";

    private ToDoListDBAdapter toDoListDBAdapter;


    @Override
    // TODO:
    public boolean onCreate() {
        toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(this.getContext());
        return true;
    }

    @Nullable
    @Override
    // TODO: Which returns data to the caller.
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (MATCHER.match(uri)) {
            case CODE_TODOS_LIST_DIR: cursor = toDoListDBAdapter.getCursorForAllToDos(); break;
            case CODE_TODOS_FROM_SPECIFIC_PLACE_DIR: cursor = toDoListDBAdapter.getCursorForSpecificPlace(
                    selectionArgs!=null && selectionArgs.length>0? selectionArgs[0]: null
            ); break;
            case CODE_TODOS_COUNT_ITEM: cursor = toDoListDBAdapter.getCount(); break;
            default: cursor = null; break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_TODOS_LIST_DIR: return MIME_TYPE_1;
            case CODE_TODOS_FROM_SPECIFIC_PLACE_DIR: return MIME_TYPE_2;
            case CODE_TODOS_COUNT_ITEM: return MIME_TYPE_3;
        }
        return null;
    }

    @Nullable
    @Override
    // The URI for the newly inserted item. & it's commonly been the = "context:// URI..." + "the row ID of the newly inserted row";
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) throws UnsupportedOperationException {
        Uri returnUri = null;
        Log.d(TAG, "insert: Provider APP");
        switch (MATCHER.match(uri)) {
            case CODE_TODOS_LIST_DIR:
                returnUri = insertToDo(uri, values); break;
            default:
                throw new UnsupportedOperationException("insert operation not supported");
        }

        return returnUri;
    }

    private Uri insertToDo(Uri uri, ContentValues values)
    {
        long id = toDoListDBAdapter.insert(values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse("content://" + AUTHORITY + PATH_TODO_LIST + id);
    }

    @Override
    // Returns: the number of rows affected;
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return toDoListDBAdapter.delete(selection, selectionArgs);
    }

    @Override
    // Returns: the number of rows affected;
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return toDoListDBAdapter.update(values, selection, selectionArgs);
    }

}
