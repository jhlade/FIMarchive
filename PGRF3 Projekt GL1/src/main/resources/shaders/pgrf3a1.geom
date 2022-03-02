#version 330

// prichazi trojuhelnik, tada...
layout(triangles) in;

// ven vychazi taky trojuhelnik, tri vertexove a jedna fazetova cara... tvorene trojuhelniky (5 * 3 = 15)
layout(triangle_strip, max_vertices = 15) out;

// uniformni vstupy pro transformaci objektu
uniform mat4 modelMatrix, viewMatrix, projMatrix;
uniform mat4 normalMatrix;

// ma se pouzivat vizualizace v GS?
uniform int showNormals;

// predavana data pro fragment shader
in FragData {
	vec3 fragUV;
    vec4 Color;
	vec3 Normal;
	vec3 LightDirection;
	vec3 ViewDirection;
	float ommit;
} inFragData[];

out FragData {
	vec3 fragUV;
	vec4 Color;
	vec3 Normal;
	vec3 LightDirection;
	vec3 ViewDirection;
	float ommit;
} outFragData;

void main (void) 
{
		// vykreslovani normal
		if (showNormals != 0) {

			// velikost normaly
			float size = 1.0;

			// trojuhelnik sam o sobe, odesle se na vystup
			int i;
			for ( i = 0; i < gl_in.length(); i++ )
			{
				vec4 Po = projMatrix * viewMatrix * modelMatrix * gl_in[i].gl_Position;

				gl_Position = Po;
				outFragData.fragUV = inFragData[i].fragUV;
				outFragData.Color = inFragData[i].Color;
				outFragData.Normal = (normalMatrix * vec4(normalize(inFragData[i].Normal), 1.0)).xyz;
				outFragData.LightDirection = inFragData[i].LightDirection;
				outFragData.LightDirection = inFragData[i].ViewDirection;
				outFragData.ommit = inFragData[i].ommit;
				EmitVertex();
			}
			EndPrimitive();

			// vertexove normaly
			int n;
			for ( n = 0; n < gl_in.length(); n++ ) {

				vec4 P = gl_in[n].gl_Position;
				vec4 PN = vec4(P + vec4(inFragData[n].Normal.xyz, .0) * size);

				gl_Position = projMatrix * viewMatrix * modelMatrix * P;
				outFragData.Color = vec4(.0, .0, 1.0, 1.0);
				outFragData.ommit = (-10.0);
				EmitVertex();

				gl_Position = projMatrix * viewMatrix * modelMatrix * PN;
				outFragData.Color = vec4(.0, .0, 1.0, 1.0);
				outFragData.ommit = (-10.0);
				EmitVertex();

				gl_Position = projMatrix * viewMatrix * modelMatrix * P;
				outFragData.Color = vec4(.0, .0, 1.0, 1.0);
				outFragData.ommit = (-10.0);
				EmitVertex();

				EndPrimitive();
			}

			// fazetova normala

			// rohy trojuhelnika
			vec3 P0 = gl_in[0].gl_Position.xyz;
			vec3 P1 = gl_in[1].gl_Position.xyz;
			vec3 P2 = gl_in[2].gl_Position.xyz;

			// normalovy vektor trojuhelnikove plochy
			vec3 N = normalize(cross(P1 - P2, P0 - P1));

			// stred trojuhelnika ve 3D
			vec3 PT = (P0 + P1 + P2) / 3.0;
			// koncovy vertex normaly
			vec4 PTN = vec4(PT + N.xyz * size, 1.0);

			gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(PT, 1.0);
			outFragData.Color = vec4(.0, 1.0, .0, 1.0);
			outFragData.ommit = (-10.0);
			EmitVertex();

			gl_Position = projMatrix * viewMatrix * modelMatrix * PTN;
			outFragData.Color = vec4(.0, 1.0, .0, 1.0);
			outFragData.ommit = (-10.0);
			EmitVertex();

			gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(PT, 1.0);
			outFragData.Color = vec4(.0, 1.0, .0, 1.0);
			outFragData.ommit = (-10.0);
			EmitVertex();

			EndPrimitive();

		} else {

			// normalni vykreslovani, nic se nemeni

			// shader zachyti kazdy trojuhelnik
			int i;
			for( i = 0; i < gl_in.length(); i++ )
			{
				// zachovani vstupni barvy a transformace normaly
				outFragData.Color = inFragData[i].Color;
				outFragData.Normal = (normalMatrix * vec4(normalize(inFragData[i].Normal), 1.0)).xyz;
				outFragData.fragUV = inFragData[i].fragUV;
				outFragData.LightDirection = inFragData[i].LightDirection;
				outFragData.ViewDirection = inFragData[i].ViewDirection;
				outFragData.ommit = inFragData[i].ommit;

				// pozicovani maticemi
				gl_Position = projMatrix * viewMatrix * modelMatrix * gl_in[i].gl_Position;
				EmitVertex();
			}
			// hotovo
			EndPrimitive();

		}

}