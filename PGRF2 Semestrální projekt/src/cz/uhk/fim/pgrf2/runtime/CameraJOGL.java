package cz.uhk.fim.pgrf2.runtime;

import javax.media.opengl.glu.*;

import cz.uhk.fim.transforms3D.Camera;
import cz.uhk.fim.transforms3D.Vec3D;

/**
 * Rozšíření kamery transforms3d.Camera pro JOGL. Původní kamera
 * byla ale upravená, aby šlo dědit a přepisovat.
 * 
 * Výpočet vektorů pohybu v OpenGL prostoru je inspirován tutoriály 안성호.
 * http://www.songho.ca/opengl/index.html
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class CameraJOGL extends Camera {
	
	// up vektor + referenční bod
	private Vec3D up, cen;
	
	/**
	 * Konstruktor
	 * 
	 * @param glu
	 * @throws Exception 
	 */
	public CameraJOGL() {
		super();
	}
	
	/**
	 * Přímá manipulace s GLU - použije se gluLookAt()
	 * 
	 * @param glu
	 */
	public void setMatrix(GLU glu) {
		
		computeMatrix();
		
		glu.gluLookAt(
				eye.x, eye.y, eye.z, // pozice kamery
				cen.x, cen.y, cen.z, // referenční bod
				up.x, up.y, up.z     // up-vektor
				);
	}
	
	/**
	 * Přepočítání matice
	 */
	@Override
	public void computeMatrix() {
			eyeVector = new Vec3D(
					(float) (Math.sin(azimuth) * Math.cos(zenith)),
					(float) Math.sin(zenith),
					(float) -(Math.cos(azimuth) * Math.cos(zenith))
					);
			
			up = new Vec3D(
					(float) (Math.sin(azimuth) * Math.cos(zenith + Math.PI/ 2)), 
					(float) Math.sin(zenith + Math.PI / 2),
					(float) -(Math.cos(azimuth) * Math.cos(zenith + Math.PI/ 2)) );
			
			if (firstPerson) {
				eye = new Vec3D(pos);
				cen = eye.add(eyeVector.mul(radius));
			} else {
				eye = pos.add(eyeVector.mul(-1 * radius));
				cen = new Vec3D(pos);
			}
	}
	
	/**
	 * Směrový vektor. Spočítá se z pozice kamery (eye) a pohledu na objekt.
	 * 
	 * @return
	 */
	public Vec3D getDirectionVector() {
		return cen.add(eye.mul(-1)).normalized();
	}
	
	/**
	 * Up-vektor
	 * 
	 * @return
	 */
	public Vec3D getUpVector() {
		return up.normalized();
	}
	
	/**
	 * Vektorový součin up-vektoru a vektoru směru dává vektor směrem vlevo od kamery
	 * 
	 * @return
	 */
	public Vec3D getLeftDirection() {
		return getUpVector().cross(getDirectionVector()).normalized();
	}
	
	@Override
	public void forward(double step) {
		
		// nová pozice = současná pozice + (normalizovaný směrový vektor * velikost kroku)
		setPosition(getPosition().add(getDirectionVector().mul(step)));
		
		computeMatrix();
	}
	
	@Override
	public void backward(double step) {
		forward(-step);
	}
	
	@Override
	public void up(double step) {
		
		// posun ve směru up-vektoru
		setPosition(getPosition().add(getUpVector().mul(step)));
		
		computeMatrix();
	}
	
	@Override
	public void down(double step) {
		up(-step);
	}
	
	@Override
	public void left(double step) {
		
		// posun ve směru vektoru vlevo
		setPosition(getPosition().add(getLeftDirection().mul(step)));
		
		computeMatrix();
	}
	
	@Override
	public void right(double step) {
		left(-step);
	}

}
