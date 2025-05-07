package de.dhbw.rahmlab.dsl4ga.test.loops;

import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class RealWorldTest {
	private final ImplementationSpecifics specifics = new FastImplSpecifics();
	private final ProgramRunner runner = new ProgramRunner(specifics);
	private final List<String> expectedStrings = new ArrayList<>();
	
	@Test
	public void test1(){
		String code = """
		fn dk(obj, theta1, theta2, theta3, theta4, theta5, theta6){
			// WORKAROUND
			theta[] = {theta1 theta2, theta2, theta3, theta4, theta5, theta6}

			// dh parameters UR5e
			a[] = {0.0, -0.425, -0.3922, 0.0, 0.0, 0.0}
			d[] = {0.1625, 0.0, 0.0, 0.1333, 0.0997, 0.0996}
			alpha[] = {π/2, 0,0, π/2, -π/2, 0}

			M [] = {}
			for (i; 0; 6; 1){
				T_d = 1 - 0.5 d[i] (ε₃ ∧ εᵢ) // Tz // oder GP?
				//R_theta = cos(0.5 theta[i]) - sin(0.5 theta[i]) (ε₁ ∧ ε₂) // Rz // oder GP? 
                R_theta = theta[i]
				T_a = 1 - 0.5 a[i] (ε₁ ∧ εᵢ) // Tx // oder GP? 
				R_alpha = cos(-0.5 alpha[i]) + sin(-0.5 alpha[i]) (ε₂ ∧ ε₃) // Rx // oder GP? 
				M[i] = T_d R_theta T_a R_alpha
			}

			// 1. Mög.
			//objs = M[0]
			//for (i; 1; 6; 1){
			//	objs = objs M[i]
			//}
			//objs = objs obj
			//for (i; 1; 6; 1){
			//	objs = objs M[i]˜
			//}

			// 2. Mög.
			objs = M[5] obj M[5]˜
			for (i; 0; 4; 1){
				objs = M[i] objs M[i]˜
			}

			objs
		}

		fn main() {

			position = dk(1,2,3,4,5,6,7)

			// TODO
			// orientation als bivector?
			// als line durch position?
			orientation = 0

			position, orientation
		}
		""";
		
		runner.parseAndRun(code);
		
		assertTrue(true);
	}
	
	
	@Test
	public void negativeIndexCalc(){
		String code = """
             fn main (){
				x[] = {1,2,3}
                a[] = {32, 48, 12}
				for (i; 2; 0; - 1) {
                    a[i] = x[i] + 12
					x[i] = a[i] + 2
				}
				x[0], x[1], x[2]
			}   
		""";
		
		expectedStrings.add(specifics.createMultivectorString(1));
		expectedStrings.add(specifics.createMultivectorString(16));
		expectedStrings.add(specifics.createMultivectorString(17));
		
		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		
	}
}
