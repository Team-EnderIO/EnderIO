---
navigation:
  title: Wireless Charger
  icon: wireless_charger
  parent: machines.md
item_ids:
  - wireless_charger
  - wireless_charger_antenna
  - wireless_charger_antenna_advanced
---

# Wireless Charger

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="wireless_charger" p:powered="true"/>
    </GameScene>
    <Recipe id="wireless_charger" />
  </Row>
</Column>

The wireless charger charges all items inside of players inventories.

## (Advanced) Wireless Charger Antenna

The (advanced) wireless charger antenna can increase the range of the wireless charger by placing it ontop of the wireless charger.

<Column alignItems="center" fullWidth={true}>
  <Row alignItems="center">
    <GameScene zoom="4">
      <Block id="wireless_charger_antenna" y="1"/>
      <Block id="wireless_charger" p:powered="true"/>
    </GameScene>
    <Recipe id="wireless_charger_antenna" />
  </Row>

  <Row alignItems="center">
    <GameScene zoom="4">
      <Block id="wireless_charger_antenna_advanced" y="1"/>
      <Block id="wireless_charger" p:powered="true"/>
    </GameScene>
    <Recipe id="wireless_charger_antenna_advanced" />
  </Row>
</Column>
