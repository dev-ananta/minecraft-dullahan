# Dullahan Mod Setup Guide

This guide will help you set up and integrate all the provided files into a working Minecraft Forge mod.

## Quick Start

### Step 1: Organize The Files

Create the following directory structure:

```
DullahanMod/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradlew
├── gradlew.bat
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── dullahan/
        │           └── dullahan/
        │               ├── DullahanMod.java
        │               ├── entity/
        │               │   ├── DullahanEntity.java
        │               │   └── DullahanAttackGoal.java
        │               ├── client/
        │               │   ├── ClientEvents.java
        │               │   └── renderer/
        │               │       └── DullahanRenderer.java
        │               └── init/
        │                   ├── ModEntities.java
        │                   └── ModSounds.java
        └── resources/
            ├── META-INF/
            │   └── mods.toml
            ├── assets/
            │   └── dullahan/
            │       ├── sounds.json
            │       ├── lang/
            │       │   └── en_us.json
            │       ├── sounds/
            │       │   └── entity/
            │       │       └── dullahan/
            │       │           ├── ambient.ogg
            │       │           ├── hurt.ogg
            │       │           ├── death.ogg
            │       │           └── spawn.ogg
            │       └── textures/
            │           └── entity/
            │               └── dullahan.png
            └── data/
                └── dullahan/
                    └── loot_tables/
                        └── entities/
                            └── dullahan.json
```

### Step 2: Place Java Files

Copy the Java files to their respective directories:

1. **DullahanMod.java** → `src/main/java/com/dullahan/dullahan/`
2. **DullahanEntity.java** → `src/main/java/com/dullahan/dullahan/entity/`
3. **DullahanAttackGoal.java** → `src/main/java/com/dullahan/dullahan/entity/`
4. **DullahanRenderer.java** → `src/main/java/com/dullahan/dullahan/client/renderer/`
5. **ClientEvents.java** → `src/main/java/com/dullahan/dullahan/client/`
6. **ModEntities.java** → `src/main/java/com/dullahan/dullahan/init/`
7. **ModSounds.java** → `src/main/java/com/dullahan/dullahan/init/`

### Step 3: Place Resource Files

1. **mods.toml** → `src/main/resources/META-INF/`
2. **sounds.json** → `src/main/resources/assets/dullahan/`
3. **en_us.json** → `src/main/resources/assets/dullahan/lang/`
4. **dullahan_loot_table.json** → `src/main/resources/data/dullahan/loot_tables/entities/dullahan.json`

### Step 4: Prepare Audio Files

Convert the provided MP3 file to OGG format and split it:

```bash
# Install FFmpeg if you haven't already
# On Ubuntu/Debian: sudo apt-get install ffmpeg
# On macOS: brew install ffmpeg
# On Windows: Download from ffmpeg.org

# Convert to OGG format
ffmpeg -i GHOSTS_Whispers_From_the_Other_Side.mp3 -acodec libvorbis -ar 44100 ambient.ogg

# Create variations for different sounds (optional - adjust pitch/speed)
ffmpeg -i GHOSTS_Whispers_From_the_Other_Side.mp3 -acodec libvorbis -ar 44100 -af "atempo=1.2" hurt.ogg
ffmpeg -i GHOSTS_Whispers_From_the_Other_Side.mp3 -acodec libvorbis -ar 44100 -af "atempo=0.8" death.ogg
ffmpeg -i GHOSTS_Whispers_From_the_Other_Side.mp3 -acodec libvorbis -ar 44100 -af "volume=1.5" spawn.ogg
```

Place the OGG files in:
`src/main/resources/assets/dullahan/sounds/entity/dullahan/`

### Step 5: Create Texture

Based on the reference images provided:

1. Open an image editor (GIMP, Photoshop, or Paint.NET)
2. Create a new 64x64 pixel image
3. Use the Minecraft skin format (front, back, sides, top, bottom)
4. Reference the provided images for the cyan glowing head and leather armor
5. Save as `dullahan.png`
6. Place in: `src/main/resources/assets/dullahan/textures/entity/`

**Texture Guidelines:**
- Use cyan/aqua colors (#00FFFF) for the glowing head
- Dark leather colors for armor (#8B4513, #654321)
- Add transparency for smoother edges
- Follow Minecraft's 64x64 player skin layout

### Step 6: Set Up Gradle

Create `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx3G
org.gradle.daemon=false
```

Create `settings.gradle`:
```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = 'https://maven.minecraftforge.net/' }
    }
}

rootProject.name = 'dullahan'
```

### Step 7: Build the Mod

```bash
# Navigate to the mod directory
cd DullahanMod

# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Set up the workspace
./gradlew setupDecompWorkspace

# Build the mod
./gradlew build
```

The compiled JAR will be in `build/libs/dullahan-1.0.0.jar`

### Step 8: Test the Mod

```bash
# Run Minecraft with the mod
./gradlew runClient
```

Or manually:
1. Copy `build/libs/dullahan-1.0.0.jar` to your Minecraft `mods` folder
2. Launch Minecraft with Forge
3. Create a new world or load an existing one
4. Use `/summon dullahan:dullahan` to spawn the boss

## Attack Pattern Summary

The Dullahan follows this attack sequence:

1. **Upon Spawning**: Waits 0.5 seconds, then creates a 9×9 explosion dealing 2 hearts
2. **Combat Phase**: Alternates between attacks
   - **Attacks 1-2**: Sword strikes (1 heart, close range)
   - **Attack 3**: Lightning summon (3 hearts, up to 64 blocks)
   - Repeats cycle

## Customization Tips

### Modify Damage
Edit values in `DullahanEntity.java`:
- `4.0F` = Spawn explosion (2 hearts)
- `2.0F` = Sword damage (1 heart)
- `6.0F` = Lightning damage (3 hearts)

### Change Health
In `DullahanEntity.createAttributes()`:
```java
.add(Attributes.MAX_HEALTH, 300.0D) // Change this
```

### Adjust Attack Speed
Modify cooldown constants in `DullahanEntity.java`:
```java
private static final int SWORD_ATTACK_COOLDOWN = 40; // 40 ticks = 2 seconds
private static final int LIGHTNING_COOLDOWN = 100; // 100 ticks = 5 seconds
```

### Custom Loot
Edit `dullahan.json` in the loot_tables folder to change drops

## Troubleshooting

### Build Errors
- Ensure Java 17 is installed: `java -version`
- Clean build: `./gradlew clean build`
- Check for typos in package names

### Mod Not Loading
- Verify `mods.toml` is in `META-INF` folder
- Check Forge version compatibility
- Review `latest.log` in `.minecraft/logs/`

### Missing Textures
- Confirm PNG is 64x64 pixels
- Check file path exactly matches code
- Use lowercase in file names

### No Sound
- Ensure OGG format, not MP3
- Verify `sounds.json` syntax
- Check file paths in sounds.json

## Additional Resources

- [Forge Documentation](https://docs.minecraftforge.net/)
- [Minecraft Wiki - Modding](https://minecraft.fandom.com/wiki/Mods)
- [FFmpeg Audio Conversion Guide](https://trac.ffmpeg.org/wiki/Encode/MP3)

## Credits

Created using provided reference materials:
- Character model references
- GHOSTS_Whispers_From_the_Other_Side.mp3 audio file
- Texture references

## License

All Rights Reserved
