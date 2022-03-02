package cz.uhk.fim.hladeja1.pgrf3.gl1.model;

import com.jogamp.opengl.GL3;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Buffers;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Texture;
import cz.uhk.fim.pgrf.transforms.Mat4;
import cz.uhk.fim.pgrf.transforms.Mat4Identity;
import cz.uhk.fim.pgrf.transforms.Mat4RotXYZ;
import cz.uhk.fim.pgrf.transforms.Mat4Transl;

/**
 * Testovací trojúhelník.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
@Deprecated
public class Triangle implements Renderable {

	private String baseName;

	private int programID, VAOid;
	private int vertexLoc, norlamLoc, fragmentLoc;
	private int matrixModelLoc, matrixProjectionLoc, matrixViewLoc;
	
	// modelová matice
	Mat4 modelMatrix = new Mat4Identity();

	private float[] vertices = { // XYZW
		0.0f, 0.0f, 0.0f, 1.0f,
		-5.0f, 5.0f, 0.0f, 1.0f,
		+5.0f, 5.0f, 0.0f, 1.0f};

	private float[] colors = { // RGB
		1.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f};
	
	// TODO spočítat normály
	private float[] normals = { // XYZ
		0.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 0.0f};

	public Triangle(String baseName) {
		this.baseName = baseName;
		
		modelMatrix = modelMatrix.mul(new Mat4Transl(5,1,1)).mul(new Mat4RotXYZ(0, -15, -35));
	}

	@Override
	public String getProgramBaseName() {
		return this.baseName;
	}

	@Override
	public int getProgramID() {
		return this.programID;
	}

	@Override
	public void setProgramID(int programID) {
		this.programID = programID;
	}

	@Override
	public void setVertexLoc(int vertexLoc) {
		this.vertexLoc = vertexLoc;
	}

	@Override
	public int getVertexLoc() {
		return this.vertexLoc;
	}

	@Override
	public void setFragmentLoc(int fragmentLoc) {
		this.fragmentLoc = fragmentLoc;
	}

	@Override
	public int getFragmentLoc() {
		return this.fragmentLoc;
	}

	@Override
	public void setMatrixProjectionLoc(int projMatrixLoc) {
		this.matrixProjectionLoc = projMatrixLoc;
	}

	@Override
	public int getMatrixProjectionLoc() {
		return this.matrixProjectionLoc;
	}

	@Override
	public void setMatrixViewLoc(int viewMatrixLoc) {
		this.matrixViewLoc = viewMatrixLoc;
	}

	@Override
	public int getMatrixViewLoc() {
		return this.matrixViewLoc;
	}

	@Override
	public void setVAOid(int newVAOid) {
		this.VAOid = newVAOid;
	}

	@Override
	public int getVAOid() {
		return this.VAOid;
	}

	@Override
	public float[] getFloatVertexArray() {
		return this.vertices;
	}

	@Override
	public float[] getFloatFragmentArray() {
		return this.colors;
	}

	@Override
	public int getDrawMethod() {
		return GL3.GL_TRIANGLES;
	}

	@Override
	public int getIndicesCount() {
		return 3;
	}

	@Override
	public void setMatrixModelLoc(int modelMatrixLoc) {
		this.matrixModelLoc = modelMatrixLoc;
	}

	@Override
	public int getMatrixModelLoc() {
		return this.matrixModelLoc;
	}

	@Override
	public float[] getFloatModelMatrix() {
		return this.modelMatrix.floatArray();
	}

	@Override
	public void setModelMatrix(Mat4 mat) {
		this.modelMatrix = mat;
	}

	@Override
	public Mat4 getModelMatrix() {
		return this.modelMatrix;
	}

	@Override
	public float[] getFloatNormalArray() {
		return this.normals;
	}

	@Override
	public void setNormalLoc(int normalLoc) {
		this.norlamLoc = normalLoc;
	}

	@Override
	public int getNormalLoc() {
		return this.norlamLoc;
	}

	@Override
	public SimpleGL3Buffers getBuffers() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void init(GL3 gl) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void display() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setMatrices(Mat4 P, Mat4 V) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setTexture(TextureType type, SimpleGL3Texture texture, String locationId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SimpleGL3Texture getTexture(TextureType type) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
