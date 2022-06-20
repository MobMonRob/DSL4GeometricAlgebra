/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

/**
 *
 * @author fabian
 */
public class Current_ICGAMultivector_Processor {

	// This is not nice. But I was not able to come up with a better solution
	//   to propagate an instance of CGAMultivector_Processor_Generic through
	//   the language boundary.
	// Another - likewise not nice - way would have been another CGAMultivector class
	//   with an assigned processor.
	// A possible way could be dependency injection into the language class. But can this be set from an
	//   api with can be exposed?
	// Two different ICGAMultivector<T> instances returned from methods of cga_processor always
	//   have T identically assigned. Therefore no compatibility check needs to be done.
	public static CGAMultivector_Processor_Generic cga_processor = null;
}
