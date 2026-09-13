---
navigation:
  title: SAG Mill
  icon: sag_mill
  parent: machines.md
item_ids:
  - sag_mill
---

# SAG Mill

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="sag_mill" p:powered="true"/>
    </GameScene>
    <Recipe id="sag_mill" />
  </Row>
</Column>

The SAQ Mill is a machine that breaks down resources. Most resources have a guaranteed primary result, while it is also possible to obtain an optional secondary result.
The machine uses energy and the speed is determined by the <ItemImage id="basic_capacitor" /> [capacitor](../misc/capacitors.md).
By using a <ItemImage id="dark_steel_grinding_ball" /> [Grinding Ball](../misc/grinding_ball.md), the energy used as well as the primary and secondary output amounts can be modified.
