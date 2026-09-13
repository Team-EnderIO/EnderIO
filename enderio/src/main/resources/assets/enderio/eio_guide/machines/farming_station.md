---
navigation:
  title: Farming Station
  icon: farming_station
  parent: machines.md
item_ids:
  - farming_station
---

# Farming Station

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="farming_station" p:powered="true"/>
    </GameScene>
    <Recipe id="farming_station" />
  </Row>
</Column>

The farming station allows for planting and farming crops, sugar cane, trees etc... The farming station has for zones that can be used to plant different crops.
The farming station needs to right tools to till the ground and break the crops, otherwise it will skip these positions. 
The farming station can optionally use bonemeal to speed up the growth of crops. 

The farming station uses energy to farm, with the speed being determined by the <ItemImage id="basic_capacitor" /> [capacitor](../misc/capacitors.md). 
The farming station can also have a soul bound to it in the <ItemLink id="soul_binder" />. 
A Bee soul will increase the bonemeal efficiency, a villager will increase the crop output while a sniffer will increase the amount of seeds obtained.
