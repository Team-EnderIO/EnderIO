# Changelog

## [Unreleased]

### Added
- Wired Charger

### Changed
- Reusing lang keys for different types of glass, should reduce burden on translators.

### Removed

### Fixed

## [6.0.7-alpha] - 2023-07-25

### Changed
- Changed the input locking system to allow quick swapping stacks.

### Removed
- An old piece of broken code that was supposed to already be gone due to its issues

### Fixed
- Fixed using the alloy smelter for normal smelting.
- Fixed the primitive alloy smelter crash.
- Fixed the crash related to the removed portion of code.

## 6.0.6-alpha - 2023-07-24

### Added
- Recipes for the capacitor banks.
- IO config widget insets to support non-standard gui shapes.
- You can now repaint painted blocks in the painting machine!
- Added tooltips to Conduit round-robin and self feed buttons
- You can now right click on a machine to insert a capacitor.
- Recipe for the painting machine.
- The Enderman Head block, can be acquired using The Ender.
- Added recipe for the ender resonator.
- Added recipes for SAG Milling stone to cobblestone and gravel to sand.
- Added recipe for SAG Milling copper ore into powder.
- Added recipes to smelt vanilla powders into ingots.
- Added support for NBT on outputs for alloy smelting.

### Changed
- Gave CheckBox widgets the ability to have tooltips
- Conduits backend and API received some tweaks and cleaning ready for some new functionality.
- Recipe locking is now more intelligent and should result in less friction for players.

### Fixed
- Fixed Soul Binder tooltip when IOConfig is open.
- Properly fixed bucketed fuel in the primitive alloy smelter.
- Fix keepInventory gamerule not being respected properly.
- Remove some old item capability logic, fixing AE2 capacitor autocrafting in the process.
- Fix game crash when running in Turkish locale.
- Painted blocks will now drop correctly.
- Fixed problems with conduits needing updating to work.
- Fixed conduit channels for redstone conduits.
- Fixed using FTB Ultimine on conduits.
- Fixed conduit not displaying disconnection from GUI.
- Fixed incorrect GUI showing when accessing a connected conduit.
- Fixed redstone conduit resetting when a piston is activated.
- Fix JEI showing smelting recipes for the primitive alloy smelter.
- Primtive alloy smelter shift r-click works as expected.
- Fixed slot problems for the Alloy Smelter.
- Fixed recipe locking, resulting in abnormal slot behaviours.

## 6.0.5-alpha - 2023-07-11

### Fixed
- Made information for grinding up coal with flint and obsidian/grindstone more clear.

## 6.0.4-alpha - 2023-07-11

### Added
- Added missing tooltips for IO Config button and Neighbour button.
- Better multismelting support for the alloy smelter

### Changed
- Grindstone crafting for grains of infinity and powdered coal now uses an in-world craft by right-clicking with flint in the off-hand and the ingredient in the main-hand. Also works with obsidian and crying obsidian (the latter of which has a chance to give a better rate of return on the craft).
- Lowered minimum Forge version to 47.0.42.
- Conduits are now placed in your inventory when shift right clicked with the Yeta Wrench

### Removed
- Removed the grave system
- Removed the unfired urn
- Removed Iron Alloy Block

### Fixed
- Solar panels are no longer blocked by transparent blocks.
- Fixed sugar cane and flower pot sag mill recipes.
- Fixed solar panels not draining energy from their buffer.
- Fixed XP fluid inconsistency.
- Fixed the deletion of a bucket when using bucketed fuel in a smelting machine.
- Fixed inability to add fuel to active primitive alloy smelter.
- Fixed the output calculations for vanilla smelting in the alloy smelter.
- Fixed IO Config Overlay text rendering. Text has a Z-offset now.
- Fixed negative scale for IO Config for multi blocks
- Fixed Primitive Alloy Smelter not serializing burn time
- Fixed linking between conduit connector shape and selected conduit.
- Fixed redstone control icons, consistent with 1.12 now.

## 6.0.3-alpha - 2023-07-08

### Fixed
- Fixed grinding balls on multiplayer
- Fixed a GUI related issue

## 6.0.2-alpha - 2023-07-08

### Changed
- Powdered coal obtainable early game.

### Fixed
- A SAG Mill crash
- Fluid Tank Tooltips

## 6.0.1-alpha - 2023-07-07

### Added
- Initial release
