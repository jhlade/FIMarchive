package cz.uhk.fim.pgrf2.structures;

/**
 * Krychle s několika odebranými stěnami
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class SemiCube extends Cube {

	public SemiCube(double size) {
		super(size);
		
		triangles.remove(11);
		triangles.remove(10);

		triangles.remove(7);
		triangles.remove(6);
		
		triangles.remove(5);
		triangles.remove(4);
	}
	
	@Override
	public void promoteMaterial() {
		
		this.ambient = new float[]{.24725f, .1995f, .0745f};
		this.diffuse = new float[]{.75164f, .60648f, .22648f};
		this.specular = new float[]{.628281f, .555802f, .366065f};
		this.shiness = .4f;
		
	}

}
