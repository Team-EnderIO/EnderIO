---
navigation:
  title: Photovoltaic Modules
  icon: energetic_photovoltaic_module
  parent: machines.md
item_ids:
    - energetic_photovoltaic_module
    - pulsating_photovoltaic_module
    - vibrant_photovoltaic_module
---

# Photovoltaic Modules

<Column alignItems="center" fullWidth={true}>
  <Row >
    <GameScene zoom="4">
      <Block id="energetic_photovoltaic_module" />
    </GameScene>
    <Recipe id="energetic_photovoltaic_module" />
  </Row>

  <Row >
    <GameScene zoom="4">
      <Block id="pulsating_photovoltaic_module" />
    </GameScene>
    <Recipe id="pulsating_photovoltaic_module" />
  </Row>

  <Row >
    <GameScene zoom="4">
      <Block id="vibrant_photovoltaic_module" />
    </GameScene>
    <Recipe id="vibrant_photovoltaic_module" />
  </Row>
</Column>

Photovoltaic modules are able to generate energy by using the sun. The exact amount of energy depends on the tier of the module as well as the time of day. 
It is possible to make a module work the entire day by placing <ItemLink id="liquid_sunshine_bucket" /> next to it. 
It is also possible to bind a phantom soul to the module in the <ItemLink id="soul_binder" />, making the module work at night instead of during the day.
In this case, using <ItemLink id="liquid_darkness_bucket" /> next to the module will always make it work at full strength. 
