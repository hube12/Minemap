# MineMap

![Minemap logo](logo.png?raw=true "Minemap")

To download it head to the [Releases section](https://github.com/hube12/MineMap/releases/latest).

This is a program to replace the old amidst with a non Minecraft based one (meaning you can run it without Minecraft
installed), it is also way more efficient since it is fully multithreaded.

There are severals nice features added, you can see per layers of biome generation, find closest structures, draw circle,
area and use a ruler.


Supports all Minecraft release starting from 1.0+

## Development

You just need to have the Java JDK installed then do:
`git clone https://github.com/hube12/MineMap`

Go in the directory and run `./gradlew run` to run Minemap (we enable the no-update option)

Run `./gradlew shadowJar` to generate the release jar (modify Minemap.version and gradle.properties MinemapVersion variables accordingly)

To use vulkan with debug and validation layers please install the vulkan Lunar SDK.

## Contributors
- KaptainWutax : Core part of the map system and libs setup
- Neil : libs enrichment + utilities in Minemap + rich icons
- Uniquepotatoes : Flat icons design
- Speedrunning and monkeys discord ppl : input on feature for Minemap


## Legal mentions
The main core part was done by KaptainWutax.

Any of the work done by Neil is released under MIT.
However all materials which are not hand made will be released under the specific author license,
this includes but not limit to some icons and the logo.

All the structures and features icons are released under CC-0 at https://github.com/hube12/mc_icons.


NOT OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.
