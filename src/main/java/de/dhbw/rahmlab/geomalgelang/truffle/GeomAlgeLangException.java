/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

/**
 *
 * @author fabian
 */
public final class GeomAlgeLangException extends AbstractTruffleException {

	public GeomAlgeLangException(String message) {
		this(null, message);
	}

	public GeomAlgeLangException(Node location, String message) {
		super(message, location);
	}
}
