/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLang;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author fabian
 */
@ExportLibrary(InteropLibrary.class)
public final class GlobalVariableScope implements TruffleObject {

	private final Map<String, Object> variables = new HashMap<>();

	public boolean newVariable(String name) {
		Object existingValue = this.variables.putIfAbsent(name, null);
		return existingValue == null;
	}

	public boolean assignVariable(String name, Object value) {
		Object existingValue = this.variables.replace(name, value);
		return existingValue != null;
	}

	public Object getVariable(String name) {
		return this.variables.get(name);
	}

	public boolean isVariablePresent(String name) {
		return this.variables.containsKey(name);
	}

	public boolean isVariableAssigned(String name) {
		return this.variables.get(name) != null;
	}

	// necessary for context.getBindings.putMember
	@ExportMessage
	public void writeMember(String member, Object value) throws UnsupportedMessageException, UnknownIdentifierException, UnsupportedTypeException {
		InputValidator.ensureIsValidVariableName(member);
		InputValidator.ensureIsCGA(value);
		this.newVariable(member);
		this.assignVariable(member, value);
	}

	@ExportMessage
	public boolean isMemberInsertable(String member) {
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#isMemberInsertable-java.lang.Object-java.lang.String-
		//Or isVariableAssigned??
		return !this.isVariablePresent(member);
	}

	@ExportMessage
	public boolean isMemberModifiable(String member) {
		//https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#isMemberModifiable-java.lang.Object-java.lang.String-
		return this.isVariablePresent(member);
	}

	@ExportMessage
	boolean isScope() {
		return true;
	}

	@ExportMessage
	boolean hasMembers() {
		return true;
	}

	@ExportMessage
	Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
		return new GlobalVariableNamesObject(this.variables.keySet());
	}

	@ExportMessage
	boolean isMemberReadable(String member) {
		return this.variables.containsKey(member);
	}

	@ExportMessage
	Object readMember(String member) throws UnknownIdentifierException {
		Object value = this.variables.get(member);
		if (null == value) {
			throw UnknownIdentifierException.create(member);
		}
		return value;
	}

	@ExportMessage
	Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
		return "global";
	}

	@ExportMessage
	boolean hasLanguage() {
		return true;
	}

	@ExportMessage
	Class<? extends TruffleLanguage<?>> getLanguage() {
		return GeomAlgeLang.class;
	}
}

@ExportLibrary(InteropLibrary.class)
final class GlobalVariableNamesObject implements TruffleObject {

	private final List<String> names;

	GlobalVariableNamesObject(Set<String> names) {
		this.names = new ArrayList<>(names);
	}

	@ExportMessage
	boolean hasArrayElements() {
		return true;
	}

	@ExportMessage
	long getArraySize() {
		return this.names.size();
	}

	@ExportMessage
	boolean isArrayElementReadable(long index) {
		return index >= 0 && index < this.names.size();
	}

	@ExportMessage
	Object readArrayElement(long index) throws InvalidArrayIndexException {
		if (!this.isArrayElementReadable(index)) {
			throw InvalidArrayIndexException.create(index);
		}
		return this.names.get((int) index);
	}
}
