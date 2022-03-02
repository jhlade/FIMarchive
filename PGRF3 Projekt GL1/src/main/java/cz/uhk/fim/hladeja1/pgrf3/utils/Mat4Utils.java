package cz.uhk.fim.hladeja1.pgrf3.utils;

import cz.uhk.fim.pgrf.transforms.Mat4;

/**
 * Statické rozšíření práce s maticemi o výpočet determinantu, transpozici
 * a inverzi.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * @version 2016.1
 */
public class Mat4Utils {

	/**
	 * Sloupcový vektor 1x4
	 */
	static class Vec4r {

		// sloupec
		private double[] vec = new double[4];

		public Vec4r() {
			this(0.0);
		}

		public Vec4r(Vec4r vec) {
			this.vec = vec.toDouble();
		}

		public Vec4r(double[] vec) {
			this.vec = vec;
		}

		public Vec4r(double k) {
			this.vec[0] = k;
			this.vec[1] = k;
			this.vec[2] = k;
			this.vec[3] = k;
		}

		public double[] toDouble() {
			return this.vec;
		}

	}

	/**
	 * Rozšířená matice umožňující rozklady, inverze a transpozice
	 * a při troše štěstí a úprav i řešení soustav lineárních rovnic
	 * - tady se počítá s tím, že transformační matice nejsou singulární.
	 * 
	 * Přímý přístup k atributu Mat4.mat.
	 */
	static class Mat4e extends Mat4 {

		// matice LU-dekompozice
		private double[][] L = new double[4][4];
		private double[][] U = new double[4][4];
		// TODO permutační matice pro LUP rozklad
		private double[][] P = new double[4][4];

		// LU rozklad hned v konstruktoru
		// TODO hrozí dělení nulou při singularitě!
		public Mat4e(Mat4 A) {
			super(A);

			int k, j, i;

			// inicializace
			for (k = 0; k < 4; k++) {
				for (i = 0; i < 4; i++) {
					U[i][k] = mat[i][k];
					L[i][k] = (i == k) ? 1.0 : 0.0;
					P[i][k] = (i == k) ? 1.0 : 0.0;
				}
			}

			// vlastní základní LU rozklad
			for (k = 0; k < 4; k++) {
				for (i = k + 1; i < 4; i++) {
					L[i][k] = U[i][k] / U[k][k];
					for (j = k; j < 4; j++) {
						U[i][j] = U[i][j] - L[i][k] * U[k][j];
					}
				}
			}
		}

		/**
		 * Konstruktor bez provedení LU rozkladu
		 *
		 * @param A
		 * @param dummy
		 */
		public Mat4e(Mat4 A, boolean dummy) {
			super(A);
		}

		/**
		 * Spodní diagonální matice
		 *
		 * @return Mat4
		 */
		public Mat4 getL() {
			return new Mat4(this.L);
		}

		/**
		 * Horní diagonální matice
		 *
		 * @return Mat4
		 */
		public Mat4 getU() {
			return new Mat4(this.U);
		}

		/**
		 * Nahradí celý sloupec col matice sloupcovým vektorem v.
		 *
		 * @param col index sloupce
		 * @param v sloupcový vektor
		 */
		public void replaceCol(int col, Vec4r v) {
			for (int i = 0; i < 4; i++) {
				mat[i][col] = v.vec[i];
			}
		}

		/**
		 * Výpočet determinantu pomocí LU rozkladu
		 *
		 * @return double determinant matice
		 */
		public double det() {
			// TODO permutace
			double det = 1;

			int i;
			for (i = 0; i < 4; i++) {
				det *= this.L[i][i] * this.U[i][i];
			}

			return det;
		}

		/**
		 * Transpozice matice
		 *
		 * @return Mat4 transponovaná matice
		 */
		public Mat4 transpose() {

			double mT[][] = new double[4][4];

			int k, i;
			for (k = 0; k < 4; k++) {
				for (i = 0; i < 4; i++) {
					mT[i][k] = mat[k][i];
				}
			}

			return new Mat4(mT);
		}

		/**
		 * Inverze matice
		 * 
		 * @return 
		 */
		public Mat4 inverse() {
			int i;

			// příprava inverzní matice
			Mat4e iA = new Mat4e(new Mat4(), true);

			for (i = 0; i < 4; i++) {

				Vec4r ei = new Vec4r();
				ei.vec[i] = 1;
				Vec4r col = solve(ei);

				iA.replaceCol(i, col);

			}

			return iA;
		}

		/**
		 * Řešení rovnice A*x = v
		 *
		 * @param Vec4r v vektor pravých stran
		 * @return Vec4r vektor neznámých
		 */
		public Vec4r solve(Vec4r v) {

			// TODO podle permutační matice musí být prohozeny řádky
			Vec4r b = new Vec4r(v);

			// z = dopředná substituce - Lb
			Vec4r z = subFw(L, b);
			// x = zpětná substituce Uz
			Vec4r x = subBc(U, z);

			return x;
		}

		/**
		 * Řešení Ax = v pro spodní trojúhelníkovou matici
		 *
		 * @param A
		 * @param v
		 * @return
		 */
		private Vec4r subFw(double[][] A, Vec4r v) {

			Vec4r x = new Vec4r();

			int i, j;
			for (i = 0; i < 4; i++) {
				x.vec[i] = v.vec[i];
				for (j = 0; j < i; j++) {
					x.vec[i] -= A[i][j] * x.vec[j];
				}

				x.vec[i] = x.vec[i] / A[i][i];
			}

			return x;
		}

		/**
		 * Řešení Ax = v pro horní trojúhelníkovou matici
		 *
		 * @param A
		 * @param v
		 * @return
		 */
		private Vec4r subBc(double[][] A, Vec4r v) {

			Vec4r x = new Vec4r();

			int i, j;

			for (i = 3; i > -1; i--) {
				x.vec[i] = v.vec[i];
				for (j = 3; j > i; j--) {
					x.vec[i] -= A[i][j] * x.vec[j];
				}

				x.vec[i] = x.vec[i] / A[i][i];
			}

			return x;
		}

	}
	
	// poskytované statično:

	public static Mat4 transpose(Mat4 m) {
		Mat4Utils.Mat4e me = new Mat4e(m);

		return me.transpose();
	}

	public static Mat4 inverse(Mat4 m) {
		Mat4Utils.Mat4e me = new Mat4e(m);

		return me.inverse();
	}

	public static double det(Mat4 m) {
		Mat4Utils.Mat4e me = new Mat4e(m);

		return me.det();
	}
	
	// spíše pro potřeby otestování
	
	public static Mat4 decompL(Mat4 m) {
		Mat4Utils.Mat4e me = new Mat4e(m);

		return me.getL();
	}
	
	public static Mat4 decompU(Mat4 m) {
		Mat4Utils.Mat4e me = new Mat4e(m);

		return me.getU();
	}

}
