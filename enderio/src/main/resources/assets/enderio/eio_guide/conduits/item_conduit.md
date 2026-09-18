---
navigation:
  title: Item Conduits
  icon: conduit
  icon_components: 
    "enderio:conduit": "enderio:item" # currently broken, not sure if this will be fixed?
  parent: conduits.md
---

# Item Conduits

<Column alignItems="center" fullWidth={true}>
  <Recipe id="item_conduit" />
</Column>

<Column alignItems="center" fullWidth={true}>
  <Row >
    <Recipe id="energetic_item_conduit" />
    <Recipe id="energetic_item_conduit_upgrade" />
  </Row>
</Column>

<Column alignItems="center" fullWidth={true}>
  <Row >
    <Recipe id="vibrant_item_conduit" />
    <Recipe id="vibrant_item_conduit_upgrade" />
  </Row>
</Column>

<ItemLink id="conduit" components="enderio:conduit='enderio:item'"/>'s are conduits that transport items. The speed will depend on the tier used.
<ItemLink id="basic_item_filter"/>'s can be used to filter which items can go in or out the conduits.
Additionally, redstone can be used to block or activate connections
