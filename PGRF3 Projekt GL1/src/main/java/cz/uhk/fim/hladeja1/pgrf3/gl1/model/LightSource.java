package cz.uhk.fim.hladeja1.pgrf3.gl1.model;

import cz.uhk.fim.pgrf.transforms.Col;
import cz.uhk.fim.pgrf.transforms.Vec3D;

/**
 * Zdroj světla.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public class LightSource {
	
	/** pozice světla ve scéně */
	private Vec3D position;
	
	/** základní barva světla */
	private Col lightColor;
	
	/** reflektor */
	private float spotCutOff;
	
	/** místo */
	private Vec3D spot;
	
	public LightSource() {
		
	}

	public Vec3D getPosition() {
		return position;
	}

	public Col getLightColor() {
		return lightColor;
	}

	public void setPosition(Vec3D position) {
		this.position = position;
	}

	public void setLightColor(Col lightColor) {
		this.lightColor = lightColor;
	}

	public float getSpotCutOff() {
		return spotCutOff;
	}

	public void setSpotCutOff(float spotCutOff) {
		this.spotCutOff = spotCutOff;
	}

	public Vec3D getSpotDirection() {
		return (spot.add(position.mul(-1)));
	}

	public Vec3D getSpot() {
		return spot;
	}

	public void setSpot(Vec3D spot) {
		this.spot = spot;
	}
	
	
	
}
