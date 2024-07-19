#noload

//<recipetype:enderio:soul_binding>.addRecipe(name as string, output as ItemDefinition, input as IIngredient, energy as int, experience as int, type as EntityType);
<recipetype:enderio:soul_binding>.addRecipe("totem_from_gold", <item:minecraft:totem_of_undying>, <tag:item:c:storage_blocks/gold>, 10000, 1, <entitytype:minecraft:evoker>);

//<recipetype:enderio:soul_binding>.addRecipe(name as string, output as ItemDefinition, input as IIngredient, energy as int, experience as int, type as MobCategory);
<recipetype:enderio:soul_binding>.addRecipe("grass_from_dirt", <item:minecraft:grass_block>, <item:minecraft:dirt>, 10000, 1, <constant:minecraft:mobcategory:creature>);

//<recipetype:enderio:soul_binding>.addRecipe(name as string, output as ItemDefinition, input as IIngredient, energy as int, experience as int, soulData as string);
