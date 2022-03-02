package cz.uhk.fim.hladeja1.pgrf3.utils;

import cz.uhk.fim.pgrf.transforms.Mat4;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Kontrola podpůrné knihovničky pro rozšířenou práci s maticemi
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public class TestMat4Utils {
	
	// vstupní matice
	protected Mat4 testA  = new Mat4(new double[][] {{1,2,3,4}, {3,4,5,1}, {3,4,3,11}, {7,8,3,14}});
	
	// transponovaná matice
	protected Mat4 testAT = new Mat4(new double[][] {{1,3,3,7}, {2,4,3,8}, {3,5,3,3}, {4,1,11,14}});
	
	// odpovídající LU rozklad
	protected Mat4 testL  = new Mat4(new double[][] {{1,0,0,0}, {3,1,0,0}, {3,1,1,0}, {7,3,3,1}});
	protected Mat4 testU  = new Mat4(new double[][] {{1,2,3,4}, {0,-2,-4,-11}, {0,0,-2,10}, {0,0,0,-11}});

	// determinant matice
	protected double detTestA = -44.0;
		
	// inverzní matice
	protected Mat4 testAinv = new Mat4(new double[][] {
			{-4.1818,1.5,2.7727,-1.0909},
			{4.3182,-1.5,-3.2273,1.4091},
			{-0.9091,0.5,0.8636,-0.4545},
			{-0.1818,0.0,0.2727,-0.0909}});
	
	@Test
	public void testTranspose() {
		Assert.assertEquals(this.testAT.toString("4.1f"), Mat4Utils.transpose(testA).toString("4.1f"));
	}
	
	@Test
	public void testLdecomp() {
		Assert.assertEquals(this.testL.toString("4.1f"), Mat4Utils.decompL(testA).toString("4.1f"));
	}
	
	@Test
	public void testUdecomp() {
		Assert.assertEquals(this.testU.toString("4.1f"), Mat4Utils.decompU(testA).toString("4.1f"));
	}
	
	@Test
	public void testDeterminant() {
		Assert.assertEquals(this.detTestA, Mat4Utils.det(testA));
	}
	
	@Test
	public void testInverse() {
		Assert.assertEquals(this.testAinv.toString("4.1f"), Mat4Utils.inverse(testA).toString("4.1f"));
	}
	
}
