# SwingGraph
A graph editor designed to aid with simple script writing

## Features
* Node-based scene graph
* Properties can be attached to pins, propagating the data to those internally
* Node traversal functions
* Runtime API
  * External runtime support
  * Allows for additional runtimes to be included
  * Runtimes can provide:
    * A Graph Compiler, able to compile the node chain into a script or program
    * A Node library, exposing additional nodes to the graph editor
  * Sample runtimes:
    * Audio runtime, provides a node to play an audio clip
    * Functions runtime, provides a set of basic functions, as well as a reflection node, allowing for method references to be exposed as nodes
    * GLSL runtime, provides a set of nodes that map to GL functions, such as texture sampling, and provides the compiler capable of converting GL nodes to a GLSL shader
    * Hue runtime, provides nodes capable of controlling the [ArduinoRGBHue](https://github.com/ncguy2/ArduinoRGBHue) script, also provides a compiler that compiles the nodes into individual instructions, then sends them to the connected arduino host
    
