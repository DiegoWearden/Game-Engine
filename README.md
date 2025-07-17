# Game Engine

A 3D game engine built with LWJGL (Lightweight Java Game Library) featuring terrain rendering, water effects, lighting, and entity management.

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- **Make**: For building and running the project
- **Linux**: This project is configured for Linux (native libraries are in `lwjgl/lwjgl-2.9.3/native/linux/`)

## Installation

### Installing Java (JDK)

#### Ubuntu/Debian:
```bash
# Update package list
sudo apt update

# Install OpenJDK 11 (recommended)
sudo apt install openjdk-11-jdk

# Or install OpenJDK 8
sudo apt install openjdk-8-jdk

# Verify installation
java -version
javac -version
```

#### Arch Linux:
```bash
# Install OpenJDK 11
sudo pacman -S jdk11-openjdk

# Or install OpenJDK 8
sudo pacman -S jdk8-openjdk

# Verify installation
java -version
javac -version
```

#### Fedora/RHEL/CentOS:
```bash
# Install OpenJDK 11
sudo dnf install java-11-openjdk-devel

# Or install OpenJDK 8
sudo dnf install java-1.8.0-openjdk-devel

# Verify installation
java -version
javac -version
```

#### Manual Installation (All Linux Distributions):
If your distribution doesn't have the JDK in its repositories:

1. Download OpenJDK from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Extract the archive:
   ```bash
   tar -xzf openjdk-11.0.x_linux-x64_bin.tar.gz
   ```
3. Move to `/usr/local/`:
   ```bash
   sudo mv jdk-11.0.x /usr/local/
   ```
4. Set environment variables in `~/.bashrc`:
   ```bash
   echo 'export JAVA_HOME=/usr/local/jdk-11.0.x' >> ~/.bashrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
   source ~/.bashrc
   ```

### Installing Make

Most Linux distributions come with Make pre-installed. If not:

#### Ubuntu/Debian:
```bash
sudo apt install make
```

#### Arch Linux:
```bash
sudo pacman -S make
```

#### Fedora/RHEL/CentOS:
```bash
sudo dnf install make
```

### Verifying Your Setup

After installation, verify everything is working:

```bash
# Check Java version (should be 8 or higher)
java -version

# Check Java compiler version
javac -version

# Check Make version
make --version

# Check if you're on Linux
uname -s
```

## Project Structure

```
Game-Engine/
├── src/main/java/          # Java source code
│   ├── engineTester/       # Main game loop and entry point
│   ├── entities/           # Game entities (Player, Camera, Light, etc.)
│   ├── renderEngine/       # Rendering system
│   ├── shaders/           # OpenGL shaders
│   ├── models/            # 3D model handling
│   ├── textures/          # Texture management
│   ├── terrains/          # Terrain generation and rendering
│   ├── water/             # Water effects and rendering
│   ├── guis/              # GUI system
│   └── toolbox/           # Utility classes
├── src/res/               # Game resources (textures, models, etc.)
├── lib/                   # Additional libraries
├── lwjgl/                 # LWJGL library files
└── target/                # Compiled classes (generated)
```

## Quick Start

### 1. Build the Project

```bash
make all
```

This command will:
- Create the `target/` directory
- Compile all Java source files
- Include all necessary LWJGL and library dependencies

### 2. Run the Game

```bash
make run
```

This command will:
- Build the project if needed
- Set the native library path for LWJGL
- Launch the game with `engineTester.MainGameLoop`

### 3. Clean Build Files

```bash
make clean
```

This removes the compiled classes from the `target/` directory.

## Controls

- **WASD**: Move the player character
- **Mouse**: Look around
- **ESC**: Close the game

## Features

- **3D Terrain Rendering**: Heightmap-based terrain with multiple texture layers
- **Water Effects**: Realistic water with reflection and refraction
- **Dynamic Lighting**: Multiple light sources with attenuation
- **Entity System**: Trees, grass, and other objects scattered across the terrain
- **Player Movement**: First-person camera with collision detection
- **Texture Blending**: Smooth transitions between different terrain textures

## Troubleshooting

### Common Issues

1. **"NoClassDefFoundError: org/lwjgl/LWJGLException"**
   - Make sure you've run `make clean` followed by `make all` to ensure proper compilation
   - Verify that all LWJGL JAR files are present in `lwjgl/lwjgl-2.9.3/jar/`

2. **"java.lang.UnsatisfiedLinkError"**
   - Ensure you're running on Linux (native libraries are Linux-specific)
   - Check that the native library path is correctly set in the Makefile

3. **Performance Issues**
   - The game renders many entities (500+ trees and ferns)
   - Consider reducing the entity count in `MainGameLoop.java` if needed

### Manual Compilation

If you prefer not to use Make, you can compile manually:

```bash
# Create build directory
mkdir -p target

# Compile
javac -cp "lib/*:lwjgl/lwjgl-2.9.3/jar/*" -d target \
      $(find src -name '*.java')

# Run
java -Djava.library.path=lwjgl/lwjgl-2.9.3/native/linux \
     -cp "target:lib/*:lwjgl/lwjgl-2.9.3/jar/*" \
     engineTester.MainGameLoop
```

## Dependencies

- **LWJGL 2.9.3**: Core OpenGL bindings and window management
- **JInput**: Input handling
- **Slick-Util**: Additional utilities
- **PNG Decoder**: PNG image loading

## Development

The main entry point is `src/main/java/engineTester/MainGameLoop.java`. This class:
- Initializes the display and OpenGL context
- Sets up terrain, entities, and lighting
- Runs the main game loop
- Handles rendering and input

## License

This project uses LWJGL which is licensed under the BSD license. See `lwjgl/lwjgl-2.9.3/doc/LICENSE` for details. 