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

package ru.ming13.gambit.database;

import android.provider.BaseColumns;

public final class DatabaseSchema
{
	private DatabaseSchema() {
	}

	static final String DATABASE_NAME = "gambit.db";

	static final class Versions
	{
		private Versions() {
		}

		public static final int CURRENT = 4;
	}

	public static final class Tables
	{
		private Tables() {
		}

		public static final String DECKS = "Decks";
		public static final String CARDS = "Cards";
	}

	public static final class DecksColumns implements BaseColumns
	{
		private DecksColumns() {
		}

		public static final String TITLE = "title";
		public static final String CURRENT_CARD_INDEX = "current_card_index";
	}

	static final class DecksColumnsParameters
	{
		private DecksColumnsParameters() {
		}

		public static final String _ID = "integer primary key autoincrement not null unique";
		public static final String TITLE = "text not null unique";
		public static final String CURRENT_CARD_INDEX = "int not null";
	}

	public static final class DecksColumnsDefaultValues
	{
		private DecksColumnsDefaultValues() {
		}

		public static final int CURRENT_CARD_INDEX = 0;
	}

	public static final class CardsColumns implements BaseColumns
	{
		private CardsColumns() {
		}

		public static final String DECK_ID = "deck_id";
		public static final String FRONT_SIDE_TEXT = "front_page_side";
		public static final String BACK_SIDE_TEXT = "back_page_side";
		public static final String ORDER_INDEX = "order_index";
	}

	static final class CardsColumnsParameters
	{
		private CardsColumnsParameters() {
		}

		public static final String _ID = "integer primary key autoincrement not null unique";
		public static final String DECK_ID = "integer not null references Decks(_id) on delete cascade";
		public static final String FRONT_SIDE_TEXT = "text not null";
		public static final String BACK_SIDE_TEXT = "text not null";
		public static final String ORDER_INDEX = "int not null";
	}

	public static final class CardsColumnsDefaultValues
	{
		private CardsColumnsDefaultValues() {
		}

		public static final int ORDER_INDEX = 0;
	}
}
