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

package ru.ming13.gambit.ui.task;


import android.os.AsyncTask;
import ru.ming13.gambit.local.model.Deck;


public class CurrentCardIndexChangingTask extends AsyncTask<Void, Void, Void>
{
	private final Deck deck;
	private final int currentCardIndex;

	public static CurrentCardIndexChangingTask newInstance(Deck deck, int currentCardIndex) {
		return new CurrentCardIndexChangingTask(deck, currentCardIndex);
	}

	private CurrentCardIndexChangingTask(Deck deck, int currentCardIndex) {
		this.deck = deck;
		this.currentCardIndex = currentCardIndex;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		deck.setCurrentCardIndex(currentCardIndex);

		return null;
	}
}