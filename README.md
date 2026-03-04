# Solo Leveling

![Java](https://img.shields.io/badge/Language-Java-orange.svg) 
![Graphics](https://img.shields.io/badge/Graphics-OpenGL-blue.svg)
![Status](https://img.shields.io/badge/Status-In--Development-green.svg)

A high-performance, custom-built 3D game engine in pure Java, engineered specifically to handle the scaling mechanics of *Solo Leveling*. Built entirely from scratch using **LWJGL 3** (Lightweight Java Game Library), this project abandons commercial engines in favor of raw memory management, custom shader pipelines, and strict data-oriented design.

---

## 👨‍💻 Developer's note

This engine is currently being developed by a 6/7-semester CS student. It actively serves as a hands-on learning environment for lower-level software architecture, manual memory management, and computer graphics in Java. 

Because this is a from-scratch learning endeavor, expect frequent refactoring, architectural shifts, and continuous optimizations as my own engineering knowledge (and "The System") levels up. Feedback and code reviews from senior developers are always welcome.

## 🏗️ Engine Architecture

Since there is no Unity or Godot under the hood, we have to build the core systems ourselves. The architecture here is designed around one main problem: **performance**. When Jin-Woo spawns 100+ shadow soldiers on screen, standard Java object-oriented programming will choke the CPU. 

To prevent that, the engine relies on a few specific design patterns:

* **Entity Component System (ECS):** We completely avoid deep inheritance trees (e.g., `class Shadow extends Monster extends Entity`). Instead, everything is just an ID. Data (Position, Health, Stats) is stored in raw **Components**, and logic (Physics, Combat) is handled by **Systems**. This keeps data tightly packed in memory, preventing cache misses and keeping the frame rate high during massive dungeon fights.
* **Fixed-Timestep Game Loop:** The rendering pipeline is completely decoupled from the physics ticks. Whether you are playing on a 60Hz laptop monitor or a 240Hz gaming display, the physics math (gravity, movement speed) calculates at the exact same rate.
* **Finite State Machines (FSM):** Combat in *Solo Leveling* is fast and combo-heavy. Instead of a massive, unreadable pile of `if/else` statements to check if the player is jumping, dashing, or attacking, an FSM strictly controls what state the player is in and what animations/hitboxes are currently active.
* **Event Bus / Observer Pattern:** The core game logic shouldn't care about the UI. When an enemy dies, the combat system just fires a `MobDeathEvent`. The "System" UI listens for that event in the background and updates the XP bar or triggers a Level Up notification without tangling the codebases together.

---

## 🛠 Technology Stack

* **Language:** Java 21 (Utilizing modern features like Records for ECS Components and Virtual Threads for async asset loading).
* **Core API:** LWJGL 3 (GLFW for windowing/context, OpenGL 3.3+ for rendering).
* **Mathematics:** JOML (Java OpenGL Math Library) for heavily optimized matrix and vector operations.
* **Memory Management:** Manual off-heap memory allocation via `MemoryUtil` to bypass Java Garbage Collection stutter during intense combat frames.

---

## 📂 Engine Directory Structure

```text
SoloLeveling/
├── lib/                    # External LWJGL3 & JOML dependencies
├── resources/              # Non-compiled assets (VRAM targets)
│   ├── shaders/            
│   │   ├── fragment.glsl   # GPU Pixel painting logic
│   │   └── vertex.glsl     # GPU Geometry & UV mapping logic
│   └── textures/           
│       └── test.jpeg       # Current active test sprite
└── src/                    # Main Source Code
    ├── engine/             # The Custom Rendering Engine
    │   ├── core/           
    │   │   ├── Main.java       # Engine entry point & Main Game Loop
    │   │   └── Transform.java  # Spatial Math: Position, Rotation, Scale matrices
    │   └── graphics/       
    │       ├── Camera.java         # 3D Viewport navigation & View Matrix
    │       ├── Mesh.java           # VBO/VAO/EBO geometry & UV coordinate memory
    │       ├── ShaderProgram.java  # GLSL Compilation & Uniform binding
    │       └── Texture.java        # stb_image C-library integration
    └── game.characters/    # Game-specific logic & prefabs
        └── SungJinWoo.java
