package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.List;

// Debugger Variables::Names.
@ExportLibrary(InteropLibrary.class)
public class ScopeVariablesNames implements TruffleObject {

	private final List<String> variablesNames;

	ScopeVariablesNames(List<String> memberNames) {
		this.variablesNames = memberNames;
	}

	@ExportMessage
	boolean hasArrayElements() {
		return true;
	}

	@ExportMessage
	long getArraySize() {
		return variablesNames.size();
	}

	@ExportMessage
	boolean isArrayElementReadable(long index) {
		return 0 <= index && index < variablesNames.size();
	}

	@ExportMessage
	String readArrayElement(long index) {
		return variablesNames.get((int) index);
	}
}
