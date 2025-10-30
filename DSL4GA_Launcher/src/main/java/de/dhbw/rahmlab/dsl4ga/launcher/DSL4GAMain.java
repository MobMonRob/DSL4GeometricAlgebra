package de.dhbw.rahmlab.dsl4ga.launcher;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.orat.math.gacalc.api.GAServiceLoader;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.io.File;
import java.io.IOException;

import org.graalvm.polyglot.Source;

//import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.io.PrintStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.graalvm.polyglot.Context;
//import org.graalvm.polyglot.PolyglotException;
//import org.graalvm.polyglot.Value;

public final class DSL4GAMain {

	/*public static void mainOrig(String[] args) throws IOException {
		if (args.length == 0) {
			throw new IllegalArgumentException("No file has been provided.");
		}

		String file = args[0];

		Source source = Source.newBuilder(Program.LANGUAGE_ID, 
			                     new File(file)).build();

		executeSource(source, null);
	}*/
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			throw new IllegalArgumentException("No file has been provided.");
		}

		/*for (String arg: args){
			System.out.println("arg=\""+arg+"\"");
	    }*/
		
		Map<String, String> options = new HashMap<>();
        String file = null;
        boolean launcherOutput = true;
        for (String arg : args) {
            if (arg.equals("--disable-launcher-output")) {
                launcherOutput = false;
            } else if (parseOption(options, arg)) {
                continue;
            } else {
                if (file == null) {
                    file = arg;
                }
            }
        }
		/*for (String key: options.keySet()){
			System.out.println("key=\""+key+"\""+" parsedProgram=\""+options.get(key)+"\"");
	    }*/
		//Source source = Source.newBuilder(Program.LANGUAGE_ID, 
		//	                     new File(file)).build();

		Source source = null;
		if (file == null) {
            // @formatter:off
			if (!options.containsKey("lsp") || !options.get("lsp").equals("true")){
				source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID,
					new InputStreamReader(System.in),
					"<stdin>").interactive(!launcherOutput).build();
			} else {
				System.out.println("No source because LSP-Server should be started!");
			}
            // @formatter:on
        } else {
			source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID,
				new File(file)).interactive(!launcherOutput).build();
        }
		 
		executeSource(source, options);
			
	    //System.exit(executeSource(source, System.in, System.out, 
		//	options, launcherOutput));
	}
	
	/*
	public static void main2(String[] args) throws IOException {
		
		Source source;
        Map<String, String> options = new HashMap<>();
        String file = null;
        boolean launcherOutput = true;
        for (String arg : args) {
            if (arg.equals("--disable-launcher-output")) {
                launcherOutput = false;
            } else if (parseOption(options, arg)) {
                continue;
            } else {
                if (file == null) {
                    file = arg;
                }
            }
        }

        if (file == null) {
            // @formatter:off
            source = Source.newBuilder(Program.LANGUAGE_ID, 
				new InputStreamReader(System.in),
				"<stdin>").interactive(!launcherOutput).build();
            // @formatter:on
        } else {
            source = Source.newBuilder(Program.LANGUAGE_ID, 
				new File(file)).interactive(!launcherOutput).build();
        }

        System.exit(executeSource(source, System.in, System.out, 
			options, launcherOutput));
	}*/

	/**
	 * 
	 * @param source == null if LSP should be started
	 * @param options 
	 */
	private static void executeSource(Source source, Map<String, String> options) {
		var fac = new TruffleProgramFactory();
		var program = fac.parse(source.getURL());

		//TODO arguments die der main()-Methode mitgegeben werden sollen
		var arguments = new ArrayList<SparseDoubleMatrix>();

		List<SparseDoubleMatrix> answer = program.invoke(arguments);

		System.out.println("answer: ");
		answer.forEach(System.out::println);
		System.out.println();
	}
	
	//FIXME funktioniert nicht, da der ParsingService im constructor von Programm gesetzt wird
	// und hier Program nicht verwendet wird!!!
	/*private static int executeSource(Source source, InputStream in, PrintStream out, Map<String,									 String> options, boolean launcherOutput) {
        Context context;
        PrintStream err = System.err;
        try {
            context = Context.newBuilder(Program.LANGUAGE_ID).in(in).out(out). 
				                       options(options).allowAllAccess(true).build();
        } catch (IllegalArgumentException e) {
            err.println(e.getMessage());
            return 1;
        }

        if (launcherOutput) {
            out.println("== running on " + context.getEngine());
        }

        try {
            Value result = context.eval(source);
			if (context.getBindings(Program.LANGUAGE_ID).getMember("main") 
				                      == null) {
                err.println("No function main() defined in GL source file.");
                return 1;
            }
            if (launcherOutput && !result.isNull()) {
                out.println(result.toString());
            }
            return 0;
        } catch (PolyglotException ex) {
            if (ex.isInternalError()) {
                // for internal errors we print the full stack trace
                ex.printStackTrace();
            } else {
                err.println(ex.getMessage());
            }
            return 1;
        } finally {
            context.close();
        }
    }*/
	
	/**
	 * Parse option from a given argument.
	 * 
	 * The option can be be property-value-pair e.g. "a=true". If there is no "=" than the value of the
	 * option is automatically set to "true".<p>
	 * 
	 * @param options map to save the parsed option
	 * @param arg arg has to start with "--" to be interpreted as an option
	 * @return false, if arg does not start by "--"
	 */
    private static boolean parseOption(Map<String, String> options, String arg) {
        if (arg.length() <= 2 || !arg.startsWith("--")) {
            return false;
        }
        int eqIdx = arg.indexOf('=');
        String key;
        String value;
        if (eqIdx < 0) {
            key = arg.substring(2);
            value = null;
        } else {
            key = arg.substring(2, eqIdx);
            value = arg.substring(eqIdx + 1);
        }

        if (value == null) {
            value = "true";
        }
        int index = key.indexOf('.');
        String group = key;
        if (index >= 0) {
            group = group.substring(0, index);
        }
        options.put(key, value);
        return true;
    }
}
