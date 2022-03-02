package cz.uhk.fim.hladeja1.pgrf3.gl1.model;

import cz.uhk.fim.pgrf.transforms.Camera;
import cz.uhk.fim.pgrf.transforms.Mat4;
import cz.uhk.fim.pgrf.transforms.Vec3D;

/**
 * Polodekorátor Transforms Camera objektu.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public class GLCamera {
	
	/** injektovaná Transforms Camera */
	private final Camera camera;
	
	/** vektory up, střed */
	private Vec3D up, cen;
	
	/**
	 * Injekce PGRF-Transforms kamery a vytvoření GL-Kamery.
	 * 
	 * @param camera 
	 */
	public GLCamera(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * Originální kamera.
	 * 
	 * @return Transforms Camera
	 */
	public Camera getCamera() {
		return this.camera;
	}
	
	public void setPosition(double x, double y, double z) {
		this.camera.setPosition(new Vec3D(x, y, z));
	}
	
	public void setPosition(Vec3D pos) {
		this.camera.setPosition(pos);
	}
	
	public Mat4 getViewMatrix() {
		return this.camera.getViewMatrix();
	}
	
	
	/**
	 * Nastavení kamery stylem "look at". Konverze mezi kartézským a sférickým
	 * souřadnicovým systémem pro kameru.
	 * 
	 * Azimut, Radius, Zenith -> bod ve scéně (X, Y, Z)
	 * 
	 * @param x
	 * @param y
	 * @param z 
	 */
	public void setLookAt(double x, double y, double z) {
		
		double azimuth, radius, zenith;
		
		radius = Math.sqrt(
							(x - camera.getPosition().x) * (x - camera.getPosition().x)
							+
							(y - camera.getPosition().y) * (y - camera.getPosition().y)
							+
							(z - camera.getPosition().z) * (z - camera.getPosition().z)
							);
		
		azimuth = Math.atan( (y - camera.getPosition().y) / (x - camera.getPosition().x) );
		
		zenith = Math.atan(
						  Math.sqrt(
							(x - camera.getPosition().x) * (x - camera.getPosition().x)
							+
							(y - camera.getPosition().y) * (y - camera.getPosition().y)
						  )
						  /
						  (z - camera.getPosition().z)
						  );
		
		this.camera.setAzimuth(azimuth);
		this.camera.setRadius(radius);
		this.camera.setZenith(zenith);
	}
	
	/**
	 * LookAt jako Vec3D
	 * 
	 * @param latPos 
	 */
	public void setLookAt(Vec3D latPos) {
		this.setLookAt(latPos.x, latPos.y, latPos.z);
	}

	public void addAzimuth(double a) {
		this.camera.addAzimuth(a);
	}
	
	public void setAziumth(double a) {
		this.camera.setAzimuth(a);
	}

	public void addZenith(double z) {
		this.camera.addZenith(z);
	}
	
	public void setZenith(double z) {
		this.camera.setZenith(z);
	}

	public double getAzimuth() {
		return this.camera.getAzimuth();
	}
	
	public double getZenith() {
		return this.camera.getZenith();
	}

	public void forward(double stepSize) {
		this.camera.forward(stepSize);
	}

	public void backward(double stepSize) {
		this.forward(-stepSize);
	}

	public void up(double stepSize) {
		this.camera.up(stepSize);
	}

	public void down(double stepSize) {
		this.up(-stepSize);
	}

	public void left(double stepSize) {
		this.camera.left(stepSize);
	}

	public void right(double stepSize) {
		this.left(-stepSize);
	}

	public Vec3D getPosition() {
		return this.camera.getPosition();
	}
	
}
