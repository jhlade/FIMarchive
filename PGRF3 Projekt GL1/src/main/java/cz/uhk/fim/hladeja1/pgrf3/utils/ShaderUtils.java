package cz.uhk.fim.hladeja1.pgrf3.utils;

import cz.uhk.fim.hladeja1.pgrf3.gl1.model.Renderable;

import com.jogamp.opengl.GL3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Alternativní knihovna pro podporu práce se shadery. Využívá GL3.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * @version 2016.1
 */
public class ShaderUtils {
	
	// nepoužitý shader
	public static final int UNUSED_SHADER = -100;
	// minimální délka pro testování
	public static final int SHADER_MIN_LENGTH = 2;

	// Shadery
	public static enum ShaderType {
		VertexShader, FragmentShader, GeometryShader
	}

	// Typy protokolů
	public static enum LogType {
		LogShader, LogProgram
	}

	/**
	 * Vytvoření nového programu.
	 *
	 * @param gl GL3 kontext
	 * @param obj Renderovatelný objekt
	 *
	 * @return (int - gl pointer na program)
	 */
	public static int newProgram(GL3 gl, Renderable obj) {

		// vytvoření a kompilace jednotlivých shaderů
		int vs = newShader(gl, "/shaders/" + obj.getProgramBaseName() + ".vert", ShaderType.VertexShader);
		System.out.print(getGLLog(gl, vs, LogType.LogShader));

		int gs = newShader(gl, "/shaders/" + obj.getProgramBaseName() + ".geom", ShaderType.GeometryShader);
		System.out.print(getGLLog(gl, gs, LogType.LogShader));

		int fs = newShader(gl, "/shaders/" + obj.getProgramBaseName() + ".frag", ShaderType.FragmentShader);
		System.out.print(getGLLog(gl, fs, LogType.LogShader));

		// samotný program - vertex, fragment, geometry
		int program = createProgram(gl, vs, fs, gs);

		// ukazatel na data předávaná do fragment shaderu
		// - struktura, pak odpadá alchymie v Javě
		gl.glBindFragDataLocation(program, 0, "FragData");

		// vypsání stavu programu
		System.out.print(getGLLog(gl, program, LogType.LogProgram));

		// ukazatel na program
		return program;
	}

	/**
	 * Vygenerování ID programu.
	 *
	 * @param gl GL3 kontext
	 * @param vertexShader Ukazatel na vertex shader
	 * @param fragmentShader Ukazatel na fragment shader
	 * @return
	 */
	private static int createProgram(GL3 gl, int vertexShader, int fragmentShader, int geometryShader) {

		// id - pointer na program
		int pgid = gl.glCreateProgram();

		// připojení shaderů k programu
		if (vertexShader != ShaderUtils.UNUSED_SHADER) {
			gl.glAttachShader(pgid, vertexShader);
		}
		if (geometryShader != ShaderUtils.UNUSED_SHADER) {
			gl.glAttachShader(pgid, geometryShader);
		}
		if (fragmentShader != ShaderUtils.UNUSED_SHADER) {
			gl.glAttachShader(pgid, fragmentShader);
		}

		// slinkování
		gl.glLinkProgram(pgid);

		return pgid;
	}

	/**
	 * Protokol shaderu
	 *
	 * @param gl GL3 kontext
	 * @param glObject Ukazatel na objekt
	 * @return
	 */
	private static String getGLLog(GL3 gl, int glObject, LogType type) {

		// získání logu o dané délce
		final int log;

		/*
		switch (type) {
			case LogProgram:
				log = getGLParam(gl, glObject, GL3.GL_INFO_LOG_LENGTH, type);
				break;

			case LogShader:
			default:
				log = getGLParam(gl, glObject, GL3.GL_INFO_LOG_LENGTH, type);
				break;
		}
		// tak asi zkráceně:
		 */
		log = getGLParam(gl, glObject, GL3.GL_INFO_LOG_LENGTH, type);

		// nic tam není, nic se nevypíše
		if (log <= 0) {
			return "";
		}

		// alokace - vrácená délka a data
		final int[] length = new int[1];
		final byte[] bytes = new byte[log + 1];

		// GL volání logu do daných struktur
		switch (type) {
			case LogProgram:
				gl.glGetProgramInfoLog(glObject, log, length, 0, bytes, 0);
				break;

			case LogShader:
			default:
				gl.glGetShaderInfoLog(glObject, log, length, 0, bytes, 0);
				break;
		}

		// zpráva GL
		final String logInfo = new String(bytes);

		return String.format("Protokol GL: %s\n", logInfo);
	}

	/**
	 * Získání parametrů pro protokolování
	 *
	 * @param gl GL3 kontext
	 * @param glObject Ukazatel na objekt GL
	 * @param param Parametr
	 * @param type Typ protokolu
	 * @return (int - gl pointer na log)
	 */
	private static int getGLParam(GL3 gl, int glObject, int param, LogType type) {
		// alokace místa
		final int params[] = new int[1];

		// načtení
		switch (type) {
			case LogProgram:
				gl.glGetProgramiv(glObject, param, params, 0);
				break;

			case LogShader:
			default:
				gl.glGetShaderiv(glObject, param, params, 0);
				break;
		}

		return params[0];
	}

	/**
	 * Vytvoření nového shaderu a jeho kompilace.
	 *
	 * @param gl GL3 kontext
	 * @param fileName Zdrojový soubor
	 * @param type Typ shaderu
	 * @return
	 */
	private static int newShader(GL3 gl, String fileName, ShaderType type) {

		// typ shaderu
		int shType;

		switch (type) {
			case FragmentShader:
				shType = GL3.GL_FRAGMENT_SHADER;
				break;

			case GeometryShader:
				shType = GL3.GL_GEOMETRY_SHADER;
				break;

			case VertexShader:
			default:
				shType = GL3.GL_VERTEX_SHADER;
				break;
		}

		// načtení kódu do řetězce
		String glslSource = loadString(fileName);

		if (glslSource.length() > ShaderUtils.SHADER_MIN_LENGTH) {

			// vytvoření ID pro daný shader
			int id = gl.glCreateShader(shType);

			// linkování
			gl.glShaderSource(id, 1, new String[]{glslSource}, null);

			// kompilace shaderu
			gl.glCompileShader(id);

			return id;
		} else {
			// neplatný
			return ShaderUtils.UNUSED_SHADER;
		}
	}

	/**
	 * Načtení řádků z textového souboru
	 *
	 * @param fileName
	 * @return
	 */
	private static String loadString(String fileName) {

		System.out.print("Probíhá načítání GLSL programu z " + fileName + "...");

		InputStream inputStream = ShaderUtils.class.getResourceAsStream(fileName);

		if (inputStream == null) {
			System.out.print(" nenalezeno.\n");
			return ""; // neexistující shader
		} else {
			//System.out.print("\n");
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		// řetězec se bude načítat po řádcích
		StringBuilder stringBuilder = new StringBuilder();

		try {
			// řádek
			String line = reader.readLine();

			// smyčka na čtení
			while (line != null) {
				//System.out.println("DEBUG: " + line);

				stringBuilder.append(line).append("\n");
				line = reader.readLine();
			}

			// uzavření bufferů
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}

		System.out.print(" " + stringBuilder.toString().length() + " B.\n");
		// hotovo - případně prázdný nový řádek pro neexistující shader
		return (stringBuilder.append("\n")).toString();
	}

}
