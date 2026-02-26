package engine.graphics;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programId;
    private final java.util.Map<String, Integer> uniforms = new HashMap<>();

    public void createUniform(String uniformName){
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if(uniformLocation < 0){
            throw new RuntimeException("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    private String loadShaderSource(String path) {
        InputStream stream = ShaderProgram.class.getResourceAsStream("/" + path);

        if (stream == null){
            throw new IllegalArgumentException("Shader file not found: " + path);
        }

        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private int compileShader(int shaderType, String sourceCode) {
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, sourceCode);
        glCompileShader(shaderId);

        int compileStatus = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (compileStatus == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shaderId);
            glDeleteShader(shaderId);
            throw new RuntimeException("Failed to compile shader: " + infoLog);
        }
        return shaderId;
    }

    public ShaderProgram(String vertexPath, String fragmentPath) {
        String vertexSource = loadShaderSource(vertexPath);
        String fragmentSource = loadShaderSource(fragmentPath);

        int vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        int programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        int linkStatus = glGetProgrami(programId, GL_LINK_STATUS);
        if (linkStatus == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(programId);
            glDeleteProgram(programId);
            throw new RuntimeException("Failed to link shader program: " + infoLog);
        }

        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        this.programId = programId;
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind(){
        glUseProgram(0);
    }

    public void cleanup(){
        unbind();
        if(programId != 0){
            glDeleteProgram(programId);
        }
    }

}
