package cz.uhk.fim.hladeja1.pgrf3.gl1.model;

import com.jogamp.opengl.GL3;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Buffers;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Texture;
import cz.uhk.fim.pgrf.transforms.Mat4;

/**
 * Asi generický sebevykreslitelný objekt.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public interface Renderable {
	
	enum TextureType { TexColor, TexNormal, TexHeight };

	/**
	 * Vertexové pole. Bude vykresleno pomocí drawArrays
	 * 
	 * @return 
	 */
	@Deprecated
	public float[] getFloatVertexArray();
	
	@Deprecated
	public float[] getFloatNormalArray();

	/**
	 * Pole barev přidružené k vertexům.
	 * 
	 * @return 
	 */
	@Deprecated
	public float[] getFloatFragmentArray();
	
	/**
	 * Modelová matice jako pole float[16]. Každý objekt by měl mít svou vlastní
	 * počáteční modelovou matici.
	 * 
	 * @return 
	 */
	@Deprecated
	public float[] getFloatModelMatrix();
	
	/**
	 * Modelová matice objektu.
	 * 
	 * @return Mat4 modleová matice
	 */
	public Mat4 getModelMatrix();
	
	/**
	 * Nastavení modelové matice
	 * 
	 * @param mat 
	 */
	public void setModelMatrix(Mat4 mat);

	/**
	 * Bázový název GLSL programů, který bude k tomuto objektu přidružen.
	 * Cesta je v /shaders/{baseName}.[ext]
	 * 
	 * @return 
	 */
	public String getProgramBaseName();

	/**
	 * Získání ukazatele na shaderový program.
	 * 
	 * @return 
	 */
	public int getProgramID();

	/**
	 * Přidružení GLSL programu k objektu pomocí ukazatele.
	 * 
	 * @param programID 
	 */
	@Deprecated
	public void setProgramID(int programID);

	/**
	 * Nastavení ukazatele na vertexové pole.
	 * 
	 * @param vertexLoc 
	 */
	@Deprecated
	public void setVertexLoc(int vertexLoc);
	
	@Deprecated
	public void setNormalLoc(int normalLoc);

	/**
	 * Ukazatel na vertexové pole.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getVertexLoc();
	
	@Deprecated
	public int getNormalLoc();

	/**
	 * Nastavení ukazatele na fragmentové/barevné pole.
	 * 
	 * @param fragmentLoc 
	 */
	@Deprecated
	public void setFragmentLoc(int fragmentLoc);

	/**
	 * Získání ukazatele na fragmentové pole.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getFragmentLoc();

	/**
	 * Nastavení ukazatele na projekční matici. Teoreticky může být per-model,
	 * je ale použita globální matice pro scénu.
	 * 
	 * @param projMatrixLoc 
	 */
	@Deprecated
	public void setMatrixProjectionLoc(int projMatrixLoc);

	/**
	 * Ukazatel na projekční matici.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getMatrixProjectionLoc();

	/**
	 * Nastavení ukazatele pohledové matice. Teoreticky může být per-model,
	 * je ale použita globální pohledová matice z kamery.
	 * 
	 * @param viewMatrixLoc 
	 */
	@Deprecated
	public void setMatrixViewLoc(int viewMatrixLoc);

	/**
	 * Získání ukazatele na pohledovou matici.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getMatrixViewLoc();
	
	/**
	 * Nastavení ukazatele na odpovídající modelovou matici.
	 * 
	 * @param modelMatrixLoc 
	 */
	@Deprecated
	public void setMatrixModelLoc(int modelMatrixLoc);

	/**
	 * Ukazatel na modelovou matici.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getMatrixModelLoc();

	/**
	 * Nastavení ukazatele na vertex array object.
	 * 
	 * @param newVAOid 
	 */
	@Deprecated
	public void setVAOid(int newVAOid);

	/**
	 * Ukazatel na vertex array object.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getVAOid();
	
	/**
	 * GL konstanta metody, která má být při volání drawArrays použita.
	 * 
	 * @return 
	 */
	@Deprecated
	public int getDrawMethod();
	
	/**
	 * Počet indexů, které se mají generovat pomocí drawArrays
	 * 
	 * @return 
	 */
	@Deprecated
	public int getIndicesCount();
	
	
	//
	/**
	 * Nastavení globálních uniformních matic
	 * 
	 * @param P
	 * @param V 
	 */
	public void setMatrices(Mat4 P, Mat4 V);
	
	/**
	 * Inicializace objektu v daném kontextu GL3
	 * 
	 * @param gl 
	 */
	public void init(GL3 gl);
	
	public void setTexture(TextureType type, SimpleGL3Texture texture, String name);
	
	public SimpleGL3Texture getTexture(TextureType type);
	
	/**
	 * Vyskreslení
	 */
	public void display();
	
	/**
	 * Získání bufferů objektu
	 * 
	 * @return 
	 */
	public SimpleGL3Buffers getBuffers();
	
}
