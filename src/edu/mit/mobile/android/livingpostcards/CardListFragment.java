package edu.mit.mobile.android.livingpostcards;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import edu.mit.mobile.android.livingpostcards.data.Card;
import edu.mit.mobile.android.locast.sync.LocastSyncService;

public class CardListFragment extends ListFragment implements LoaderCallbacks<Cursor>,
        OnItemClickListener {

    private static final String[] FROM = { Card.COL_TITLE };
    private static final int[] TO = { android.R.id.text1 };

    private static final String[] PROJECTION = { Card._ID, Card.COL_TITLE };

    private SimpleCursorAdapter mAdapter;

    private final Uri mCards = Card.CONTENT_URI;
    private static final String TAG = CardListFragment.class.getSimpleName();

    public CardListFragment() {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2,
                null, FROM, TO);

        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);
        LocastSyncService.startExpeditedAutomaticSync(getActivity(), mCards);
        registerForContextMenu(getListView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.card_list_fragment, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(getActivity(), mCards, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        mAdapter.swapCursor(c);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Uri item = ContentUris.withAppendedId(mCards, id);
        startActivity(new Intent(Intent.ACTION_VIEW, item));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.activity_card_view, menu);

        menu.findItem(R.id.delete).setVisible(true);
        menu.findItem(R.id.edit).setVisible(true);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (final ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }
        final Uri card = ContentUris.withAppendedId(mCards, info.id);

        switch (item.getItemId()) {
            case R.id.edit:
                startActivity(new Intent(Intent.ACTION_EDIT, card));
                return true;

            case R.id.delete:
                startActivity(new Intent(Intent.ACTION_DELETE, card));
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }
}
