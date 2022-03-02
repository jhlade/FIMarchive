package cz.uhk.fim.hladeja1.pgrf3.utils;

import cz.uhk.fim.hladeja1.pgrf3.gl1.model.Renderable;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

/**
 * Alternativní knihovna pro práci s buffery. Využívá GL3.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * @version 2016.1
 */
@Deprecated
public class BufferUtils {

	/**
	 * Vygeneruje nové ID pro uložení ukazatele vertexového pole.
	 *
	 * @param gl GL3 handler
	 * @return (int - gl pointer)
	 */
	public static int generateVAOid(GL3 gl) {

		// alokace jednoprvkového pole pro uložení tohoto ID
		int[] arrayId = new int[1];

		// nové ID - uloží se do připraveného pole
		gl.glGenVertexArrays(1, arrayId, 0);

		// zísakné ID
		return arrayId[0];
	}

	/**
	 * Vygeneruje nové ID pro uložení ukazatele obecného bufferu.
	 *
	 * @param gl GL3 handler
	 * @return (int - gl pointer)
	 */
	private static int genereateBufferId(GL3 gl) {

		// alokace jednoprvkového pole pro uložení tohoto ID
		int[] arrayId = new int[1];

		// nové ID - uloží se do připraveného pole
		gl.glGenBuffers(1, arrayId, 0);

		// zísakné ID
		return arrayId[0];
	}

	/**
	 * Nabindování jednoho bufferu
	 *
	 * @param gl GL Handler
	 * @param buffID Ukazatel na buffer
	 * @param array Data - pole floatů
	 * @param location Ukazatel na datový prostor
	 */
	private static void bindBuffer(GL3 gl, int buffID, float[] array, int location) {
		// obecný bind
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffID);
		// alokace a zkopírování dat do bufferu
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, array.length * Float.SIZE / 8, Buffers.newDirectFloatBuffer(array), GL3.GL_STATIC_DRAW);

		// atributy
		gl.glEnableVertexAttribArray(location);
		// https://www.opengl.org/sdk/docs/man/html/glVertexAttribPointer.xhtml
		gl.glVertexAttribPointer(location, 4, GL3.GL_FLOAT, false, 0, 0);
	}

	/**
	 * Vytvoření a nabindování bufferů daného objektu.
	 *
	 * @param gl GL3 kontext
	 * @param obj Vykreslitelný objekt
	 */
	public static void newFloatVNCBuffers(GL3 gl, Renderable obj) {
		// nabindování korektního VAO ID
		gl.glBindVertexArray(obj.getVAOid());

		// nový paměťový prostor pro buffery
		int vbID = BufferUtils.genereateBufferId(gl);
		int nbID = BufferUtils.genereateBufferId(gl);
		int cbID = BufferUtils.genereateBufferId(gl);

		// nabindování obou
		BufferUtils.bindBuffer(gl, vbID, obj.getFloatVertexArray(), obj.getVertexLoc());
		BufferUtils.bindBuffer(gl, nbID, obj.getFloatVertexArray(), obj.getNormalLoc());
		BufferUtils.bindBuffer(gl, cbID, obj.getFloatFragmentArray(), obj.getFragmentLoc());
	}

}
