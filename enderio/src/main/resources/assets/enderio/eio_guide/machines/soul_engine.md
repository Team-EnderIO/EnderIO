---
navigation:
  title: Soul Engine
  icon: soul_engine
  parent: machines.md
item_ids:
  - soul_engine
---

# Soul Engine

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="soul_engine" p:powered="true"/>
    </GameScene>
    <Recipe id="soul_engine" />
  </Row>
</Column>

The soul engine produces energy by consuming liquids. The liquid consumed and energy generated per bucket depends on the type of soul bound to the machine in the <ItemLink id="soul_binder" />.
The speed and storage capacity for the energy depend on the <ItemImage id="basic_capacitor" /> [capacitor](../misc/capacitors.md).
