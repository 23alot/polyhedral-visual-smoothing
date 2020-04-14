attribute vec3 vertexPosition;
attribute vec3 vertexColour;
varying vec3 fragColour;
uniform mat4 uMVPMatrix;

void main() {
    gl_Position = uMVPMatrix * vec4(vertexPosition, 1.0);
    fragColour = vertexColour;
}