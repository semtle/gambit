/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardAssembledEvent;
import ru.ming13.gambit.bus.CardSavedEvent;
import ru.ming13.gambit.bus.OperationCancelledEvent;
import ru.ming13.gambit.fragment.CardEditingFragment;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.task.CardEditingTask;
import ru.ming13.gambit.util.Fragments;
import ru.ming13.gambit.util.Intents;
import ru.ming13.gambit.util.OperationBar;

public class CardEditingActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operation);

		setUpBar();
		setUpFragment();
	}

	private void setUpBar() {
		OperationBar.at(this).show();
	}

	private void setUpFragment() {
		Fragments.Operator.at(this).set(buildFragment(), R.id.container_operation);
	}

	private Fragment buildFragment() {
		return CardEditingFragment.newInstance(getCard());
	}

	private Card getCard() {
		return getIntent().getParcelableExtra(Intents.Extras.CARD);
	}

	@Subscribe
	public void onOperationCancelled(OperationCancelledEvent event) {
		finish();
	}

	@Subscribe
	public void onCardAssembled(CardAssembledEvent event) {
		saveCard(event.getCard());
	}

	private void saveCard(Card card) {
		CardEditingTask.execute(getContentResolver(), getDeck(), card);
	}

	private Deck getDeck() {
		return getIntent().getParcelableExtra(Intents.Extras.DECK);
	}

	@Subscribe
	public void onCardSaved(CardSavedEvent event) {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}