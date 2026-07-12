# ⚡ Z-Optimization

**A surgical, client-side performance engine for modern Minecraft.**

Z-Optimization doesn't just lower your render distance; it fundamentally rewrites how the Minecraft client handles network data, mathematical rendering, and particle spam. Designed specifically for the 26.1+ rendering engine, it eliminates CPU bottlenecks in heavily populated multiplayer servers and hubs.

---

## 🚀 Core Features

### 📡 Distance-Based Network Culling (Fast Lerp)
In vanilla Minecraft, your CPU calculates complex 3D math (Linear Interpolation) to smoothly animate every single player and mob in the server, even if they are a hundred blocks away.
* **The Fix:** Z-Optimization intercepts the network engine. If an entity is more than 24 blocks away, the heavy math is completely bypassed, and their position is instantly snapped to the server's coordinates.
* **The Result:** Massive CPU relief in crowded lobbies (Hypixel, 2b2t) with zero noticeable visual stuttering.

### 🌑 Zero-Cost Shadow Culling
The 26.1+ rendering engine rewrite made entity shadows surprisingly heavy. Instead of fighting the render pipeline, this mod intercepts the shadow radius math before the GPU even sees it.
* **The Fix:** When Fast Shadows are enabled, the game forces the shadow radius of all entities to `0.0F`.
* **The Result:** The game's rendering pipeline skips the shadow entirely, saving your CPU from attempting to process hundreds of ground gradients.

### ✨ Dynamic Particle Budgeting
Crystal spam, lag machines, and massive splash potion fights can easily freeze a client by attempting to render thousands of particles in a single tick.
* **The Fix:** Z-Optimization introduces a hard, per-tick particle cap (150 particles/tick). It intercepts the `ParticleEngine` at the start of every tick, counts the spawn requests, and ruthlessly deletes any overflow.
* **The Result:** Complete immunity to particle-based lag machines and frame-drops during intense PvP.

### 🧮 Fast Math Engine
*(Optional: Bypasses standard Java Math libraries in favor of hardware-accelerated Sine/Cosine and game-dev optimized Atan2 calculations for faster entity rotation processing).*

---

## ⚙️ Configuration
Z-Optimization features a fully integrated in-game settings menu. You don't need to dig through text files to change your performance profile.
*(Requires Mod Menu to view the menu).*

* **Fast Math:** Toggle the Lerp network bypass and math optimizations.
* **Render Entity Shadows:** Instantly vaporize or restore mob shadows.
* **Particle Budget:** Toggle the hard-cap on particle rendering.

---

## 📥 Installation

**Required Dependencies:**
* [Fabric API](https://modrinth.com/mod/fabric-api)

**Compatibility:**
* Completely **Client-Side**. You do not need to install this on the server.
* Safe to use on vanilla servers, anti-cheat servers, and modded networks.

---

### License
This project is licensed under the terms defined in the [LICENSE](LICENSE) file.

## 🛠️ For Developers (Building from Source)

Want to see how the engine works under the hood?
1. Clone the repository: `git clone https://github.com/TDHunterrr/z-optimization.git`
2. Navigate to the folder: `cd z-optimization`
3. Build the mod: `./gradlew build`
4. Find the compiled `.jar` inside `build/libs/`

---
*Built with speed in mind.*