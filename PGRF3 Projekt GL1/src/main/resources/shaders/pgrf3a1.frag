#version 330

// reflektorovy zdroj svetla
uniform int reflector;

// slozky materialu
uniform vec4 Ambient, Specular, Diffuse, Emission;
uniform float Shininess;

// reflektor
uniform float spotCutOff;
uniform vec3 spotDirection;
uniform vec4 LightColor;

// utlum prostedi
uniform float constantAttenuation, linearAttenuation, quadraticAttenuation;

// vstupni data
in FragData {
	vec3 fragUV;
    vec4 Color;
	vec3 Normal;
	vec3 LightDirection;
	vec3 ViewDirection;
	float ommit;
} inFragData;

uniform int perfragment;
uniform int demoId, gridSize;
uniform int showTexture, bump, parallax;

// http://stackoverflow.com/questions/26266198/glsl-invalid-call-of-undeclared-identifier-texture2d
uniform sampler2D texture;
uniform sampler2D texture_normal;
uniform sampler2D texture_height;
vec2 vOffset = vec2(0.04, -0.02);

out vec4 outColor;

#define PI 3.141592654

void main()
{
	vec2 texCoord = inFragData.fragUV.xy;
	vec4 color = inFragData.Color;

	// hlavni vypocet
	if (inFragData.ommit > .0) {

		// prepocet souradnic textury
		if (showTexture == 1 && perfragment == 1) {

			// TODO mapping
			switch (demoId) {
				// sfericke
				case 30:
				case 3:
				case 4:
					texCoord.y = acos( inFragData.fragUV.z ) / PI;
					texCoord.x = atan( (inFragData.fragUV.x)/(inFragData.fragUV.y) ) / 2.0 / PI;
					texCoord.x += 0.25; // ctvrtkoule
					if (sign( inFragData.fragUV.y ) <= 0.0) texCoord.x += 0.5;
				break;

				default:
					texCoord = inFragData.fragUV.xy / (gridSize + 1.0);
			}

		}
		

		// pocitani per-fragment
		if (perfragment == 1) {

			vec3 ld = normalize( inFragData.LightDirection );
			vec3 nd = normalize( inFragData.Normal );
			vec3 vd = normalize( inFragData.ViewDirection );


			// textura
			if (showTexture == 1) {
				
				vec2 texUV = texCoord;

				// normal mapping
				if (bump == 1 && parallax == 0) {
					
					// bump + prichozi kalkulace v ld a vd
					nd = normalize ( texture(texture_normal, texCoord.xy).rgb * 2.0 - 1.0 );

				}
				if (bump == 0 && parallax == 1) {

					nd = normalize ( texture(texture_normal, texCoord.xy).rgb * 2.0 - 1.0 );

					// cteni vyskove mapy
					float height = texture(texture_height, texCoord.xy).r * vOffset.x + vOffset.y;
					texUV = texCoord + vd.xy * height;
				}

				color = texture(texture, texUV).rgba;
			}

			// difuzni - kladne cislo z uhlu mezi pohledem a normalou
			float NdotL = max( dot(nd, ld), 0.0 );

			// spekularni
			float RdotV = max( 0.0, dot( (normalize( ( ( 2.0 * nd ) * NdotL ) - ld) ), vd ) );

			// vsechny slozky
			vec4 amb = Ambient * color * LightColor;
			vec4 dif = Diffuse * NdotL * color * LightColor;
			vec4 spe = Specular * ( pow( RdotV, Shininess ) );

			// vzdalenost od zdroje svetla pro odhad utlumu
			float dist = length( inFragData.LightDirection );

			// utlum prostedi
			float att = 1.0/(constantAttenuation + linearAttenuation * dist + quadraticAttenuation * dist * dist);

			// kuzel svetla
			float spotEffect = dot( normalize( spotDirection ), normalize( -ld ) );

			// vysledek
			if (reflector == 0 || spotEffect > spotCutOff) {
				outColor = Emission + amb + att * (dif + spe);
			} else {
				outColor = Emission + amb;
			}

		} else {
			// per vertex

			if (showTexture == 0) {
				outColor = inFragData.Color;
			}

			if (showTexture == 1) {
				// textura + korekce
				outColor = inFragData.Color * texture(texture, texCoord).rgba;
			}
		}

	} else {
		// GS-emitovane normaly se nijak nezpracovavaji
		outColor = inFragData.Color;
	}
}
