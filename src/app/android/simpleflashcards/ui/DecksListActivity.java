package app.android.simpleflashcards.ui;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.simpleflashcards.R;
import app.android.simpleflashcards.SimpleFlashcardsApplication;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.Decks;
import app.android.simpleflashcards.models.ModelsException;


public class DecksListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decks);

		initializeActionbar();
		initializeList();
	}

	@Override
	protected void onResume() {
		super.onResume();

		new LoadDecksTask().execute();
	}

	private void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);

		ImageButton newItemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		newItemCreationButton.setOnClickListener(deckCreationListener);
	}

	private OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call update task
		}
	};

	private OnClickListener deckCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ActivityStarter.start(activityContext, DeckCreationActivity.class);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter decksAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.decks_list_item, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(decksAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	private class LoadDecksTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() {
			setEmptyListText(getString(R.string.loadingDecks));

			listData.clear();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Decks decks = SimpleFlashcardsApplication.getInstance().getDecks();
				addItemsToList(decks.getDecksList());
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noDecks));
			}
			else {
				updateList();
			}

			if (!result.isEmpty()) {
				UserAlerter.alert(activityContext, result);
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.decks_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int itemPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.edit:
				new EditDeckTask(itemPosition).execute();
				return true;

			case R.id.editCards:
				new EditDeckContentsTask(itemPosition).execute();
				return true;

			case R.id.delete:
				new DeleteDeckTask(itemPosition).execute();
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}
	
	private class EditDeckTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;
		
		private int deckId;

		private int deckAdapterPosition;
		
		public EditDeckTask(int deckAdapterPosition) {
			this.deckAdapterPosition = deckAdapterPosition;
		}

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.gettingDeckName));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deckId = getDeck(deckAdapterPosition).getId();
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialogHelper.hide();
			
			if (result.isEmpty()) {
				ActivityMessager.startActivityWithMessage(activityContext, DeckEditingActivity.class,
					deckId);
			}
			else {
				UserAlerter.alert(activityContext, result);
			}
		}
	}
	
	private class EditDeckContentsTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogShowHelper;

		private int deckId;
		private int deckAdapterPosition;
		
		public EditDeckContentsTask(int deckAdapterPosition) {
			this.deckAdapterPosition = deckAdapterPosition;
		}

		@Override
		protected void onPreExecute() {
			progressDialogShowHelper = new ProgressDialogShowHelper();
			progressDialogShowHelper.show(activityContext, getString(R.string.gettingDeckName));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deckId = getDeck(deckAdapterPosition).getId();
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialogShowHelper.hide();

			if (result.isEmpty()) {
				ActivityMessager.startActivityWithMessage(activityContext, CardsListActivity.class, deckId);
			}
			else {
				UserAlerter.alert(activityContext, result);
			}
		}
	}
	
	private class DeleteDeckTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		private int deckAdapterPosition;
		
		public DeleteDeckTask(int deckAdapterPosition) {
			super();

			this.deckAdapterPosition = deckAdapterPosition;
		}

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.deletingDeck));
		}

		@Override
		protected String doInBackground(Void... params) {
			Deck deck = getDeck(deckAdapterPosition);

			try {
				SimpleFlashcardsApplication.getInstance().getDecks().deleteDeck(deck);
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}
			
			listData.remove(deckAdapterPosition);

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.noDecks));
			}
			else {
				updateList();
			}
			
			progressDialogHelper.hide();
			
			if (!result.isEmpty()) {
				UserAlerter.alert(activityContext, result);
			}
		}
	}

	private Deck getDeck(int adapterPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(adapterPosition);

		return (Deck) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}
}