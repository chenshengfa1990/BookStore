package com.bookstore.main;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListLoader;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;
import com.bookstore.provider.Projection;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/26.
 */
public class DBHandler {
    public ArrayList<LoaderItem> loaders;
    private BookListGridListViewAdapter adapter = null;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;

    public DBHandler(BookListGridListViewAdapter adapter) {
        this.adapter = adapter;
        loaders = new ArrayList<LoaderItem>();
    }

    public static void saveBookCategory(final Activity activity, final ArrayList<BookCategory.CategoryItem> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BookCategory.CategoryItem item : list) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DB_Column.BookCategory.Name, item.category_name);
                    contentValues.put(DB_Column.BookCategory.Code, item.category_code);
                    try {
                        activity.getContentResolver().insert(BookProvider.BOOKCATEGORY_URI, contentValues);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void getBookCategory(final Activity activity, final BookCategory category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor result_cursor = null;
                result_cursor = activity.getContentResolver().query(BookProvider.BOOKCATEGORY_URI, null, null, null, null);
                ArrayList<BookCategory.CategoryItem> list = new ArrayList<BookCategory.CategoryItem>();
                if (result_cursor != null) {
                    if (result_cursor.moveToFirst()) {
                        do {
                            String category_name = result_cursor.getString(Projection.BookCategory.COLUMN_NAME);
                            int category_code = result_cursor.getInt(Projection.BookCategory.COLUMN_CODE);
                            list.add(new BookCategory.CategoryItem(category_code, category_name));
                        } while (result_cursor.moveToNext());
                        category.setUser_category_list(list);
                        if (((MainActivity) activity).mGridListViewAdapter != null) {
                            ((MainActivity) activity).mGridListViewAdapter.notifyDataSetChanged();
                        }
                    }
                    result_cursor.close();
                }

            }
        }).start();
    }

    public void loadBookList(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mlistLoader = new BookListLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();

        LoaderItem item = new LoaderItem(mlistLoader, mLoadListener);
        loaders.add(item);
    }

    public ArrayList<LoaderItem> getLoaders() {
        return loaders;
    }

    public static class LoaderItem {
        BookListLoader loader;
        BookListLoadListener listener;

        public LoaderItem(BookListLoader loader, BookListLoadListener listener) {
            this.loader = loader;
            this.listener = listener;
        }
    }

    public class BookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public BookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            Log.i("BookListLoader", "load complete");
            if (data == null || data.getCount() == 0) {
                return;
            }
            adapter.registerDataCursor(data);
            adapter.notifyDataSetChanged();
        }
    }
}
