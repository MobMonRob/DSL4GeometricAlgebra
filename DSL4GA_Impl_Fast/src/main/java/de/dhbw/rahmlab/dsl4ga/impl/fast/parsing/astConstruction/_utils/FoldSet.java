package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import java.util.HashMap;
import java.util.HashSet;


public class FoldSet {
	private HashSet<String> set;
	private HashMap<String, Integer> deletions;

	public FoldSet() {
		set = new HashSet<>();
		deletions = new HashMap<>();
	}
	
	public boolean contains(String name, Integer line){
		return set.contains(name) && line>deletions.getOrDefault(name, -1);
	}
	
	public void remove(String name, Integer line){
		set.remove(name);
		deletions.put(name, line);
	}	

	public void removeIfContained(String name, int line) {
		if (contains(name, line)) remove(name, line);
	}

	public void add(String name) {
		set.add(name);
	}
}
