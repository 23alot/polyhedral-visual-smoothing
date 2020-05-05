attribute vec3 vertexPosition;
attribute vec3 vertexColour;
attribute vec3 vertexNormal;
varying vec3 fragColour;
uniform mat4 modelView;
uniform mat4 uMVPMatrix;

void main() {
    vec3 transformedVertexNormal = normalize((modelView * vec4(vertexNormal, 0.0)).xyz);
    vec3 inverseLightDirection = normalize(vec3(0.0, -0.5, 0.8));
    gl_Position = uMVPMatrix * vec4(vertexPosition, 1.0);
    fragColour = vertexColour;
    vec3 diffuseLightIntensity = vec3(1.0, 1.0, 1.0);
    vec3 vertexDiffuseReflectionConstant = vertexColour;
    float normalDotLight = max(0.0, dot(transformedVertexNormal, inverseLightDirection));
    fragColour += normalDotLight * vertexDiffuseReflectionConstant * diffuseLightIntensity;
    vec3 ambientLightIntensity = vec3(0.4, 0.4, 0.4);
    vec3 vertexAmbientReflectionConstant = vertexColour;
    fragColour += vertexAmbientReflectionConstant * ambientLightIntensity;
    vec3 inverseEyeDirection = normalize(vec3(0.0, 1.0, 1.0));
    vec3 specularLightIntensity = vec3(0.4, 0.4, 0.4);
    vec3 vertexSpecularReflectionConstant = vec3(1.0, 1.0, 1.0);
    float shininess = 2.0;
    vec3 lightReflectionDirection = reflect(vec3(0.0) - inverseLightDirection, transformedVertexNormal);
    float normalDotReflection = max(0.0, dot(inverseEyeDirection, lightReflectionDirection));
    fragColour += pow(normalDotReflection, shininess) * vertexSpecularReflectionConstant * specularLightIntensity;
}