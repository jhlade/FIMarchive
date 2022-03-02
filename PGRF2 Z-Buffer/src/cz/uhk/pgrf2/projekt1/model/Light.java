package cz.uhk.pgrf2.projekt1.model;

import java.awt.Color;

import cz.uhk.pgrf2.projekt1.transforms3D.Mat4;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4Identity;
import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;
import cz.uhk.pgrf2.projekt1.transforms3D.Vec3D;

/**
 * Světelný bod.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Light {
	
	/** poloha světla */
	private Point3D position;
	/** intenzita světla */
	double intensity;
	/** barva světla */
	private Color color;
	/** rozptyl */
	private double dropRate;
	
	/** komaptibilní transformační matice */
	private Mat4 matrix = new Mat4Identity();
	
	/**
	 * @param position
	 * @param intensity
	 * @param color
	 * @param dropRate
	 */
	public Light(Point3D position, double intensity, Color color, double dropRate) {
		this.position = position;
		this.intensity = intensity;
		this.color = color;
		this.dropRate = dropRate;
	}
	

	/**
	 * @param intensity
	 * @param color
	 * @param dropRate
	 */
	public Light(double intensity, Color color, double dropRate) {
		this(new Point3D(0, 0, 0), intensity, color, dropRate);
	}

	/**
	 * @return the position
	 */
	public Point3D getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Point3D position) {
		this.position = position;
	}

	/**
	 * @return the intensity
	 */
	public double getIntensity() {
		return intensity;
	}

	/**
	 * @param intensity the intensity to set
	 */
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the dropRate
	 */
	public double getDropRate() {
		return dropRate;
	}
	
	public double getDropRateSquare() {
		return dropRate * dropRate;
	}

	/**
	 * @param dropRate the dropRate to set
	 */
	public void setDropRate(double dropRate) {
		this.dropRate = dropRate;
	}
	
	public Mat4 getMatrix() {
		return matrix;
	}


	public void setMatrix(Mat4 matrix) {
		this.matrix = matrix;
	}


	/**
	 * Vliv tohoto světla na zvolený bod na vykreslovaném polygonu.
	 * 
	 * @param point Osvětlovaný bod v homogenních souřadnicích
	 * @param nVec Normálový vektor plochy
	 * @return
	 */
	public float[] calculate(Point3D point, Vec3D nVec) {
		// výpočet úhlu v krocích:
		
		// nejdřív vektor, proboha
		Vec3D uVec = new Vec3D(getPosition().x - point.x, getPosition().y - point.y, getPosition().z - point.z);
		
		// skalární součin pro určení úhlu
		double product = nVec.dot(uVec);
		
		// čtverce před odmocněním - skalární součiny sama sebe
		double nMag = nVec.dot(nVec);
		double lMag = uVec.dot(uVec);
		
		// výsledná intenzita daná svíraným úhlem mezi vektory, I = f(cos(a)), 1 - arccos( (u.v) / (sqrt(uu.vv) ) / pi )
		double intensity = ( 1 - ( Math.acos( product / Math.sqrt(nMag * lMag) ) / Math.PI ) );
	
		// světlo má pouze omezený dosah
		if (getDropRate() > 0) {
		
			// čtverec vzdálenosti bodu od světla
			double pointDistanceSq = (Math.abs(point.x - getPosition().x)
								  + Math.abs(point.y - getPosition().y)
								  + Math.abs(point.z - getPosition().z));
			
			// ta vzdálenost je větší než čtverec rozptylu
			if (pointDistanceSq > getDropRateSquare()) {
				
				intensity *= (getDropRate() / Math.sqrt(pointDistanceSq));

			}
			
			
		}
		
		// zmixuje se to s globální intenzitou světla
		intensity *= getIntensity();

		// výsledné světlo
		float[] light = new float[3];
		
		// naplnění vlastní barvou
		getColor().getRGBColorComponents(light);
		
		light[0] *= intensity;
		light[1] *= intensity;
		light[2] *= intensity;
		
		// ... a odeslat
		return light;
	}

}
