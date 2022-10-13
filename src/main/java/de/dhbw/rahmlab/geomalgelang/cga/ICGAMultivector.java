/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

import com.oracle.truffle.api.interop.TruffleObject;

// "implements TruffleObject" is only a temporary workaround
public class ICGAMultivector<T> implements TruffleObject {

	protected final T inner;

	public ICGAMultivector(T t) {
		this.inner = t;
	}

	public T getInner() {
		return this.inner;
	}
}
