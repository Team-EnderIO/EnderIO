#noload

//<recipetype:enderio:slicing>.addRecipe(name as string, output as ItemDefinition, inputs as IIngredient[], energy as int);
//errors out if more than 6 inputs given

<recipetype:enderio:slicing>.addRecipe("tnt", <item:minecraft:tnt>, [<item:minecraft:sand>, <item:minecraft:gunpowder>, <item:minecraft:sand>, <item:minecraft:gunpowder>, <item:minecraft:sand>, <item:minecraft:gunpowder>], 100);
