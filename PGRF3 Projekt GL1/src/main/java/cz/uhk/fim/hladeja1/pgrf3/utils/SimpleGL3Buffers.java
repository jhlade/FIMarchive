package cz.uhk.fim.hladeja1.pgrf3.utils;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import java.util.ArrayList;

/**
 * Alternativní GL3 nástroj pro práci s buffery. Práce s atributy inspirována
 * PGRF OGLBuffers.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * @version 2016.1
 */
public class SimpleGL3Buffers {

	/**
	 * Atributy, převzato z PGRF utils
	 */
	static public class Attrib {

		String name;
		int dimension;

		public Attrib(String name, int dimension) {
			this.name = name;
			this.dimension = dimension;
		}
	}

	/**
	 * Vertex buffer třída, převzato z PGRF utils
	 */
	protected class VBO {

		int id, stride;
		Attrib[] attributes;

		/**
		 * Nový buffer
		 * 
		 * @param id ID ukazatel
		 * @param stride Proklad
		 * @param attributes Atributy
		 */
		public VBO(int id, int stride, Attrib[] attributes) {
			this.id = id;
			this.stride = stride;
			this.attributes = attributes;
		}
	}

	// GL3 kontext
	protected GL3 gl;

	// seznam VBO a atributů
	protected ArrayList<VBO> vertexBuffers = new ArrayList<>();
	protected ArrayList<Integer> attribArrays = null;

	public SimpleGL3Buffers(GL3 gl, float[] data, Attrib[] params) {

		this.gl = gl;

		createNewArrayBuffer(data, params);
	}

	public void createNewArrayBuffer(float[] dataArray, Attrib[] params) {

		// spočítání velikosti - N x Rozměr dat atributů
		int fPV = 0; // počet prvků na jeden vertex, nastavení prokaldu

		for (Attrib param : params) {
			fPV += param.dimension;
		}

		// vytvoření bufferu s atributy
		createNewArrayBuffer(dataArray, params, fPV);
	}

	private void createNewArrayBuffer(float[] dataArray, Attrib[] params, int fPV) {

		// alokace jednoprvkového pole pro uložení tohoto ID
		int[] arrayId = new int[1];

		// nové ID - uloží se do připraveného pole
		gl.glGenBuffers(1, arrayId, 0);

		// obecný bind
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, arrayId[0]);

		// alokace a zkopírování dat do bufferu
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, dataArray.length * Float.SIZE / 8, Buffers.newDirectFloatBuffer(dataArray), GL3.GL_STATIC_DRAW);

		// přidání na věšák
		vertexBuffers.add(new VBO(arrayId[0], fPV * 4, params));
	}

	private void bind(int programId) {
		// vyčištění před použitím
		unbind();

		this.attribArrays = new ArrayList<>();

		vertexBuffers.stream().map((VBO vbo) -> {
			// buffer
			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo.id);
			return vbo;
		}).forEach((vbo) -> {
			// atributy
			for (Attrib atr : vbo.attributes) {

				// pozice atributu v rpogramu
				int atrLoc = gl.glGetAttribLocation(programId, atr.name);

				// povolení atributu
				gl.glEnableVertexAttribArray(atrLoc);

				// https://www.opengl.org/sdk/docs/man/html/glVertexAttribPointer.xhtml
				gl.glVertexAttribPointer(atrLoc, atr.dimension, GL3.GL_FLOAT, false, vbo.stride, 0);
			}
		});
	}

	/**
	 * Odbindování bufferů.
	 */
	private void unbind() {

		// jsou nějaké atributy?
		if (this.attribArrays != null) {

			this.attribArrays.stream().forEach((atr) -> {
				gl.glDisableVertexAttribArray(atr);
			});

			attribArrays.clear();
		}
	}

	/**
	 * Vykreslení do GL pomocí VBO.
	 *
	 * @param drawMethod kreslení vzhledem k pospojování trojúhelníků
	 * @param program GL program
	 * @param indicesCount počet indexů
	 */
	public void drawBuffers(int drawMethod, int program, int indicesCount) {

		// 1) použití programu
		gl.glUseProgram(program);

		// 2) nabindování těchto bufferů
		bind(program);

		// 3) přímé vykreslení pole
		gl.glDrawArrays(drawMethod, 0, indicesCount);

		// 4) uvolnění paměti
		unbind();
	}

}
