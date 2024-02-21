package de.dhbw.rahmlab.geomalgelang.parsing;

public class ParsingServiceProvider {

	private static volatile iParsingService parsingService;

	public static synchronized void setParsingService(iParsingService parsingService) {
		ParsingServiceProvider.parsingService = parsingService;
	}

	public static iParsingService getParsingService() {
		return ParsingServiceProvider.parsingService;
	}
}
