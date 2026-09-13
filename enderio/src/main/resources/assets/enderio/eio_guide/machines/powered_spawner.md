---
navigation:
  title: Powered Spawner
  icon: powered_spawner
  parent: machines.md
item_ids:
  - powered_spawner
  - mind_killer
---

# Powered Spawner

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="powered_spawner" p:powered="true"/>
    </GameScene>
    <Recipe id="powered_spawner" />
  </Row>
</Column>

The powered spawner is able to spawn mobs based on which soul has been bound to the powered spawner in the <ItemLink id="soul_binder" />. 
The powered spawner operates using energy, and the amount needed to spawn the mob will depend on the specific mob bound.
The speed at which the mob gets spawned depends on the <ItemImage id="basic_capacitor" /> [capacitor](../misc/capacitors.md) installed.

Besides spawing mobs, the spawner can also be but on "Capturing", which will use <ItemLink id="soul_vial" />'s to capture the mob directly and return a filled vial.

## Mind Killer

<Column alignItems="center" fullWidth={true}>
  <Row alignItems="center">
    <GameScene zoom="4">
      <Block id="mind_killer" y="1"/>
      <Block id="powered_spawner" p:powered="true"/>
    </GameScene>
    <Recipe id="mind_killer" />
  </Row>
</Column>

The mind killer is an in-world-upgrade for the powered spawner. By placing it on top of the spawner, the spawned mobs will have no AI and will not move or fight back.
