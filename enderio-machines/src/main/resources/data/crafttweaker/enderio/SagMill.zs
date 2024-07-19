#noload

//<recipetype:enderio:sag_milling>.addRecipe(name as string, outputs as Percentaged<IItemstack>[], input as IIngredient, energy as int, bonusType as BonusType);

//no grinding ball bonus
<recipetype:enderio:sag_milling>.addRecipe("bedrock_to_grains_of_infinity", [<item:enderio:grains_of_infinity>], <item:minecraft:bedrock>, 1000);

//grinding ball bonus increases drop chance
<recipetype:enderio:sag_milling>.addRecipe("seeds_from_grass", [<item:minecraft:wheat_seeds> % 12, <item:minecraft:pumpkin_seeds> % 6, <item:minecraft:melon_seeds> % 6, <item:minecraft:beetroot_seeds> % 8], <item:minecraft:short_grass>, 1000, <constant:enderio:bonus_type:chance_only>);

//grinding ball bonus increases amount given
<recipetype:enderio:sag_milling>.addRecipe("sawdust_from_logs", [<item:mekanism:sawdust> * 2], <tag:item:minecraft:logs>, 1000, <constant:enderio:bonus_type:chance_only>);
