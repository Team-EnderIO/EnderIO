---
navigation:
  title: Fluid Conduits
  icon: conduit
  icon_components: 
    "enderio:conduit": "enderio:fluid" # currently broken, not sure if this will be fixed?
  parent: conduits.md
---

# Fluid Conduits

<Column alignItems="center" fullWidth={true}>
  <Recipe id="fluid_conduit" />
</Column>

<Column alignItems="center" fullWidth={true}>
  <Row >
    <Recipe id="pressurized_fluid_conduit" />
    <Recipe id="pressurized_fluid_conduit_upgrade" />
  </Row>
</Column>

<Column alignItems="center" fullWidth={true}>
  <Row >
    <Recipe id="ender_fluid" />
    <Recipe id="ender_fluid_conduit_upgrade" />
  </Row>
</Column>

<ItemLink id="conduit" components="enderio:conduit='enderio:fluid'"/>'s are conduits that transport fluid. The speed will depend on the tier used.
<ItemLink id="basic_fluid_filter"/>'s can be used to filter which fluids can go in or out the conduits.
Additionally, redstone can be used to block or activate connections
