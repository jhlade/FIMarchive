package cz.uhk.fim.hladeja1.pgrf3.gl1.model;

import com.jogamp.opengl.GL3;
import cz.uhk.fim.hladeja1.pgrf3.utils.Mat4Utils;
import cz.uhk.fim.hladeja1.pgrf3.utils.ShaderUtils;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Buffers;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Texture;
import cz.uhk.fim.pgrf.transforms.Col;

import cz.uhk.fim.pgrf.transforms.Mat4;
import cz.uhk.fim.pgrf.transforms.Mat4Identity;
import cz.uhk.fim.pgrf.transforms.Mat4Transl;
import cz.uhk.fim.pgrf.transforms.Point3D;
import cz.uhk.fim.pgrf.utils.ToFloatArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Modelové grafické primitivum - mřížka.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public class Grid implements Renderable {
	
	// injektovaný GL3 kontext
	GL3 gl;

	// velikost plochy (musí být sudé, pro tr. strip bude v IB pak (2x+2)*(y-1)+4y prvků)
	// shadery budou asi teda počítat se čtvercovou sítí NxN
	private final int ROWS = 32;
	private final int COLS = 32;

	// použitá topologie - Triangle Strip
	private final int TYPE = GL3.GL_TRIANGLE_STRIP;

	// název GLSL programu
	private final String baseName;
	
	// TODO menší refaktůrek - implementovat buffery jako součást modelu
	private SimpleGL3Buffers buffers;
	
	// textury objektu
	private SimpleGL3Texture texture;
	private SimpleGL3Texture textureNormal;
	private SimpleGL3Texture textureHeight;
	
	// ladící informace
	private final boolean debug = true;
	
	// materiál
	/*
	private final Col ambient     = new Col( .25,     .20725,  .20725,  .922);
	private final Col diffuse     = new Col(1.0,      .829,    .829,    .922);
	private final Col specular    = new Col( .296648, .296648, .296648, .922);
	private final Col emission    = new Col( .0,      .0,      .0,      .0);
	private final float shininess = 11.264f;
	*/
	
	private final Col ambient     = new Col( .1,     .18725,  .1745,  .8);
	private final Col diffuse     = new Col(0.396,      .74151,    .69102,    .8);
	private final Col specular    = new Col( .297254, .30829, .306678, .8);
	private final Col emission    = new Col( .0,      .0,      .0,      .0);
	private final float shininess = 12.8f;

	
	// JOGL
	// ukazatele na objekty
	private int programID, VAOid;
	private int vertexLoc, normalLoc, fragmentLoc;
	private int matrixModelLoc, matrixProjectionLoc, matrixViewLoc;
	private int normalMatrixLoc;
	
	// DATA
	// vertexy, normály a barvy
	private final float[] vertices;
	private final float[] normals;
	private final float[] colors;
	
	// MODEL
	// modelová matice
	Mat4 modelMatrix = new Mat4Identity();
	// modelové vertexy
	private final List<Point3D> modelVertices = new ArrayList<>();
	// modelové indexy
	private final List<Integer> modelIndices = new ArrayList<>();
	
	// lazy - počet indexů
	Integer ibCount;

	
	/**
	 * Nový konstruktor uv-mřížky
	 * 
	 * @param baseName název programu
	 * @param uvGrid true pro uv-mřížku
	 */
	public Grid(String baseName, boolean uvGrid) {
		
		this.baseName = baseName;
		
		if (uvGrid == false) {
			// TODO - případná chyba
			//throw new Exception();
		}
		
		// vytvoření modelu
		createModel();
		
		// MODEL -> VBO
		
		// alokace pole uv-vertexů (vec2)
		vertices = new float[2 * modelIndices.size()];
		// zbytek tady není potřeba
		normals = new float[0];
		colors = new float[0];
		
		int floatCounterVec2 = 0;
		Iterator<Integer> indexIterator = modelIndices.iterator();

		while (indexIterator.hasNext()) {

			// generický vertex
			Point3D point = modelVertices.get(indexIterator.next());

			// UV-souřadnice
			vertices[floatCounterVec2 + 0] = (float) point.x; // U
			vertices[floatCounterVec2 + 1] = (float) point.y; // V
			
			// počítadlo vektorů
			floatCounterVec2 += 2;
		}
		
		// modelová matice zůstává nedotčena
		//modelMatrix = new Mat4Identity();
		
		// offset - zarování mřížky na střed ostrova souřadnic - fyzicky se spočítá až ve vertex shaderu
		this.modelMatrix = modelMatrix.mul(new Mat4Transl(-(COLS / 2.0), -(ROWS / 2.0), .0));
	}
	
	/**
	 * Starý konstruktor komplexní mřížky
	 *
	 * @param baseName Název shaderového programu
	 */
	@Deprecated
	public Grid(String baseName) {
		this.baseName = baseName;

		createModel();

		// MODEL -> VBO
		
		// alokace pole vertexů (vec4)
		vertices = new float[4 * modelIndices.size()];
		// alokace vertexových normál (vec3)
		normals = new float[3 * modelIndices.size()];
		// alokace pole barev (vec3)
		colors = new float[3 * modelIndices.size()];

		int floatCounterVec4 = 0;
		int floatCounterVec3 = 0;
		Iterator<Integer> indexIterator = modelIndices.iterator();

		while (indexIterator.hasNext()) {

			// vertex
			Point3D point = modelVertices.get(indexIterator.next());

			// UV-souřadnice
			vertices[floatCounterVec4 + 0] = (float) point.x; // X
			vertices[floatCounterVec4 + 1] = (float) point.y; // Y
			vertices[floatCounterVec4 + 2] = 0.0f; // Z = 0
			vertices[floatCounterVec4 + 3] = 1.0f; // W = 1

			// primárně asi černá
			colors[floatCounterVec3 + 0] = .0f; // R
			colors[floatCounterVec3 + 1] = .0f; // G
			colors[floatCounterVec3 + 2] = .0f; // B
			
			// vertexová normála - prostě sahá ve směru z
			normals[floatCounterVec3 + 0] = .0f; // X
			normals[floatCounterVec3 + 1] = .0f; // Y
			normals[floatCounterVec3 + 2] = 1.0f; // Z

			// počítadla vektorů
			floatCounterVec4 += 4;
			floatCounterVec3 += 3;
		}

		// offset - zarování mřížky na střed ostrova souřadnic - fyzicky se spočítá až ve vertex shaderu
		modelMatrix = modelMatrix.mul(new Mat4Transl(-(COLS / 2), -(ROWS / 2), 0));
	}
	
	
	/**
	 * Inicializace kontextu, bufferů a programu
	 */
	@Override
	public void init(GL3 gl) {
		
		// nastavení konextu
		this.gl = gl;
		
		// vytvoření a nastavení programu pro grid
		this.programID = ShaderUtils.newProgram(gl, this);
		
		// nastavení pozic uniformních matic
		this.setMatrixModelLoc(gl.glGetUniformLocation(this.programID, "modelMatrix"));
		this.setMatrixProjectionLoc(gl.glGetUniformLocation(this.programID, "projMatrix"));
		this.setMatrixViewLoc(gl.glGetUniformLocation(this.programID, "viewMatrix"));
		
		this.setMatrixNormalLoc(gl.glGetUniformLocation(this.programID, "normalMatrix"));
		
		// buffery
		SimpleGL3Buffers.Attrib[] gridParams = { new SimpleGL3Buffers.Attrib("uvPosition", 2) };
		this.buffers = new SimpleGL3Buffers(gl, this.vertices, gridParams);
	}
	
	
	/**
	 * Vytvoření základního modelu
	 * 
	 */
	private void createModel() {
		
		// vygenerování modelových vertexů
		for (int rM = 0; rM <= ROWS; rM++) {     // Y-osa
			for (int cM = 0; cM <= COLS; cM++) { // X-osa
				// x, y, z = 0, w = 1.0
				modelVertices.add(new Point3D((float) cM, (float) rM, .0f, 1.0f));
			}
		}

		if (this.debug) {
			System.out.println("=============================");
			//System.out.println("0,5,1,6,2,7,3,8,4,9,5,5,10,6,11,7,12,8,13,9,14");
			//System.out.println("0,5,1,6,2,7,3,8,4,9,9,5,5,10,6,11,7,12,8,13,9,14");
			System.out.println("0,5,1,6,2,7,3,8,4,9,9,9,14,9,13,8,12,7,11,6,10,5,10,10");
		}
		// modelové indexy - topologie TRIANGLE_STRIP (!)
		for (int rI = 0; rI < ROWS; rI++) {
			for (int cI = 0; cI <= COLS + 1; cI++) {

				if (cI < COLS + 1) {
					if (rI % 2 == 0) { // sudá řádka
						modelIndices.add(rI * (COLS + 1) + cI);
						modelIndices.add(rI * (COLS + 1) + cI + COLS + 1);

						// debug
						if (this.debug) {
							System.out.print((rI * (COLS + 1) + cI) + ",");
							System.out.print((rI * (COLS + 1) + cI + COLS + 1) + ",");
						}
					} else {
						// opačným směrem
						modelIndices.add((rI + 1) * (COLS + 1) + (-cI + COLS + 1 - 1));
						modelIndices.add(rI * (COLS + 1) + (-cI + COLS + 1 - 1));

						// debug
						if (this.debug) {
							System.out.print(((rI + 1) * (COLS + 1) + (-cI + COLS + 1 - 1)) + ",");
							System.out.print((rI * (COLS + 1) + (-cI + COLS + 1 - 1)) + ",");
						}
					}
				} else // degenerace
				if (rI % 2 == 0) {
					modelIndices.add(rI * (COLS + 1) + cI + COLS);
					modelIndices.add(rI * (COLS + 1) + cI + COLS);

					if (this.debug) {
						System.out.print((rI * (COLS + 1) + cI + COLS) + ",");
						System.out.print((rI * (COLS + 1) + cI + COLS) + ",");
					}
				} else {
					modelIndices.add((rI + 1) * (COLS + 1) + (-cI) + COLS + 1);
					modelIndices.add((rI + 1) * (COLS + 1) + (-cI) + COLS + 1);
					
					if (this.debug) {
						System.out.print(((rI + 1) * (COLS + 1) + (-cI) + COLS + 1) + ",");
						System.out.print(((rI + 1) * (COLS + 1) + (-cI) + COLS + 1) + ",");
					}
				}

			}
		}
		if (this.debug) { System.out.println("\n============================="); }
	}
	
	@Override
	public void display() {
		
		// nastavení materiálu
		gl.glUniform1f(gl.glGetUniformLocation(this.programID, "Shininess"), this.shininess);
		gl.glUniform4fv(gl.glGetUniformLocation(this.programID, "Ambient"),1, ToFloatArray.convert(ambient), 0);
		gl.glUniform4fv(gl.glGetUniformLocation(this.programID, "Diffuse"),1, ToFloatArray.convert(diffuse), 0);
		gl.glUniform4fv(gl.glGetUniformLocation(this.programID, "Specular"),1, ToFloatArray.convert(specular), 0);
		gl.glUniform4fv(gl.glGetUniformLocation(this.programID, "Emission"),1, ToFloatArray.convert(emission), 0);
		
		// textura
		if (this.texture != null) {
			this.texture.bindTexture(0);
		}
		
		// normálová textura
		if (this.textureNormal != null) {
			this.textureNormal.bindTexture(1);
		}
		
		// výšková textura
		if (this.textureHeight != null) {
			this.textureHeight.bindTexture(2);
		}
		
		buffers.drawBuffers(this.TYPE, this.programID, this.getIndicesCount());
		
		/*
		@Deprecated
		// 1) použití programu
		gl.glUseProgram(this.getProgramID());
		
		// TODO 4) bindování vertexového pole
		gl.glBindVertexArray(this.VAOid);
		
		// 5) vykrelsení
		gl.glDrawArrays(TYPE, 0, this.getIndicesCount());
		
		// TODO 6) unbindování -- buffers.unbind
		gl.glDisableVertexAttribArray(this.VAOid);
		*/
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
		return this.TYPE;
	}

	@Override
	public int getIndicesCount() {
		
		// lazy init
		if (ibCount == null) {
			this.ibCount = 4 * ROWS + (2 * COLS + 2) * (ROWS - 1);
		}
		
		return this.ibCount.intValue();
	}

	@Override
	public void setMatrixModelLoc(int modelMatrixLoc) {
		this.matrixModelLoc = modelMatrixLoc;
	}

	@Override
	public int getMatrixModelLoc() {
		return this.matrixModelLoc;
	}
	
	public void setMatrixNormalLoc(int normalMatrixLoc) {
		this.normalMatrixLoc = normalMatrixLoc;
	}
	
	public int getMatrixNormalLoc() {
		return this.normalMatrixLoc;
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
		this.normalLoc = normalLoc;
	}

	@Override
	public int getNormalLoc() {
		return this.normalLoc;
	}

	@Override
	public SimpleGL3Buffers getBuffers() {
		return this.buffers;
	}

	@Override
	public void setMatrices(Mat4 P, Mat4 V) {
		
		// nastavení modelové matice
		gl.glUniformMatrix4fv(this.getMatrixModelLoc(), 1, false, this.modelMatrix.floatArray(), 0);
		
		// nastavení uniformních matic scény - globální projekční + pohledová z kamery
		gl.glUniformMatrix4fv(this.getMatrixProjectionLoc(), 1, false, P.floatArray(), 0);
		gl.glUniformMatrix4fv(this.getMatrixViewLoc(), 1, false, V.floatArray(), 0);
		
		// výpočet normálové matice z modelu
		Mat4 normalMatrix = Mat4Utils.transpose(Mat4Utils.inverse(this.modelMatrix));
		gl.glUniformMatrix4fv(this.getMatrixNormalLoc(), 1, false, normalMatrix.floatArray(), 0);
	}

	@Override
	public void setTexture(TextureType type, SimpleGL3Texture texture, String name) {
		
		switch(type) {
			
			default:
			case TexColor:
				this.texture = texture;
				this.texture.setLocation(this.getProgramID(), name);
			break;
			
			case TexNormal:
				this.textureNormal = texture;
				this.textureNormal.setLocation(this.getProgramID(), name);
			break;
			
			case TexHeight:
				this.textureHeight = texture;
				this.textureHeight.setLocation(this.getProgramID(), name);
			break;
			
		}
		
	}
	
	@Override
	public SimpleGL3Texture getTexture(TextureType type) {
		switch(type) {
			
			default:
			case TexColor:
				return this.texture;
			
			case TexNormal:
				return this.textureNormal;
			
			case TexHeight:
				return this.textureHeight;

		}
	}
	
}
