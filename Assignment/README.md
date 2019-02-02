NOTE
=====
1. Make sure you are using the same version of JDK (javac) and JRE (java)
2. It is recommended to run using NVIDIA GPU (or better)

How to run
====================
Run the Anilamp.bat

Folder structure
====================
- lib - For "libraries" (gmaths, Camera, Light, SGNode etc.)
- models - Models file (CactusPot, Lamp, OutsideScene, PictureFrame, PiggyBank, Room, Table, Window)
- shaders - Shaders file
- shapes - Files for the mesh shape (Cube, Cylinder, FrustumCone, Sphere, TwoTriangles)
- textures - Textures file, license.txt included

| FINAL MARK | 91 | 100 |
| --- | ---: | ---: |
| **Program Code** | | |
| <ul><li><strong>General style:</strong> e.g. use of variables rather than literals to promote flexibility, layout of code, methods not overlong, organised classes; Structure formodels, e.g. use of separate classes/methods to draw parts of scene.Use of scene graph for drawing a parent child hierarchy, use of variables for altering scene graph nodes</li><li><strong>Animation control:</strong> should be a flexible solution, e.g. separate class for pose data and interpolation process</li></ul> | 16 | 20 |
| **Working Program** | | |
| <ul><li><strong>Modelling:</strong> lamp and bulb – it is the hierarchical nature that is important; Floor, wall, desk, objects, window, outside scene (e.g. skybox)</li></ul> | <ul><li>25</li></ul> | <ul><li>25</li></ul> |
| <ul><li><strong>Texture:</strong> Texture mapping on objects; specular and diffuse map on a table object; attention to detail: blockiness, texture seams (where appropriate); changeable window texture - viewpoint, time.</li></ul> | <ul><li>17</li></ul> | <ul><li>20</li></ul> |
| <ul><li><strong>Lighting, camera, interface:</strong> Spotlight from bulb shines in correct direction (should see effect on other objects); Light appearance changes when switched on/off; Global light source(s) to illuminate entire scene, interface controls</li></ul> | <ul><li>15</li></ul> | <ul><li>15</li></ul> |
| <ul><li><strong>Animation:</strong> random poses; jump; smooth animation, timing</li></ul> | <ul><li>18</li></ul> | <ul><li>20</li></ul> |

**Overall**
---

**What is good about this work?**
- Has produced a scene containing all of the required models to a high level of detail.
- The textures look great.
- Scene supports multiple lights, including a functional spotlight.
- Lamp has an appropriate jump animation.
- Good use of classes and methods to manage the complexity of the system.

**What needs to be done to make it better?**
- Scene outside window could have utilised a full skybox.
- You could have included some of the decorations in the animations/poses.
- You could create a separate animation class to make it easier to support flexible animations.