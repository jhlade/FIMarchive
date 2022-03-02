#version 330

// uniformni vstupy pro objekt
uniform mat4 modelMatrix, viewMatrix, projMatrix;
uniform mat4 normalMatrix;

// dynamicky citac
uniform float dyncounter;
uniform int gridSize;

// volba demo programu
uniform int demoId;

// model per-fragment / per-vertex
uniform int perfragment;
uniform int showTexture, bump, parallax;

// parametry svetla
uniform vec3 LightPosition;
uniform vec3 CameraPosition;

// slozky materialu
uniform vec4 Ambient, Specular, Diffuse, Emission;
uniform float Shininess;

// reflektor
uniform float spotCutOff;
uniform vec3 spotDirection;
uniform vec4 LightColor;

// utlum prostedi
uniform float constantAttenuation, linearAttenuation, quadraticAttenuation;

// vstup gridu
layout(location = 2) in vec2 uvPosition;

// vystupni data pro fragment shader
out FragData {
	vec3 fragUV;
	vec4 Color;
	vec3 Normal;
	vec3 LightDirection;
	vec3 ViewDirection;
	float ommit; // hack pro pouziti GS ve FS - vynechaji se virtualni normaly
} outFragData;

#define PI 3.141592654

////////////////////////////////////////////////////////////////////////////////
// MRIZKA z = f(x, y) = 0, prototyp

float fx0(vec2 P) {
	return 0.0;
}

////////////////////////////////////////////////////////////////////////////////
// Z ukazky, z = f(x, y) = 4.5 * sin(4 * cas) * cos( sqrt(20x^2 + 20y^2) )

float fx1(vec2 P) {
	return 4.5 * sin(dyncounter * 4) * cos( sqrt( 20*(P.x * P.x) + 20*(P.y * P.y) ) );
}

////////////////////////////////////////////////////////////////////////////////
// z = f(x, y) = 

float fx2(vec2 P) {
	return 4.5 * sin(12*pow(P.x,2))*cos(P.y*3*sin(dyncounter));
}

////////////////////////////////////////////////////////////////////////////////
// KOULE R = f(s, t) = 12.0, prototyp

float sp0(vec2 P) {
	return 12.0;
}

////////////////////////////////////////////////////////////////////////////////
// UFO, R = 16 + sin(cas) * cos(7 * t)

float sp1(vec2 P) {
	return 16.0 + 2 * sin(-dyncounter) * cos(7 * P.y);
}

////////////////////////////////////////////////////////////////////////////////
// Vetrnik, R = 12 + 3*cos(4*t-3*s);

float sp2(vec2 P) {
	return 12 + 3*cos(4*P.y-3*P.x+dyncounter);
}

////////////////////////////////////////////////////////////////////////////////
// VALEC v = f(s, t) = 0.0, prototyp

float cy0(vec2 P) {
	return 0.0;
}

////////////////////////////////////////////////////////////////////////////////
// sombrerko, v = 7 * sin(t)

float cy1(vec2 RS) {
	return 7 * sin( RS.y + sin(dyncounter));
}

////////////////////////////////////////////////////////////////////////////////
// , v =

float cy2(vec2 RS) {
	return 7 * sin( RS.x + sin(6*dyncounter)) + 7*sin(dyncounter)*cos( 2 * RS.y - 3*sin(dyncounter));
}


////////////////////////////////////////////////////////////////////////////////
// Torus - pro rozdil mezi per-vertex a per-fragment osvetlenim

vec3 torus(vec2 UV) {
	vec3 XY;

	XY.x = 16 * cos(UV.x) - 4 * cos(UV.y) * cos(UV.x);
	XY.y = 16 * sin(UV.x) - 4 * cos(UV.y) * sin(UV.x);
	XY.z =  4 * sin(UV.y);

	return XY;
}

////////////////////////////////////////////////////////////////////////////////
// wrapper pro puvodni ctvercovou(!) mrizku v rozsahu 0-mx

