package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.TruffleFile;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GAFileTypeDetector implements TruffleFile.FileTypeDetector {

	@Override
	public String findMimeType(TruffleFile file) throws IOException {
		//return file.detectMimeType();
		// so geht das vermutlich nicht, da hier auch wieder der GAFileTypeDetector verwendet wird???
	}

	@Override
	public Charset findEncoding(TruffleFile file) throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}
	
}
