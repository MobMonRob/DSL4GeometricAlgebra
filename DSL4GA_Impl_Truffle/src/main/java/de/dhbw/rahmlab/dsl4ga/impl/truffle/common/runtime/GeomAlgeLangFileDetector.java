package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.TruffleFile;
import java.io.IOException;
import java.nio.charset.Charset;

public class GeomAlgeLangFileDetector implements TruffleFile.FileTypeDetector {

	@Override
	public String findMimeType(TruffleFile file) throws IOException {
		String name = file.getName();
		if (name != null && name.endsWith(GeomAlgeLang.FILE_ENDING)) {
			return GeomAlgeLang.MIME_TYPE;
		}
		return null;
	}

	@Override
	public Charset findEncoding(TruffleFile file) throws IOException {
		return null;
	}
}
