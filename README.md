<p align="center">
    <img src="https://raw.githubusercontent.com/hube12/minemap/master/logo.png?sanitize=true"
        height="130">
</p>
<p align="center">
    <a href="https://github.com/hube12/MineMap/blob/master/LICENSE" alt="License">
       <img src="https://img.shields.io/github/license/hube12/Minemap?style=flat-square" />
    </a>
    <a href="https://github.com/hube12/minemap/graphs/contributors" alt="Contributors">
        <img src="https://img.shields.io/github/contributors/hube12/minemap?style=flat-square"/>
    </a>
    <a href="https://github.com/hube12/MineMap/graphs/commit-activity" alt="Activity">
        <img src="https://img.shields.io/github/commit-activity/w/hube12/minemap?style=flat-square"/>
    </a>
    <a href="https://github.com/hube12/MineMap/releases/latest"  alt="Release">
        <img src="https://img.shields.io/github/v/release/hube12/minemap?style=flat-square"/>
    </a>
    <a href="https://github.com/hube12/MineMap/releases/latest"  alt="Release count">
        <img src="https://img.shields.io/github/downloads/hube12/minemap/total?style=flat-square"/>
    </a>
    <a href="https://discord.gg/anHsq24nqt" alt="Discord">
        <img src="https://img.shields.io/discord/813104049737433189?logo=discord&style=flat-square" alt="chat on Discord"/>
    </a>
    <a href="https://twitter.com/intent/follow?screen_name=NeilSeed" alt="Twitter">
        <img src="https://img.shields.io/twitter/follow/NeilSeed?style=social&logo=twitter" alt="follow on Twitter"/>
    </a>
</p>

To download it head to the [Releases section](https://github.com/hube12/MineMap/releases/latest).

This is a program to replace the old amidst with a non Minecraft based one (meaning you can run it without Minecraft
installed), it is also way more efficient since it is fully multithreaded.

There are severals nice features added, you can see per layers of biome generation, find closest structures, draw
circle, area and use a ruler.

Supports all Minecraft release starting from 1.0+

## Demo video

[![Demo Video](http://img.youtube.com/vi/aQo6H_3MXHc/0.jpg)](http://www.youtube.com/watch?v=aQo6H_3MXHc "Minemap demo video")

## Features

- View Biome map for Overworld/Nether/End for all version from 1.0 till 1.16.5.
- Load multiple seeds with a navigation tab to switch between them.
- View structures, and some features placement on the biome map for version 1.8+.
- View chest loot for 1.16.5 structures: Desert Pyramid, Buried Treasure, Ruined Portal and Shipwreck.
- Multithreaded processing
- List the N closest structure and get tp/location to those.
- Draw line, polygon and circle as overlay to the map.
- View proportion of biome in a pie chart.
- Structure seed mode to see all the sister seeds (all seeds sharing the same 48 bottom bits and thus structures)
- Themes, Shortcuts, Biome colors and structure salts are customizable.
- Possibility to take screenshot in app via a button or shortcut.
- View different biomes layers
- View extra infos such as Stronghold portal order, type of structure (Bastion, Shipwreck, Village are currently supported)
- Fully customized icons for structures and features + Mojang ones downloaded for item in chest loot.

### Shortcuts

#### Main controls:
- Ctrl + `N` : New seed
- Ctrl + `S` : Screenshot
- Ctrl + `Q` : Close

#### Optional controls :
- Alt + `A` : Toggle Structure Seed Mode
- Alt + `C` : Change Salts
- Alt + `E` : Open Settings Folder (see [Configuration](#configuration))
- Alt + `G` : Go to Coordinates
- Alt + `K` : Show Shortcuts Menu
- Alt + `L` : Load Shadow Seed
- Alt + `O` : Open Screenshot Folder
- Alt + `P` : Go to Spawn
- Alt + `S` : Go to Structure

#### Zooming in and out:
  - Ctrl + `Numpad +` : Zoom in
  - Ctrl + `Numpad -` : Zoom out
  - Alt + `Numpad +` : Layer +
  - Alt + `Numpad -` : Layer -

In a dialog type `enter` to activate the continue button or `esc` to close the dialog.

Hold `alt` then press H, W, U or E to get one of the 4 menu to open.

You can navigate the menus and press enter to use the button in it.

### Configuration

All configuration can be found in `%HOMEPATH%/.minemap` (Windows) or `$HOME/.minemap` (Mac/Linux)

There is 4 folder here: 
- 

### Command line

- Take a screenshot :
  ```shell
  java -jar Minemap-<version>.jar --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>
  ```

- Don't show any update popup even if there is a update available
  ```shell
  java -jar Minemap-<version>.jar --no-update
  ```


- Update Minemap without a popup
  ```shell
  java -jar Minemap-<version>.jar --update
  ```

## Support and bugs

- This project is still in its enfancy (while packing most of the necessary features) so bug will likely still happen
  inside it.

- However, as it is actively maintained you can get a hold of me on [Discord](https://discord.gg/mn47bWvFjf) or by
  simply submitting a bug report in the [Issues](https://github.com/hube12/MineMap/issues) tab.

- We also have a roadmap of future ideas [here](https://github.com/hube12/MineMap/projects/1) and you are welcome to
  open a discussion [here](https://github.com/hube12/MineMap/discussions) or on [Discord](https://discord.gg/xa6cpSjsqZ)
  to ask for any feature that you deem reasonable enough (be aware for performance issues some might be denied).

## Development

You just need to have the Java JDK installed then do:
`git clone https://github.com/hube12/MineMap`

Go in the directory and run `./gradlew run` to run Minemap (we enable the no-update option)

Run `./gradlew shadowJar` to generate the release jar (modify Minemap.version and gradle.properties MinemapVersion
variables accordingly)

To use vulkan with debug and validation layers please install the vulkan Lunar SDK.

## Contributors

- KaptainWutax : Core part of the map system and libs setup
- Neil : libs enrichment + utilities in Minemap + rich icons
- Uniquepotatoes : Flat icons design
- Speedrunning and monkeys discord ppl : input on feature for Minemap

## Legal mentions

The main core part was done by KaptainWutax.

Any of the work done by Neil is released under MIT. However all materials which are not hand made will be released under
the specific author license, this includes but not limit to some icons and the logo.

All the structures and features icons are released under CC-0 at https://github.com/hube12/mc_icons.

NOT OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.
