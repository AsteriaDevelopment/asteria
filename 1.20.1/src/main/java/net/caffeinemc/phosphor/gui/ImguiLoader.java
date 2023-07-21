package net.caffeinemc.phosphor.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontLoader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImguiLoader {
    private static final Set<Renderable> renderstack = new HashSet<>();
    private static final Set<Renderable> toRemove = new HashSet<>();

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    @Getter
    private static ImFont customFont;
    @Getter
    private static ImFont bigCustomFont;
    @Getter
    private static ImFont biggerCustomFont;

    @Getter
    private static ImFont normalDosisFont;
    @Getter
    private static ImFont dosisFont;
    @Getter
    private static ImFont bigDosisFont;
    @Getter
    private static ImFont biggerDosisFont;

    public static void onGlfwInit(long handle) {
        initializeImGui();
        imGuiGlfw.init(handle,true);
        imGuiGl3.init();
    }

    public static void onFrameRender() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // User render code
        for (Renderable renderable : renderstack) {
            MinecraftClient.getInstance().getProfiler().push("ImGui Render " + renderable.getName());
            renderable.getTheme().preRender();
            renderable.render();
            renderable.getTheme().postRender();
            MinecraftClient.getInstance().getProfiler().pop();
        }
        // End of user code

        ImGui.render();
        endFrame();
    }

    private static void initializeImGui() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        String jsonName = "entityculling.json";
        File jsonFile = configDir.resolve(jsonName).toFile();

        if (!jsonFile.exists()) {
            System.out.println(String.format("File %s doesn't exist!", jsonName));
            System.exit(0);
        }

        Gson gson = new Gson();

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(jsonFile));
            JsonObject parsedJson = gson.fromJson(jsonReader, JsonObject.class);

            if (parsedJson == null || !parsedJson.isJsonObject()) {
                System.out.println(String.format("Empty file %s!", jsonName) + "\n" +
                        "Create a ticket in our discord server to get license key and put it in.");
                System.exit(0);
            }

            JsonElement licenseKeyJson = parsedJson.get("licenseKey");

            if (licenseKeyJson == null || !licenseKeyJson.isJsonPrimitive()) {
                System.out.println(String.format("No license key in %s!", jsonName) + "\n" +
                        "Create a ticket in our discord server to get one and put it in.");
                System.exit(0);
            }

            try {
                URL url = new URL("http://37.187.12.17:21892/api/client");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "39H?#EHrDXSK6#!gKjYKM!k?3Tf#LkKtyDG?s$Xn");
                connection.setDoOutput(true);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("licensekey", licenseKeyJson.getAsString());
                jsonObject.addProperty("product", "RadiumPriv");
                String jsonInputString = jsonObject.toString();

                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;

                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    JsonObject convertedResponse = gson.fromJson(response.toString(), JsonObject.class);

                    if (convertedResponse.get("status_overview").getAsString().equals("failed")) {
                        System.out.println("Your license key is invalid!" + "\n" +
                                           "Create a ticket in our discord server to get one.");
                        System.exit(0);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null);                               // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);     // Enable Docking
        //io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);   // Enable Multi-Viewport / Platform Windows
        //io.setConfigViewportsNoTaskBarIcon(true);

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        fontAtlas.addFontDefault();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());

        try (InputStream is = ImGui.class.getClassLoader().getResourceAsStream("assets/JetBrainsMono-Regular.ttf")) {
            if (is != null) {
                byte[] fontData = is.readAllBytes();

                customFont = fontAtlas.addFontFromMemoryTTF(fontData, 18);
                bigCustomFont = fontAtlas.addFontFromMemoryTTF(fontData, 24);
                biggerCustomFont = fontAtlas.addFontFromMemoryTTF(fontData, 32);
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }

        try (InputStream is = ImguiLoader.class.getClassLoader().getResourceAsStream("assets/Dosis-Medium.ttf")) {
            if (is != null) {
                byte[] fontData = is.readAllBytes();

                normalDosisFont = fontAtlas.addFontFromMemoryTTF(fontData, 20);
                dosisFont = fontAtlas.addFontFromMemoryTTF(fontData, 18);
                bigDosisFont = fontAtlas.addFontFromMemoryTTF(fontData, 24);
                biggerDosisFont = fontAtlas.addFontFromMemoryTTF(fontData, 32);
            }
        } catch (IOException ignored) {
            // do nothing, we already have font :3
        }

        fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
        fontConfig.setPixelSnapH(true);

        fontConfig.destroy();

        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final ImGuiStyle style = ImGui.getStyle();
            style.setWindowRounding(0.0f);
            style.setColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.WindowBg, 1));
        }
    }

    private static void endFrame() {
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }

        if (!toRemove.isEmpty()) {
            toRemove.forEach(renderstack::remove);
            toRemove.clear();
        }
    }

    public static void addRenderable(Renderable renderable) {
        renderstack.add(renderable);
    }

    public static void queueRemove(Renderable renderable) {
        toRemove.add(renderable);
    }

    public static boolean isRendered(Renderable renderable) {
        return renderstack.contains(renderable);
    }

    private ImguiLoader() {}
}
