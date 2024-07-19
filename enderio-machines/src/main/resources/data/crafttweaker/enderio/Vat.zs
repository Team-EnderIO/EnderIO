#noload

//<recipetype:enderio:vat_fermenting>.addRecipe(name as string, output as IFluidStack, inputFluid as CTFluidIngredient, leftInput as KnownTag<ItemDefinition>, rightInput as KnownTag<ItemDefinition>, time as int);
<recipetype:enderio:vat_fermenting>.addRecipe("water_to_lava", <fluid:minecraft:lava> * 100, <fluid:minecraft:water> * 100, <tag:item:c:netherracks>, <tag:item:minecraft:coals>, 100);