vec3 funcWrapper(vec2 Puv) {

	// velikost mrizky - pocita se se cvtercovou siti
	float mx = 1.0 + gridSize;

	// kartezske
	vec2 Xuv;
	// zmena rozsahu XY
	Xuv = (Puv / mx) * (2.0) - 1.0;

	// prevod zpet
	vec3 XY;
	XY.xy = vec2(mx * (Xuv.xy + 1) / 2.0);

	// sfericke
	float s = ( Puv.y / (mx - 1) ) * 2 * PI; // azimuth
	float t = ( Puv.x / (mx - 1) ) * 1 * PI; // zenith
	float R;

	vec3 SP;
	SP.x = sin(t) * cos(s);
	SP.y = sin(t) * sin(s);
	SP.z = cos(t);

	// cylindricke
	float cs = ( Puv.y / (mx - 1) ) * 2 * PI; // azimuth
	float ct = ( Puv.x / (mx - 1) ) * 2 * PI; // zenith
	float cR = 4 * ct;
	float v;

	vec3 CY;
	CY.x = cR * cos(cs);
	CY.y = cR * sin(cs);

	switch(demoId) {
		
		// kartezske1 z = fx1(x, y)
		case 10:
			XY.z = fx0(Xuv);

			return XY;
		break;

		// debug - test - kartezske
		case 0:
		case 1:
			XY.z = fx1(Xuv);

			return XY;
		break;

		// kartezske2 z = fx(x, y)
		case 2:
			XY.z = fx2(Xuv);

			return XY;
		break;

		// sfericke1 R = sp1(s1, t1)
		case 30:
			R = sp0( vec2(s, t) );

			outFragData.fragUV = SP;

			SP *= R;
			SP.xy += (mx/2);
			SP.z *= 2.0;

			return SP;
		break;

		case 3:
			R = sp1( vec2(s, t) );

			outFragData.fragUV = SP;

			SP *= R;
			SP.xy += (mx/2);

			return SP;
		break;

		// sfericke2 R = sp2(s2, t2)
		case 4:
			R = sp2( vec2(s, t) );

			outFragData.fragUV = SP;

			SP *= R;
			SP.xy += (mx/2);

			return SP;
		break;

		// cylindricke0
		case 50:
			v = cy0( vec2(cs, ct) );
			CY.z = v;

			CY.xy += (mx/2);
			return CY;
		break;

		// cylindricke1 - sombrero
		case 5:
			v = cy1( vec2(cs, ct) );
			CY.z = v;

			CY.xy += (mx/2);
			return CY;
		break;

		// cylindricke2
		case 6:
			v = cy2( vec2(cs, ct) );
			CY.z = v;

			CY.xy += (mx/2);
			return CY;
		break;

		// torus
		case 7:
			CY = torus( vec2(cs, ct) );

			CY.xy += (mx/2);
			return CY;
		break;

		default:
			return vec3(Puv, 0);
	}
} 

////////////////////////////////////////////////////////////////////////////////

vec3 calcNormal(vec2 uv) {

	float diff = 0.01;
	vec3 df = vec3(diff, diff, 0);

	// FDM
	vec3 dzdu = ( funcWrapper(uv + df.xz ) - funcWrapper( uv - df.xz ) ) / 2.0 / diff;
	vec3 dzdv = ( funcWrapper(uv + df.zy ) - funcWrapper( uv - df.zy ) ) / 2.0 / diff;

	return normalize(cross(dzdu, dzdv));
}

vec3 calcTangent(vec2 uv) {
	return vec3(1,1,1);
}

////////////////////////////////////////////////////////////////////////////////

