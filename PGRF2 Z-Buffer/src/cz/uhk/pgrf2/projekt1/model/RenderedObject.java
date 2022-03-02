package cz.uhk.pgrf2.projekt1.model;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * RenderedObject - těleso používané v rendereru.
 */
public class RenderedObject {

	/** počet vrcholů */
	private int vertexCount;
	/** celkový počet indexů - ukazatelů definujících hrany nebo polygony */
	private int indexCount;
	/** režim - 0 = wireframe, 1 = surface */
	private boolean mode;

	/**
	 * @param vertexCount
	 * @param indexCount
	 * @param colors
	 * @param matrix
	 * @param mode
	 */
	public RenderedObject(int vertexCount, int indexCount, boolean mode) {
		super();
		this.vertexCount = vertexCount;
		this.indexCount = indexCount;
		this.mode = mode;
	}

	/**
	 * @return the vertexCount
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/**
	 * @param vertexCount the vertexCount to set
	 */
	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	/**
	 * @return the indexCount
	 */
	public int getIndexCount() {
		return indexCount;
	}
	
	public int getIterIndexCount() {
		return (mode) ? indexCount/3 : indexCount/2;
	}

	/**
	 * @param indexCount the indexCount to set
	 */
	public void setIndexCount(int indexCount) {
		this.indexCount = indexCount;
	}

	/**
	 * @return the mode
	 */
	public boolean isWireframe() {
		return !mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setWireframe(boolean mode) {
		this.mode = !mode;
	}
	
	

}
