package cz.uhk.fim.hladeja1.pgrf3.utils;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Práce s texturami
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * @version 2016.1
 */
public class SimpleGL3Texture {
	
	private final int MISSING = -1;
	
	private GL3 gl;
	private Texture texture;
	private Integer location;
	
	public SimpleGL3Texture(GL3 gl, String filename) {
		
		this.gl = gl;
		
		String[] file = filename.split("\\.(?=[^\\.]+$)");
		
		try {
			// jpeg
			this.texture = TextureIO.newTexture(this.getClass().getResource("/textures/" + filename), false, "." + file[1]);
			
			this.texture.setTexParameteri(this.gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
			this.texture.setTexParameteri(this.gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			this.texture.setTexParameteri(this.gl, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
			this.texture.setTexParameteri(this.gl, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
			
		} catch (IOException | GLException ex) {
			Logger.getLogger(SimpleGL3Texture.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public void setLocation(int program, String name) {
		this.location = (int) gl.glGetUniformLocation(program, name);
	}
	
	public int getLocation() {
		return (this.location != null) ? this.location.intValue() : MISSING;
	}
	
	public void bindTexture(int slot) {
		gl.glActiveTexture(GL3.GL_TEXTURE0 + slot);
		texture.enable(gl);
		texture.bind(gl);
		gl.glUniform1i(getLocation(), slot);
	}
	
}