void main()
{
	// nejaka obecna inicializace
	outFragData.ommit = 1.0;
	outFragData.Color = vec4(1, 1, 1, 1.0);

	// (x = u, y = v, z = 0, w = 1.0)
	vec4 asPosition = vec4(uvPosition, 0, 1.0);

	// funkce
	asPosition.xyz = funcWrapper( uvPosition );

	// UV-koordinaty textury
	outFragData.fragUV = asPosition.xyz;//uvPosition;//(uvPosition / (1.0 + gridSize));

	// normala
	vec3 N = calcNormal( uvPosition );
	//outFragData.Normal = ((normalMatrix * vec4(normalize(N), 1.0)).xyz);
	outFragData.Normal = N;

	// vektory v prostoru projekce
	// smer svetla
	outFragData.LightDirection = (projMatrix * viewMatrix * modelMatrix * asPosition).xyz - (projMatrix * viewMatrix * vec4(LightPosition, 1.0)).xyz;
	// smer kamery
	outFragData.ViewDirection = (projMatrix * viewMatrix * modelMatrix * asPosition).xyz - (projMatrix * viewMatrix * vec4(CameraPosition, 1.0)).xyz;

	// pocitani per-vertex
	if (perfragment == 0) {
	
		vec3 ld = normalize( outFragData.LightDirection );
		vec3 nd = normalize( (normalMatrix * vec4(outFragData.Normal, 1.0)).xyz );
		vec3 vd = normalize( outFragData.ViewDirection );

		// difuzni - kladne cislo z uhlu mezi pohledem a normalou
		float NdotL = max( dot(nd, ld), 0.0 );

		// spekularni
		float RdotV = max( 0.0, dot( (normalize( ( ( 2.0 * nd ) * NdotL ) - ld) ), vd ) );

		// vsechny slozky
		vec4 amb = Ambient * LightColor;
		vec4 dif = Diffuse * NdotL * LightColor;
		vec4 spe = Specular * ( pow( RdotV, Shininess ) );

		// vzdalenost od zdroje svetla pro odhad utlumu
		float dist = length( outFragData.LightDirection );

		// utlum prostedi
		float att = 1.0/(constantAttenuation + linearAttenuation * dist + quadraticAttenuation * dist * dist);

		outFragData.Color = Emission + amb + att * (dif + spe);

		// TODO mapovani vsech souradnic
		// per vertex texture mapping?
		if (showTexture == 1) {

			vec3 tmpMap = outFragData.fragUV;

			switch (demoId) {

				// sfericke
				case 30:
				case 3:
				case 4:
					outFragData.fragUV.y = acos( tmpMap.z ) / PI;
					outFragData.fragUV.x = atan( (tmpMap.x)/(tmpMap.y) ) / 2.0 / PI;
					outFragData.fragUV.x += 0.25; // ctvrtkoule
					if (sign( outFragData.fragUV.y ) <= 0.0) outFragData.fragUV.x += 0.5;
				break;

				default:
					outFragData.fragUV.xy = tmpMap.xy / (gridSize + 1.0);
			}

			// normal mapping
			if (bump == 1 && parallax == 0) {

				// TODO binormala + tangenta, modif. view a light vektoru
				// TODO pristup k texture

				vec3 tangent;
				vec3 tmpT1 = cross(outFragData.Normal, vec3(0.0, 0.0, 1.0)); 
				vec3 tmpT2 = cross(outFragData.Normal, vec3(0.0, 1.0, 0.0));

				if(length(tmpT1) > length(tmpT2)) {tangent = tmpT1;} else {tangent = tmpT2;}

				// TBN matice
				vec3 n = outFragData.Normal;
				vec3 t = normalize(normalMatrix * vec4(tangent, 1.0)).xyz;
				vec3 b = cross(outFragData.Normal, t);
				

				//--> outFragData.ViewDirection;
				//--> outFragData.LightDirection;

				// prepocet 
				vec3 tmpVD, tmpLD;
				tmpVD.x = dot(t, outFragData.ViewDirection);
				tmpVD.y = dot(b, outFragData.ViewDirection);
				tmpVD.z = dot(n, outFragData.ViewDirection);

				tmpLD.x = dot(t, outFragData.LightDirection);
				tmpLD.y = dot(b, outFragData.LightDirection);
				tmpLD.z = dot(n, outFragData.LightDirection);

				outFragData.ViewDirection = tmpVD;
				outFragData.LightDirection = tmpLD;
			}
			// parallax mapping
			if (bump == 0 && parallax == 1) {
			}
		}
	}

	// vysledne maticove operace realizuje az geometry shader
	gl_Position = asPosition;

}
