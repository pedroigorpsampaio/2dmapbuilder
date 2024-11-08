A 2D tile-based map builder implemented using Swing (Java)

# About 2DMapBuilder

2DMapBuilder is a 2D map editor that helps you develop the scenario for your 2D games. Its primary focus is to aid map designers in tile map editing, as well as sprites and map objects arranging.

It supports square shaped tile maps, tilesets importing and layers management. A tileset can be of any image type and can contain one or many tiles. Layers separates the content of the map giving the ability to stack objects in a 2D world. A tile has always a layer and tiles of inferior layers are rendered behind of those of higher layers. In 2DMapBuilder, layers are automatically created and tiles are automatically set to their respective layer, but it is also possible to manage them manually.

To create your map, you'll have at your disposal several tools designed to help the efficiency of map creation. The primary tool for painting is the Brush Tool that allows you to paint a tile or a group of tiles into the map, accepting well-known modificators of selection Ctrl-Click and Shift-Click, as well as drag-and-drop selection. The Eraser Tool will help you deleting single tiles of your map shifting automatically between layers. Finally, the Select Tool will give you the skill of cutting, copying, pasting and deleting a tile or a group of tiles in the selected layer from the map.

In order to give you more control, 2DMapBuilder provides a state mechanism that allows you to go back and forth in your map modifications, the famous undo and redo operations.

To facilitate the parsing of the created map file, 2DMapBuilder's own file extension .m2d was projected using XML notation and uses a simple formatting for the map data.
No headaches for using the generated map in your games!

# Wiki

[https://bitbucket.org/KikoSampaio/2dmapbuilder/wiki/Home](https://bitbucket.org/KikoSampaio/2dmapbuilder/wiki/Home)

# Screenshots

![teaser](https://pedroigorpsampaio.github.io/src/img/2dmb/media_2dmb0.png)

![teaser](https://pedroigorpsampaio.github.io/src/img/2dmb/media_2dmb2.png)

