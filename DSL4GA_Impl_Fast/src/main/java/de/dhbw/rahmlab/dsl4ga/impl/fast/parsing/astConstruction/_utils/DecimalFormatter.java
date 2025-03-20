package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DecimalFormatter {
	public static DecimalFormat initDecimalFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');

		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(symbols);
		return format;
	}

}
