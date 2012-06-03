package app.android.gambit.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.gambit.R;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import com.actionbarsherlock.view.Menu;


public class DecksListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		initializeList();
	}

	@Override
	protected void initializeList() {
		SimpleAdapter decksAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_one_line, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(decksAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getListView().setOnItemLongClickListener(actionModeListener);
		}
		else {
			registerForContextMenu(getListView());
		}
	}

	private final AdapterView.OnItemLongClickListener actionModeListener = new AdapterView.OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
			startActionMode(new ActionModeCallback(position));

			return true;
		}
	};

	private class ActionModeCallback implements ActionMode.Callback
	{
		private int selectedItemPosition;

		public ActionModeCallback(int selectedItemPosition) {
			this.selectedItemPosition = selectedItemPosition;
		}

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, android.view.Menu menu) {
			actionMode.getMenuInflater().inflate(R.menu.menu_context_decks, menu);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, android.view.Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
			switch (menuItem.getItemId()) {
				case R.id.menu_rename:
					callDeckEditing(selectedItemPosition);
					actionMode.finish();
					return true;

				case R.id.menu_delete:
					callDeckDeleting(selectedItemPosition);
					actionMode.finish();
					return true;

				case R.id.menu_edit_cards:
					callCardsEditing(selectedItemPosition);
					actionMode.finish();
					return true;

				default:
					return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDecks();
	}

	private void loadDecks() {
		new LoadDecksTask().execute();
	}

	private class LoadDecksTask extends AsyncTask<Void, Void, Void>
	{
		private List<Deck> decks;

		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loading_decks));
		}

		@Override
		protected Void doInBackground(Void... params) {
			decks = DbProvider.getInstance().getDecks().getDecksList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (decks.isEmpty()) {
				setEmptyListText(getString(R.string.empty_decks));
			}
			else {
				fillList(decks);
				updateList();
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Deck deck = (Deck) itemData;

		HashMap<String, Object> deckItem = new HashMap<String, Object>();

		deckItem.put(LIST_ITEM_TEXT_ID, deck.getTitle());
		deckItem.put(LIST_ITEM_OBJECT_ID, deck);

		listData.add(deckItem);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		getMenuInflater().inflate(R.menu.menu_context_decks, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int deckPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.menu_rename:
				callDeckEditing(deckPosition);
				return true;

			case R.id.menu_edit_cards:
				callCardsEditing(deckPosition);
				return true;

			case R.id.menu_delete:
				callDeckDeleting(deckPosition);
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callDeckEditing(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createDeckEditingIntent(activityContext, deck);
		startActivity(callIntent);
	}

	private Deck getDeck(int deckPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(deckPosition);

		return (Deck) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	private void callCardsEditing(int deckPosition) {
		Deck deck = getDeck(deckPosition);

		Intent callIntent = IntentFactory.createCardsListIntent(activityContext, deck);
		startActivity(callIntent);
	}

	private void callDeckDeleting(int deckPosition) {
		new DeleteDeckTask(deckPosition).execute();
	}

	private class DeleteDeckTask extends AsyncTask<Void, Void, Void>
	{
		private final int deckPosition;
		private final Deck deck;

		public DeleteDeckTask(int deckPosition) {
			super();

			this.deckPosition = deckPosition;
			this.deck = getDeck(deckPosition);
		}

		@Override
		protected void onPreExecute() {
			listData.remove(deckPosition);
			updateList();

			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.empty_decks));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			DbProvider.getInstance().getDecks().deleteDeck(deck);

			return null;
		}
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		callDeckViewing(position);
	}

	private void callDeckViewing(int deckPosition) {
		new ViewDeckTask(deckPosition).execute();
	}

	private class ViewDeckTask extends AsyncTask<Void, Void, String>
	{
		private final Deck deck;

		public ViewDeckTask(int deckPosition) {
			this.deck = getDeck(deckPosition);
		}

		@Override
		protected String doInBackground(Void... params) {
			if (deck.isEmpty()) {
				return getString(R.string.empty_cards);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (errorMessage.isEmpty()) {
				callCardsViewing(deck);
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}

		private void callCardsViewing(Deck deck) {
			Intent callIntent = IntentFactory.createCardsViewingIntent(activityContext, deck);
			startActivity(callIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_decks_and_cards, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_create_item:
				callDeckCreation();
				return true;

			case R.id.menu_sync:
				// TODO: Call decks updating
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void callDeckCreation() {
		Intent callIntent = IntentFactory.createDeckCreationIntent(activityContext);
		activityContext.startActivity(callIntent);
	}
}
