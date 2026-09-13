---
navigation:
  title: Fluid Tank
  icon: fluid_tank
  parent: machines.md
item_ids:
  - fluid_tank
  - pressurized_fluid_tank
---

# (Pressurized) Fluid Tank

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="fluid_tank" />
    </GameScene>
    <Recipe id="fluid_tank" />
  </Row>

  <Row >
    <GameScene zoom="4">
      <Block id="pressurized_fluid_tank" />
    </GameScene>
    <Recipe id="pressurized_fluid_tank" />
  </Row>
</Column>

Fluid tanks storage blocks for fluids. They also have the ability to empty and fill other fluid containers in the gui. 
Additionally, it can do certain special recipes like using XP Juice to repair items with Mending.
