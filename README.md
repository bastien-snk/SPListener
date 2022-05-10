# ✉️ SPL (Simple Packet Listener)

SPL is a lightweight, flexible and optimised package listener with a structure and API similar to the one implemented for the Bukkit event listeners.

## 🏷️ Features

- A simple packet listening system
- Optimised flow management
- Compatible with all versions of minecraft (1.7 -> 1.18.2)
- Supports packet cancellation
- Supports input and output packets
- Multi-packet listener

## 📂 Installation

### Maven

Soon...
### Gradle

Soon...

### Manual

Copy all the classes contained in the `dev.fls.spl` package in your plugin.


## 📐 Usage

### 🔒 Register SPL

Before being able to listen to the packages, you will need to register your plugin by adding `SPListener.register(this);` in the `onEnable()` method of your plugin:

```java
SPListener.register(this);
```

### 🏗️ Creating a PacketListener class

Now you need to create a class that implements the PacketListener interface so that SPL can identify this class as containing packet listeners.

```java
package dev.fls.spl;

import dev.fls.spl.PacketListener;

public class ExampleListener implements PacketListener {
}
```

### 🆕 Initializing the PacketListener classes

Now we need to instantiate this class and list it as a packet listener so that methods that are packet listeners can be called.

```java
package dev.fls.spl;

import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SPListener.register(this);


        ExampleListener listener = new ExampleListener();
        SPListener.registerListener(listener);
    }
}
```

### 🎧 Adding packet listeners to classes done for

Now that we have added everything to the packet listener registry. We can add our own listeners to listen for packets and perform actions.

```java
package dev.fls.spl;

import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;

public class ExampleListener implements PacketListener {

    /*
     * Here whe add the @PacketHandler annotation to tell that this methods listen a packet.
     * We pass as an argument to the annotation the packet variable which contains the packet(s) we want to listen to with reference to their class.
     * Here I have chosen to listen to the packet sent when a player places a block (PacketPlayInBlockPlace)
     *
     * Then we add as an argument to the method a PacketEvent which contains the event data that is called when a packet is read. 
     * In order to be able to process and modify the packet and event data.
     */
    @PacketHandler(packet = {PacketPlayInBlockPlace.class})
    public void onBlockPlace(PacketEvent event) {
        // Add your code
    }
}
```

🎉 There you go, now you can listen to any packet and make the changes you want!