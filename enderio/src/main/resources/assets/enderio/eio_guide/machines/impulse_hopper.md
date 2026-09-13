---
navigation:
  title: Impulse Hopper
  icon: impulse_hopper
  parent: machines.md
item_ids:
  - impulse_hopper
---

# Impulse Hopper

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="impulse_hopper" p:powered="true"/>
    </GameScene>
    <Recipe id="impulse_hopper" />
  </Row>
</Column>

The Impulse hopper is an item flow control machine. It will hold on to all items its receives until it has the correct number specified in the gui. 
This was the Impulse hopper acts as a buffer that will always let the same items pass as set in the gui. 
