attribute vec4 vPosition;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
uniform mat4 u_xform;
uniform mat4 u_rotation;

void main() {
    gl_Position = vPosition;
    v_texCoord = vec2(u_xform * u_rotation * vec4(a_texCoord, 0.0, 1.0));
}
