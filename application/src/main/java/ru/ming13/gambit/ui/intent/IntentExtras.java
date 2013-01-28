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

package ru.ming13.gambit.ui.intent;


public final class IntentExtras
{
	private IntentExtras() {
	}

	private static final String EXTRA_PREFIX;

	public static final String DECK_URI;
	public static final String CARDS_URI;
	public static final String CARD_URI;

	static {
		EXTRA_PREFIX = IntentFactory.class.getPackage().getName();

		DECK_URI = buildIntentExtraName(ExtraPostfixes.DECK_URI);
		CARDS_URI = buildIntentExtraName(ExtraPostfixes.CARDS_URI);
		CARD_URI = buildIntentExtraName(ExtraPostfixes.CARD_URI);
	}

	private static String buildIntentExtraName(String extraPostfix) {
		return String.format("%s.%s", EXTRA_PREFIX, extraPostfix);
	}

	private static final class ExtraPostfixes
	{
		private ExtraPostfixes() {
		}

		public static final String DECK_URI = "deck_uri";
		public static final String CARDS_URI = "cards_uri";
		public static final String CARD_URI = "card_uri";
	}
}
