# Spigot Reinforcement Plugin
A Spigot Reinforcement Plugin for 1.13 inspired by Civcraft. You can reinforce after enabling the mode using `/reinforce` and right clicking on the blocks.

# How it works

There are three levels of reinforcement: stone (250), iron (750), or diamond (1800). Type `/reinforce` to toggle reinforcement mode and left click to determine the remaining strength of the block being hit. Right clicking while holding one of the reinforcement materials in your main hand will reinforce a block instead.

Note that you cannot damage blocks while in reinforcement mode.

# File format
The `.srpdata` file is structured as a series of `[x y z strength group_id mat_id dimension]` structs encoded in binary taking up 18 bytes of space per block to denote reinforcements. One file exists for the entire server.

I did not use any databases at all since since it would have performed worse, and creating a basic file format to store block reinforcement data is all you need. At some point the server needs to load all the blocks into memory at once,	or store data according to chunk. 100,000 blocks reinforced only takes up 1.72mb of space so it'd take roughly 59.5 million blocks to take up a gigabyte. The main problem lies in the time it takes to load all this data off disk and into memory. I do not imagine that this many reinforcements will exist.

I'm not so sure that each Reinforcement object loaded in from memory is in fact 18 bytes, but we'll have to make do.. :(
