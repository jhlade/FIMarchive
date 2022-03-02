package cz.uhk.pgrf2.projekt1.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;
import cz.uhk.pgrf2.projekt1.transforms3D.Vec3D;

/**
 * Správce osvětlení, tzv. světelný velitel. Stará se o zavedení světelného modelu,
 * nese globální hodnoty prostředí (ambient) a udržuje seznam všech bodových světel.
 * 
 * Hlavní metodou je lightedPointColor(Point3D point, Vec3D nVec, Color color),
 * která přepočítá barvu color pro aktuálně renderovaný pixel pro bod point
 * nacházející se na polygonu s normálovým vektorem nVec.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class LightManager {
	
	/** celkové osvícení - barva a intenzita */
	private Color ambientColor = new Color(255, 255, 255);
	private float ambientIntensity = 0.325f;
	
	/** spravované světelné body */
	private List<Light> lights = new ArrayList<Light>(); 
	
	public LightManager() {
		// TODO nastavení konstruktorů pro zavedení ambientu?
	}
	
	/**
	 * @return the lights
	 */
	public List<Light> getLights() {
		return lights;
	}

	/**
	 * @param lights the lights to set
	 */
	public void setLights(List<Light> lights) {
		this.lights = lights;
	}
	
	/**
	 * 
	 */
	public void clearLights() {
		this.lights.clear();
	}
	
	/**
	 * Přidání světla do seznamu
	 * 
	 * @param light
	 */
	public void addLight(Light light) {
		this.lights.add(light);
	}

	/**
	 * @return the ambientColor
	 */
	public Color getAmbientColor() {
		return ambientColor;
	}

	/**
	 * @param ambientColor the ambientColor to set
	 */
	public void setAmbientColor(Color ambientColor) {
		this.ambientColor = ambientColor;
	}

	/**
	 * @return the ambientIntensity
	 */
	public float getAmbientIntensity() {
		return ambientIntensity;
	}

	/**
	 * @param ambientIntensity the ambientIntensity to set
	 */
	public void setAmbientIntensity(float ambientIntensity) {
		this.ambientIntensity = ambientIntensity;
	}

	/**
	 * Přepočítání barvy bodu podle spravovaných světelných podmínek.
	 * 
	 * @param point Zpracovávaný bod v homogenních souřadnicích
	 * @param nVec Normálový vektor polygonu právě zpracovávaného bodu
	 * @param color Barva povrchu polygonu
	 * @return
	 */
	public Color lightedPointColor(Point3D point, Vec3D nVec, Color color) {
		
		// rozklad výsledku na složky, složka barvy je vždycky jen float!
		float[] rgblFinal = new float[3];
		
		// rozklad intenzity na složky
		float[] rgbIntensity = new float[3];
		
		// ambientní světlo jako základ
		getAmbientColor().getRGBColorComponents(rgbIntensity);
		
		rgbIntensity[0] *= getAmbientIntensity(); // R
		rgbIntensity[1] *= getAmbientIntensity(); // G
		rgbIntensity[2] *= getAmbientIntensity(); // B
		
		// jednotlivá světla
		Iterator<Light> iterLight = lights.iterator();
		
		// jsou vůbec nějaká světla?
		while (iterLight.hasNext()) {
			
			// získání hodnoty pro toto světlo a bod
			float singleLight[] = iterLight.next().calculate(point, nVec);
	
			// připočítání ke globálnímu výsledku
			rgbIntensity[0] += singleLight[0]; // R
			rgbIntensity[1] += singleLight[1]; // G
			rgbIntensity[2] += singleLight[2]; // B
		}
		
		// přepočítání intenzity všem komponentám
		color.getRGBColorComponents(rgblFinal);
		rgblFinal[0] *= rgbIntensity[0]; // R
		rgblFinal[1] *= rgbIntensity[1]; // G
		rgblFinal[2] *= rgbIntensity[2]; // B
		
		// Hotová nová barva.Pokud se to po cestě někde přepálilo, bude to prostě "jenom" bílé.
		return new Color(
					/*R*/ (rgblFinal[0] > 1) ? 1 : rgblFinal[0],
					/*G*/ (rgblFinal[1] > 1) ? 1 : rgblFinal[1],
					/*B*/ (rgblFinal[2] > 1) ? 1 : rgblFinal[2] 
				);
	}

}
