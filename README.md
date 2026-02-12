# Dullahan Boss Mod for Minecraft

A custom Minecraft Forge mod that adds the **Dullahan**, a powerful undead boss mob inspired by Irish mythology.

## Installation

### Prerequisites
- Minecraft 1.20.1 (compatible with 1.20.x versions)
- Minecraft Forge 47.0.0 or higher
- Java 17 or higher

### Steps

1. **Install Minecraft Forge**
   - Download Forge installer from [files.minecraftforge.net](https://files.minecraftforge.net)
   - Run the installer and select "Install Client"
   - Launch Minecraft with the Forge profile

2. **Set Up Development Environment** (for building from source)
   ```bash
   # Clone or download the mod files
   # Navigate to the mod directory
   
   # Run Forge setup
   ./gradlew setupDecompWorkspace
   ./gradlew build
   ```

3. **Install the Mod**
   - Copy the built JAR file to your Minecraft `mods` folder
   - Location: `%appdata%/.minecraft/mods/` (Windows) or `~/Library/Application Support/minecraft/mods/` (Mac)

## File Structure

```
src/main/
├── java/com/yourmod/dullahan/
│   ├── DullahanMod.java              # Main mod class
│   ├── entity/
│   │   ├── DullahanEntity.java        # Boss entity logic
│   │   └── DullahanAttackGoal.java    # AI attack patterns
│   ├── client/
│   │   ├── ClientEvents.java          # Client-side events
│   │   └── renderer/
│   │       └── DullahanRenderer.java  # Entity rendering
│   └── init/
│       ├── ModEntities.java           # Entity registration
│       └── ModSounds.java             # Sound registration
│
└── resources/
    ├── META-INF/
    │   └── mods.toml                  # Mod metadata
    ├── assets/dullahan/
    │   ├── sounds.json                # Sound definitions
    │   ├── lang/
    │   │   └── en_us.json            # English translations
    │   ├── sounds/entity/dullahan/
    │   │   ├── ambient.ogg           # Ambient sound
    │   │   ├── hurt.ogg              # Hurt sound
    │   │   ├── death.ogg             # Death sound
    │   │   └── spawn.ogg             # Spawn sound
    │   └── textures/entity/
    │       └── dullahan.png          # Entity texture
    └── data/dullahan/
        └── loot_tables/entities/
            └── dullahan.json          # Loot table
```

## Customization

### Audio Files
Place your custom sound file in:
```
src/main/resources/assets/dullahan/sounds/entity/dullahan/
```

The provided `GHOSTS_Whispers_From_the_Other_Side.mp3` should be converted to `.ogg` format and split into:
- `ambient.ogg` - Background whispers
- `hurt.ogg` - Damage sounds
- `death.ogg` - Death sounds
- `spawn.ogg` - Spawn sounds

**Converting MP3 to OGG:**
```bash
ffmpeg -i GHOSTS_Whispers_From_the_Other_Side.mp3 -acodec libvorbis ambient.ogg
```

### Textures
Create a 64x64 pixel texture based on the reference images:
1. Use the provided reference images as a guide
2. Create a PNG file with Minecraft skin format
3. Place it at: `src/main/resources/assets/dullahan/textures/entity/dullahan.png`

### Adjusting Damage Values
In `DullahanEntity.java`:
```java
// Spawn explosion damage (currently 2 hearts)
player.hurt(this.damageSources().mobAttack(this), 4.0F);

// Sword damage (currently 1 heart)
return target.hurt(this.damageSources().mobAttack(this), 2.0F);

// Lightning damage (currently 3 hearts)
target.hurt(this.damageSources().lightningBolt(), 6.0F);
```

### Adjusting Boss Health
In `DullahanEntity.java`:
```java
.add(Attributes.MAX_HEALTH, 300.0D) // Change this value
```

## Spawning the Dullahan

### Creative Mode
```
/summon dullahan:dullahan ~ ~ ~
```

### Survival Mode
- Craft or obtain a Dullahan spawn egg
- The boss can also spawn naturally in specific biomes (configure in spawn rules)

## Troubleshooting

### Boss Not Spawning
- Check that the mod is loaded: `/mods` command should list "Dullahan Boss Mod"
- Verify file structure matches the expected layout
- Check logs for errors: `.minecraft/logs/latest.log`

### Texture Not Loading
- Ensure texture file is named exactly `dullahan.png`
- Verify it's in the correct directory
- Check that the texture is 64x64 pixels
- Use PNG format with transparency

### Sounds Not Playing
- Convert audio files to OGG Vorbis format
- Ensure files are in correct directory structure
- Check `sounds.json` matches file names
- Verify audio files are mono or stereo (not surround)

## Development

### Building from Source
```bash
./gradlew build
```

Output JAR will be in `build/libs/`

### Testing
```bash
./gradlew runClient
```

## Intended Features

### Boss Characteristics
- **Appearance**: Humanoid figure clad in leather armor with a glowing cyan head
- **Weapon**: Wields a netherite sword
- **Health**: 300 HP (150 hearts)
- **Boss Bar**: Red progress bar displaying health

### Attack Patterns

1. **Spawn Explosion**
   - Triggers 0.5 seconds after spawning
   - 9x9 block radius (doesn't destroy blocks)
   - Deals 2 hearts (4.0 damage) to nearby players

2. **Sword Strikes**
   - Close-range melee attack
   - Deals 1 heart (2.0 damage) per hit
   - 2-second cooldown between attacks

3. **Lightning Summon**
   - Long-range attack (up to 64 blocks)
   - Summons lightning bolt on target
   - Deals 3 hearts (6.0 damage)
   - 5-second cooldown
   - Alternates with sword attacks (every 3rd attack cycle)

### Boss Mechanics
- High knockback resistance (80%)
- Cannot despawn in peaceful mode
- Cannot change dimensions
- Grants 100 XP upon death
- Drops standard mob loot

## Credits

- **Model Reference**: Based on provided Minecraft character designs. [Reference Image No.1](/credits/reference.jpeg)
- **Audio**: Ghost whispers sound effect from free music website. [Utilized Sound Effect](/credits/269008-GHOSTS_Whispers_From_the_Other_Side.mp3)
- **Inspiration**: Irish folklore Dullahan (headless horseman) [Reference Image No.2](/credits/texture.jpeg)

## License

All Rights Reserved

## Support

For issues, questions, or contributions, please refer to your project repository or contact information.

---

**Note**: This mod is designed for Minecraft Forge 1.20.1. Compatibility with other versions may require adjustments to the code.

#### Signed by Ananta the Developer
